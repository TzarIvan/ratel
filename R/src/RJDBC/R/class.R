##=== JDBCDriver

setClass("JDBCDriver", representation("DBIDriver", identifier.quote="character", jdrv="jobjRef"))




JDBC <- function(driverClass='', classPath='', identifier.quote=NA) {
  .jinit(classPath)
  if (nchar(driverClass) && is.jnull(.jfindClass(as.character(driverClass)[1])))
    stop("Cannot find JDBC driver class ",driverClass)
  jdrv <- .jnew(driverClass, check=FALSE)
  .jcheck(TRUE)
  if (is.jnull(jdrv)) jdrv <- .jnull()
  new("JDBCDriver", identifier.quote=as.character(identifier.quote), jdrv=jdrv)
}

.verify.JDBC.result <- function (result, ...) {
  if (is.jnull(result)) {
    x <- .jgetEx(TRUE)
    if (is.jnull(x))
      stop(...)
    else
      stop(...," (",.jcall(x, "S", "getMessage"),")")
  }
}

setMethod("dbListConnections", "JDBCDriver", def=function(drv, ...) { warning("JDBC driver maintains no list of acitve connections."); list() })

setMethod("dbGetInfo", "JDBCDriver", def=function(dbObj, ...)
  list(name="JDBC", driver.version="0.1-1",
       DBI.version="0.1-1",
       client.version=NA,
       max.connections=NA)
          )

setMethod("dbUnloadDriver", "JDBCDriver", def=function(drv, ...) NULL)

setMethod("dbConnect", "JDBCDriver", def=function(drv, url, user='', password='', ...) {
  jc <- .jcall("java/sql/DriverManager","Ljava/sql/Connection;","getConnection", as.character(url)[1], as.character(user)[1], as.character(password)[1], check=FALSE)
  if (is.jnull(jc) || !is.jnull(drv@jdrv)) {
    # ok one reason for this to fail is its interaction with rJava's
    # class loader. In that case we try to load the driver directly.
    oex <- .jgetEx(TRUE)
    p <- .jnew("java/util/Properties")
    if (length(user)==1 && nchar(user)) .jcall(p,"Ljava/lang/Object;","setProperty","user",user)
    if (length(password)==1 && nchar(password)) .jcall(p,"Ljava/lang/Object;","setProperty","password",password)
    jc <- .jcall(drv@jdrv, "Ljava/sql/Connection;", "connect", as.character(url)[1], p)
  }
  .verify.JDBC.result(jc, "Unable to connect JDBC to ",url)
  new("JDBCConnection", jc=jc, identifier.quote=drv@identifier.quote)},
          valueClass="JDBCConnection")

### JDBCConnection

setClass("JDBCConnection", representation("DBIConnection", jc="jobjRef", identifier.quote="character"))

setMethod("dbDisconnect", "JDBCConnection", def=function(conn, ...)
          {.jcall(conn@jc, "V", "close"); TRUE})

.fillStatementParameters <- function(s, l) {
  for (i in 1:length(l)) {
    if (is.integer(l[[i]]))
      .jcall(s, "V", "setInteger", i, l[[i]][1])
    else {
      if (is.numeric(l[[i]]))
        .jcall(s, "V", "setDouble", i, as.double(l[[i]])[1])
      else
        .jcall(s, "V", "setString", i, as.character(l[[i]])[1])
    }
  }
}

setMethod("dbSendQuery", signature(conn="JDBCConnection", statement="character"),  def=function(conn, statement, ..., list=NULL) {
  s <- .jcall(conn@jc, "Ljava/sql/PreparedStatement;", "prepareStatement", as.character(statement)[1], check=FALSE)
  .verify.JDBC.result(s, "Unable to execute JDBC statement ",statement)
  if (length(list(...))) .fillStatementParameters(s, list(...))
  if (!is.null(list)) .fillStatementParameters(s, list)
  r <- .jcall(s, "Ljava/sql/ResultSet;", "executeQuery", check=FALSE)
  .verify.JDBC.result(r, "Unable to retrieve JDBC result set for ",statement)
  md <- .jcall(r, "Ljava/sql/ResultSetMetaData;", "getMetaData", check=FALSE)
  .verify.JDBC.result(md, "Unable to retrieve JDBC result set meta data for ",statement, " in dbSendQuery")
  new("JDBCResult", jr=r, md=md)
})

if (is.null(getGeneric("dbSendUpdate"))) setGeneric("dbSendUpdate", function(conn, statement, ...) standardGeneric("dbSendUpdate"))

setMethod("dbSendUpdate",  signature(conn="JDBCConnection", statement="character"),  def=function(conn, statement, ..., list=NULL) {
  s <- .jcall(conn@jc, "Ljava/sql/PreparedStatement;", "prepareStatement", as.character(statement)[1], check=FALSE)
  .verify.JDBC.result(s, "Unable to execute JDBC statement ",statement)
  if (length(list(...))) .fillStatementParameters(s, list(...))
  if (!is.null(list)) .fillStatementParameters(s, list)
  .jcall(s, "I", "executeUpdate", check=FALSE)
  x <- .jgetEx(TRUE)
  if (!is.jnull(x)) stop("execute JDBC update query failed in dbSendUpdate (", .jcall(x, "S", "getMessage"),")")
})

setMethod("dbGetQuery", signature(conn="JDBCConnection", statement="character"),  def=function(conn, statement, ...) {
    r <- dbSendQuery(conn, statement, ...)

    if(Sys.getenv("RJDBC_THROW_TIMEOUT") != "") {
        numTimeOuts <- as.numeric(Sys.getenv("RJDBC_THROW_TIMEOUT"))
        throwTimeOutError <- numTimeOuts > 0
        Sys.setenv(RJDBC_THROW_TIMEOUT=(numTimeOuts - 1))
    } else {
        throwTimeOutError <- FALSE
    }

    if(throwTimeOutError)
        dbDisconnect(conn)
    fetch(r, -1, throwTimeOutError)
})

setMethod("dbGetException", "JDBCConnection",
          def = function(conn, ...) list()
          , valueClass = "list")

setMethod("dbGetInfo", "JDBCConnection",
          def = function(dbObj, ...) list() )

setMethod("dbListResults", "JDBCConnection",
          def = function(conn, ...) { warning("JDBC maintains no list of active results"); NULL }
          )

setMethod("dbListTables", "JDBCConnection", def=function(conn, pattern="%", ...) {
  md <- .jcall(conn@jc, "Ljava/sql/DatabaseMetaData;", "getMetaData", check=FALSE)
  .verify.JDBC.result(md, "Unable to retrieve JDBC database metadata")
  r <- .jcall(md, "Ljava/sql/ResultSet;", "getTables", .jnull("java/lang/String"),
              .jnull("java/lang/String"), pattern, .jnull("[Ljava/lang/String;"), check=FALSE)
  .verify.JDBC.result(r, "Unable to retrieve JDBC tables list")
  ts <- character()
  while (.jcall(r, "Z", "next"))
    ts <- c(ts, .jcall(r, "S", "getString", "TABLE_NAME"))
  .jcall(r, "V", "close")
  ts
})

setMethod("dbExistsTable", "JDBCConnection", def=function(conn, name, ...) (length(dbListTables(conn, name))>0))

setMethod("dbRemoveTable", "JDBCConnection", def=function(conn, name, ...) dbSendUpdate(conn, paste("DROP TABLE", name))==0)

setMethod("dbListFields", "JDBCConnection", def=function(conn, name, pattern="%", ...) {
  md <- .jcall(conn@jc, "Ljava/sql/DatabaseMetaData;", "getMetaData", check=FALSE)
  .verify.JDBC.result(md, "Unable to retrieve JDBC database metadata")
  r <- .jcall(md, "Ljava/sql/ResultSet;", "getColumns", .jnull("java/lang/String"),
              .jnull("java/lang/String"), name, pattern, check=FALSE)
  .verify.JDBC.result(r, "Unable to retrieve JDBC columns list for ",name)
  ts <- character()
  while (.jcall(r, "Z", "next"))
    ts <- c(ts, .jcall(r, "S", "getString", "COLUMN_NAME"))
  .jcall(r, "V", "close")
  ts
})

setMethod("dbReadTable", "JDBCConnection", def=function(conn, name, ...)
          dbGetQuery(conn, paste("SELECT * FROM",.sql.qescape(name,TRUE,conn@identifier.quote))))

setMethod("dbDataType", signature(dbObj="JDBCConnection", obj = "ANY"),
          def = function(dbObj, obj, ...) {
            if (is.integer(obj)) "INTEGER"
            else if (is.numeric(obj)) "DOUBLE PRECISION"
            else "VARCHAR(255)"
          }, valueClass = "character")

.sql.qescape <- function(s, identifier=FALSE, quote="\"") {
  s <- as.character(s)
  if (identifier) {
    vid <- grep("^[A-Za-z]+([A-Za-z0-9_]*)$",s)
    if (length(s[-vid])) {
      if (is.na(quote)) stop("The JDBC connection doesn't support quoted identifiers, but table/column name contains characters that must be quoted (",paste(s[-vid],collapse=','),")")
      s[-vid] <- .sql.qescape(s[-vid], FALSE, quote)
    }
    return(s)
  }
  if (is.na(quote)) quote <- ''
  s <- gsub("\\\\","\\\\\\\\",s)
  if (nchar(quote)) s <- gsub(paste("\\",quote,sep=''),paste("\\\\\\",quote,sep=''),s,perl=TRUE)
  paste(quote,s,quote,sep='')
}

setMethod("dbWriteTable", "JDBCConnection", def=function(conn, name, value, overwrite=TRUE, ...) {
  ac <- .jcall(conn@jc, "Z", "getAutoCommit")
  if (is.vector(value) && !is.list(value)) value <- data.frame(x=value)
  if (length(value)<1) stop("value must have at least one column")
  if (is.null(names(value))) names(value) <- paste("V",1:length(value),sep='')
  if (length(value[[1]])>0) {
    if (!is.data.frame(value)) value <- as.data.frame(value, row.names=1:length(value[[1]]))
  } else {
    if (!is.data.frame(value)) value <- as.data.frame(value)
  }
  fts <- sapply(value, dbDataType, dbObj=conn)
  if (dbExistsTable(conn, name)) {
    if (overwrite) dbRemoveTable(conn, name)
    else stop("Table `",name,"' already exists")
  }
  fdef <- paste(.sql.qescape(names(value), TRUE, conn@identifier.quote),fts,collapse=',')
  qname <- .sql.qescape(name, TRUE, conn@identifier.quote)
  ct <- paste("CREATE TABLE ",qname," (",fdef,");",sep= '')
  if (ac) {
    .jcall(conn@jc, "V", "setAutoCommit", FALSE)
    on.exit(.jcall(conn@jc, "V", "setAutoCommit", ac))
  }
  dbSendUpdate(conn, ct)
  if (length(value[[1]])) {
    inss <- paste("INSERT INTO ",qname," VALUES(", paste(rep("?",length(value)),collapse=','),");",sep='')
    for (j in 1:length(value[[1]]))
      dbSendUpdate(conn, inss, list=as.list(value[j,]))
  }
  if (ac) dbCommit(conn)            
})

setMethod("dbCommit", "JDBCConnection", def=function(conn, ...) {.jcall(conn@jc, "V", "commit"); TRUE})
setMethod("dbRollback", "JDBCConnection", def=function(conn, ...) {
  .jcall(conn@jc, "V", "rollback", check=FALSE)
  handleJavaException("rollback") 
  TRUE
})

##=== JDBCResult

setClass("JDBCResult", representation("DBIResult", jr="jobjRef", md="jobjRef"))

handleJavaException <- function(func.description) {
  exception <- .jgetEx(TRUE)

  # StringWriter w = new StringWriter();
  # e.printStackTrace(new PrintWriter(w));
  # String result = w.toString();

  if (!is.jnull(exception)) {
      w <- .jnew("java/io/StringWriter")
      pw <- .jnew("java/io/PrintWriter", .jcast(w, "java/io/Writer"))
      .jcall(.jcast(exception, "java/lang/Throwable"), "V", "printStackTrace", pw)
      stack.trace <- .jcall(.jcast(w, "java/lang/Object"), "S", "toString")

      stop(func.description," failed (", .jcall(exception, "S", "getMessage"),")\n", stack.trace)
  }
}

setMethod("fetch", signature(res="JDBCResult", n="numeric"), def=function(res, n, throwTimeOutError=FALSE, ...) {
  if (throwTimeOutError) {
    stop("ResultSet::next failed (I/O Error: Read timed out)")
  }
  
  cols <- .jcall(res@md, "I", "getColumnCount")
  if (cols < 1) return(NULL)
  l <- vector("list", cols)
  nrows <- 1
  for (i in 1:cols) {
    ct <- .jcall(res@md, "I", "getColumnType", i)
    if (ct == -5 | ct ==-6 | (ct >= 2 & ct <= 8))
      l[[i]] <- vector("numeric", nrows)
    else
      l[[i]] <- vector("character", nrows)
    names(l)[i] <- .jcall(res@md, "S", "getColumnName", i)
  }

  j <- 0
  while (.jcall(res@jr, "Z", "next", check = FALSE)) {
    j <- j + 1
    if(isDebugSql()) {
      if (j %% 100000 == 0) cat("!")
      else if (j %% 10000 == 0) cat("*")
      else if (j %% 10001 == 0) cat((j %% 100000) %/% 10000)
      else if(j %% 1000 == 0) cat(".")
    }
    if(j == nrows) {
      nrows <- nrows * 4
      for(i in 1:cols) length(l[[i]]) <- nrows
    }
    for (i in 1:cols) {
      if (is.numeric(l[[i]])) {
        l[[i]][j] <- .jcall(res@jr, "D", "getDouble", i)
      } else {
        a <- .jcall(res@jr, "S", "getString", i)
        l[[i]][j] <- ifelse(is.null(a), NA, a)
      }
    }
    if (n > 0 && j >= n) break
  }
  handleJavaException("ResultSet::next")

  for(i in 1:cols) length(l[[i]]) <- j
  if(isDebugSql()) cat("\n")
  if(j)
    as.data.frame(l, row.names=1:j)
  else
    as.data.frame(l)
})

setMethod("dbClearResult", "JDBCResult",
          def = function(res, ...) { jcall(res@jr, "V", "close"); TRUE},
          valueClass = "logical")

setMethod("dbGetInfo", "JDBCResult", def=function(dbObj, ...) list(), valueClass="list")

setMethod("dbColumnInfo", "JDBCResult", def = function(res, ...) {
  cols <- .jcall(res@md, "I", "getColumnCount")
  l <- list(field.name=character(), field.type=character(), data.type=character())
  if (cols < 1) return(as.data.frame(l))
  for (i in 1:cols) {
    l$field.name[i] <- .jcall(res@md, "S", "getColumnName", i)
    l$field.type[i] <- .jcall(res@md, "S", "getColumnTypeName", i)
    ct <- .jcall(res@md, "I", "getColumnType", i)
    l$data.type[i] <- if (ct == -5 | ct ==-6 | (ct >= 2 & ct <= 8)) "numeric" else "character"
  }
  as.data.frame(l, row.names=1:cols)    
},
          valueClass = "data.frame")

