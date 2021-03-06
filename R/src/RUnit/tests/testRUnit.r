##  RUnit : A unit test framework for the R programming language
##  Copyright (C) 2003-2007  Thomas Koenig, Matthias Burger, Klaus Juenemann
##
##  This program is free software; you can redistribute it and/or modify
##  it under the terms of the GNU General Public License as published by
##  the Free Software Foundation; either version 2 of the License, or
##  (at your option) any later version.
##
##  This program is distributed in the hope that it will be useful,
##  but WITHOUT ANY WARRANTY; without even the implied warranty of
##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##  GNU General Public License for more details.
##
##  You should have received a copy of the GNU General Public License
##  along with this program; if not, write to the Free Software
##  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
##
##  $Id: runitRUnit.r,v 1.13 2007/05/16 12:16:47 burgerm Exp $




testRUnit.checkEquals <- function()
{
  ##@bdescr
  ## test case for function checkEquals of class: none
  ##@edescr

  ##  integer
  x <- 1:10
  checkEquals(x, x)
  ##  return value
  checkTrue( checkEquals(x, x))
  namedInt <- 1:10
  names(namedInt) <- letters[namedInt]
  checkEquals(namedInt, namedInt)
  checkEquals(namedInt, x, checkNames=FALSE)
  
  ##  numeric
  checkEquals(9,9)
  checkEquals( numeric(1), numeric(1))
  checkEquals( 0.01, 0.02, tol=0.01)
  tmp <- c(0.01, NA, 0.02, Inf, -Inf, NaN, 1.0)
  checkEquals( tmp, tmp, tol=0.01)
  
  ##  complex
  checkEquals(complex(0), complex(0))
  checkEquals(complex(2), complex(2))
  checkEquals(complex(2, imaginary=1), complex(2, imaginary=1))

  ##  character
  checkEquals( character(1), character(1))

  ##  matrix
  checkEquals( matrix(1, 3,5), matrix(1, 3,5))
  checkEquals( matrix(1, 50000,5), matrix(1, 50000,5),
              "large matrix not identified as equal")

  ##  language
  checkEquals( expression(2), expression(2))
  checkEquals( call("mean", "median"), call("mean", "median"))

  ##  formula
  simpleForm <- x ~ 1
  checkEquals( simpleForm, simpleForm,
              "simple formula not identified as equal")
  compForm <- y ~ x + y + x*y + offset(x)
  checkEquals( compForm, compForm,
              "formula not identified as equal")
  
  ##  factor
  alphaFac <- factor(letters)
  checkEquals( alphaFac, alphaFac,
              "factor not identified as equal")
  
  ##  list
  checkEquals( list(100), list(100))
  checkEquals( list(100), list(100), tol=1)
  alphaList <- seq(along=letters)
  names(alphaList) <- letters
  checkEquals( alphaList, alphaList)
  checkEquals( alphaList, alphaList, checkNames=FALSE)
  ##  example from ?glm
  counts <- c(18,17,15,20,10,20,25,13,12)
  outcome <- gl(3,1,9)
  treatment <- gl(3,3)
  lmFit <- glm(counts ~ outcome + treatment, family=poisson())
  checkEquals( lmFit, lmFit, checkNames=FALSE)
  checkEquals( lmFit, lmFit)
  lmFitUnnamed <- lmFit
  names(lmFitUnnamed) <- NULL
  checkEquals( lmFit, lmFitUnnamed, checkNames=FALSE)
  
  ##  POSIXct
  sysTime <- as.POSIXct(Sys.time())
  checkEquals( sysTime, sysTime)

  ##  raw
  checkEquals( raw(14), raw(14))
  namedRaw <-  as.raw(1:14)
  names(namedRaw) <- letters[1:14]
  checkEquals( namedRaw, namedRaw)

  ##  S4 objects
  if (identical(TRUE, require(methods))) {
    setClass("track1",
             representation(x="numeric", y="numeric"),
             where=.GlobalEnv)
    on.exit(removeClass("track1", where=.GlobalEnv))
    
    s4Obj <- try(new("track1"))
    s4Obj@x <- 1:10
    s4Obj@y <- 10:1
    checkEquals( s4Obj, s4Obj)

    ##  S4 class containing S4 class slot
    setClass("trackPair",
             representation(trackx = "track1",
                            tracky = "track1"),
             where=.GlobalEnv)
    
    on.exit(removeClass("trackPair", where=.GlobalEnv), add=TRUE)

    tPair <- new("trackPair")
    tPair@trackx <- s4Obj
    tPair@tracky <- s4Obj
    checkEquals( tPair, tPair)
  }

  
  ##  exception handling
  shouldBomb( checkEquals(1 , 1, tol=FALSE))
  shouldBomb( checkEquals(1 , 1, tol=numeric(0)))
  shouldBomb( checkEquals(1 , 1, tol=numeric(2)))

  ##  integer
  namedInt <- 1:9
  names(namedInt) <- letters[namedInt]
  shouldBomb( checkEquals( namedInt, 1:9))
  
  ##  numeric
  shouldBomb( checkEquals( 8, 9))
  shouldBomb( checkEquals( 0.01, 0.02, tol=0.009))
  
  ##  complex
  shouldBomb( checkEquals(complex(0), complex(1)))
  shouldBomb( checkEquals(complex(1), complex(2)))
  shouldBomb( checkEquals(complex(2, imaginary=1), complex(2, imaginary=0)))
  shouldBomb( checkEquals(complex(2, real=1, imaginary=1), complex(2, real=1, imaginary=0)))
  shouldBomb( checkEquals(complex(2, real=1, imaginary=1), complex(2, real=0, imaginary=1)))
  shouldBomb( checkEquals(complex(2, real=1, imaginary=1), complex(2, real=0, imaginary=0)))

  ##  character
  named <- character(1)
  names(named) <- "name"
  shouldBomb( checkEquals( character(1), named))

  ##  formula
  shouldBomb( checkEquals( lmFit, lmFitUnnamed))
  lmFitInter <- glm(counts ~ outcome * treatment, family=poisson())
  shouldBomb( checkEquals( lmFitInter, lmFit))
  
  ##  factor
  alphaFacRecoded <- factor(alphaFac, labels=as.character(seq(along=levels(alphaFac))))
  shouldBomb( checkEquals(alphaFacRecoded, alphaFac))
  
  ##  list
  shouldBomb( checkEquals( list(1), list("1"=1)))
  shouldBomb( checkEquals( list(), list("1"=1)))
  shouldBomb( checkEquals( list(list(), list(list()), list(list(list()))),
                              list(list(), list(list()), list(list(list(), list())))))


  ##  POSIXct
  shouldBomb( checkEquals(as.POSIXct(Sys.time()), as.POSIXct("2007-04-04 16:00:00")))
  shouldBomb( checkEquals(as.POSIXlt(Sys.time()), as.POSIXlt("2007-04-04 16:00:00")))
  
  ##  nested type not supported
  sysTime <- as.POSIXct(Sys.time())
  shouldBomb( checkEquals( list(a=2, list(time=sysTime)), list(a=2, time=list(sysTime))))

  ##  raw
  shouldBomb( checkEquals(raw(1), raw(2)))
  shouldBomb( checkEquals(raw(1E5), raw(100001)))
  
  shouldBomb( checkEquals(as.raw(1:1000), as.raw(c(1:99,-1,101:1000))))

  ##  S4 objects
  if (identical(TRUE, require(methods))) {
    ##  class defined above
    s4Obj <- new("track1")
    s4Obj@x <- 1:10
    shouldBomb( checkEquals( s4Obj, new("track1")))

    tPair <- new("trackPair")
    tPair@trackx <- s4Obj
    shouldBomb( checkEquals( tPair, new("trackPair")))
  }

}

testRUnit.checkEqualsNumeric <- function()
{
  ##@bdescr
  ## test case for function checkEqualsNumeric of class: none
  ##@edescr

  checkTrue( checkEqualsNumeric( 9,9))
  checkTrue( checkEqualsNumeric( 9.1,9.2, tol=0.1))
  x <- 1:10
  attributes(x) <- list(dummy="nonsense")
  checkTrue( checkEqualsNumeric( x, x))
  checkTrue( checkEqualsNumeric( 1:10, x, check.attributes=FALSE))
  
  rvec <- rnorm(132)
  checkTrue( checkEqualsNumeric( matrix(rvec, 12, 11), matrix(rvec, 12, 11)))
  checkTrue( checkEqualsNumeric( rvec, rvec))

  ##  exception handling
  ##  numeric difference
  shouldBomb( checkEqualsNumeric( 9, 10))
  shouldBomb( checkEqualsNumeric( list(9), list(10)))
  shouldBomb( checkEqualsNumeric( matrix(9), matrix(10)))
  rvec2 <- rnorm(132)
  shouldBomb( checkEqualsNumeric( matrix(rvec, 12, 11), matrix(rvec2, 12, 11)))

  
  ##  type not supported
  shouldBomb( checkEqualsNumeric( list(rvec), list(rvec)))
}


testRUnit.checkIdentical <- function()
{
  ##@bdescr
  ## test case for function checkIdentical of class: none
  ##@edescr

  checkIdentical( TRUE, TRUE)
  ##  return value
  checkTrue( checkIdentical( TRUE, TRUE))
  checkIdentical( FALSE, FALSE)

  checkIdentical( as.integer(2), as.integer(2))
  checkIdentical( as.character(2), as.character(2))
  checkIdentical( as.complex(2), as.complex(2))
  checkIdentical( as.numeric(2), as.numeric(2))
  checkIdentical( as.expression("2+4"), as.expression("2+4"))
  checkIdentical( as.expression(2+4), as.expression(2+4))

  sysTime <- as.POSIXlt(Sys.time())
  checkIdentical( sysTime, sysTime)

  ##  S3 objects (ie. lists with attributes)
  ##  from ?lm Example
  ctl <- c(4.17,5.58,5.18,6.11,4.50,4.61,5.17,4.53,5.33,5.14)
  trt <- c(4.81,4.17,4.41,3.59,5.87,3.83,6.03,4.89,4.32,4.69)
  group <- gl(2,10,20, labels=c("Ctl","Trt"))
  weight <- c(ctl, trt)
  lm.D9 <- lm(weight ~ group)
  checkIdentical( lm.D9, lm(weight ~ group))


  ##  S4 objects
  if (identical(TRUE, require(methods))) {
    setClass("track1",
             representation(x="numeric", y="numeric"),
             where=.GlobalEnv)
    on.exit(removeClass("track1", where=.GlobalEnv))

    s4Obj <- try(new("track1"))
    checkIdentical( s4Obj, new("track1"))
    rm(s4Obj)
  }

  
  ##  exception handling
  ##  type mismatches
  shouldBomb( checkIdentical( as.integer(2), as.numeric(2)))
  shouldBomb( checkIdentical( as.integer(2), as.character(2)))
  shouldBomb( checkIdentical( as.integer(2), as.list(2)))
  shouldBomb( checkIdentical( as.integer(2), as.complex(2)))
  shouldBomb( checkIdentical( as.integer(2), as.expression(2)))

  ##  value mismatches
  shouldBomb( checkIdentical( as.integer(2), as.integer(3)))
  shouldBomb( checkIdentical( as.character(2), as.character(3)))
  shouldBomb( checkIdentical( as.complex(2), as.complex(3)))
  shouldBomb( checkIdentical( as.numeric(2), as.numeric(3)))
  shouldBomb( checkIdentical( as.expression("2+4"), as.expression("2+3")))

  sysTime <- as.POSIXlt(Sys.time())

  shouldBomb( checkIdentical( sysTime, as.POSIXlt(Sys.time(), tz="GMT")))

  ##  S3 objects (ie. lists with attributes)
  ##  from ?lm Example
 

  lm.D9base <- lm(weight ~ group - 1)
  shouldBomb( checkIdentical( lm.D9base, lm.D9))

  ##  S4 objects
  if (identical(TRUE, require(methods))) {
    setClass("track2",
             representation(x="numeric", y="numeric"),
             prototype(x=as.numeric(1:23), y=as.numeric(23:1)),
             where=.GlobalEnv)
    on.exit(removeClass("track2", where=.GlobalEnv), add=TRUE)

    s4Obj <- try(new("track2"))
    s4ObjDiff <- s4Obj
    s4ObjDiff@y <- s4ObjDiff@x
    shouldBomb( checkIdentical( s4Obj, s4ObjDiff))
  }

}


testRUnit.checkTrue <- function()
{
  ##@bdescr
  ## test case for function checkTrue of class: none
  ##@edescr


  checkEquals( checkTrue( TRUE), TRUE)

  ##  named arguments
  namedArg <- TRUE
  names(namedArg) <- "Yes"
  checkEquals( checkTrue( namedArg), TRUE)


  ##  errorr handling
  namedArg <- FALSE
  names(namedArg) <- "No"
  shouldBomb( checkTrue( namedArg))
  
  shouldBomb( checkTrue( FALSE))
  
  ##  incorrect length
  shouldBomb( checkTrue( c(TRUE, TRUE)))
  shouldBomb( checkTrue( c(FALSE, TRUE)))
  shouldBomb( checkTrue( logical(0)))
  shouldBomb( checkTrue( logical(2)))
  
}


testRUnit.shouldBomb <- function()
{
  ##@bdescr
  ## test case for function shouldBomb of class: none
  ##@edescr

  shouldBomb( checkTrue( FALSE))
  shouldBomb( checkTrue( ))
  shouldBomb( checkEquals( ))
  shouldBomb( checkEquals( 24))
  shouldBomb( checkEquals( 24, 24, tol="dummy"))
  shouldBomb( checkEqualsNumeric( ))
  shouldBomb( checkEqualsNumeric( 24))
  shouldBomb( checkEqualsNumeric( 24, 24, tol="dummy"))

  shouldBomb( stop("wo message"))

  ##  R 2.5.0 devel example that failed
  ##  minimal example provided by Seth Falcon
  ll = list()
  ll[[1]] = function(x) stop("died")
  shouldBomb( do.call(ll[[1]], list(1)))

  ##  S4 objects
  if (identical(TRUE, require(methods))) {
    setClass("track2",
             representation(x="numeric", y="numeric"),
             prototype(x=as.numeric(1:23), y=as.numeric(23:1)),
             where=.GlobalEnv)
    on.exit(removeClass("track2", where=.GlobalEnv))

    s4Obj <- try(new("track2"))
    shouldBomb( slot(s4Obj, "z"))
    shouldBomb( slot(s4Obj, "z") <- 1:10)
  
    ##  missing method argument
    ##  coerce(from, to)
    shouldBomb( coerce(s4Obj))
  }
  
}


testRUnit.DEACTIVATED <- function()
{
  ##@bdescr
  ## test case for function DEACTIVATED of class: none
  ##@edescr

  shouldBomb( DEACTIVATED())
  shouldBomb( DEACTIVATED("some message"))
}


testRUnit.defineTestSuite <- function()
{
  ##@bdescr
  ## test case for function defineTestSuite of class: none
  ##@edescr
  
  ##  correct working
  testSuite <- defineTestSuite("RUnit Example", system.file("examples", package="RUnit"), 
                               testFileRegexp="correctTestCase.r")
  
  ##  this also works for S3 objects
  checkTrue( inherits(testSuite, "RUnitTestSuite"))
  checkTrue( is.list(testSuite))
  checkTrue( all(c("name", "dirs", "testFileRegexp", "testFuncRegexp",
                   "rngKind", "rngNormalKind") %in% names(testSuite)))
  checkTrue( isValidTestSuite(testSuite))
  
  
  ##  error handling
  
}


testRUnit.isValidTestSuite <- function()
{
  ##@bdescr
  ## test case for function isValidTestSuite of class: none
  ##@edescr
  
  ##  correct working
  testSuite <- defineTestSuite("RUnit Example", system.file("examples", package="RUnit"), testFileRegexp="correctTestCase.r")
  checkTrue( isValidTestSuite(testSuite))
  
  ##  error handling
  ##  has to be S3 class 'RUnitTestSuite'
  testSuiteFail <- testSuite
  class(testSuiteFail) <- "NotUnitTestSuite"
  checkTrue( !isValidTestSuite(testSuiteFail))
  
  ##  expecting list elements
  testSuiteFail <- testSuite
  testSuiteFail[["dirs"]] <- NULL
  checkTrue( !isValidTestSuite(testSuiteFail))
  
  ##  has to be character
  testSuiteFail <- testSuite
  testSuiteFail[["name"]] <- list()
  checkTrue( !isValidTestSuite(testSuiteFail))
 
  testSuiteFail <- testSuite
  testSuiteFail[["dirs"]] <- list()
  checkTrue( !isValidTestSuite(testSuiteFail))
  
  testSuiteFail <- testSuite
  testSuiteFail[["testFileRegexp"]] <- list()
  checkTrue( !isValidTestSuite(testSuiteFail))
  
  testSuiteFail <- testSuite
  testSuiteFail[["testFuncRegexp"]] <- list()
  checkTrue( !isValidTestSuite(testSuiteFail))
  
  
  ##  director has to exist
  testSuiteFail <- testSuite
  testSuiteFail[["dirs"]] <- "doesNotExist"
  checkTrue( !isValidTestSuite(testSuiteFail))
}
  

testRUnit.runTestFile <- function()
{
  ##@bdescr
  ## test case for function runTestFile of class: none
  ##@edescr

  testFile <- file.path(system.file("examples", package="RUnit"), "correctTestCase.r")
  checkTrue( file.exists(testFile))
  
  ##res <- runTestFile(testFile)
  
  ##  error handling
  ##  all argument checks delegated to runTestSuite so no need for comprehensive check here
  ##  check if any argument check is reached/performed
  ##  useOwnErrorHandler
  ##  type logical
  shouldBomb( runTestFile(testFile, useOwnErrorHandler=integer(1)))
}


testRUnit.runTestSuite <- function()
{
  ##@bdescr
  ## test case for function runTestSuite of class: none
  ##@edescr

  testSuiteTest <- defineTestSuite("RUnit Example", system.file("examples", package="RUnit"), testFileRegexp="correctTestCase.r")

  checkTrue( isValidTestSuite(testSuiteTest))

  ##  this call has to be executed in new environment
  ##res <- runTestSuite(testSuiteTest)
  ##
  ##  FIXME: does not work as intended
  #testEnv <- new.env()
  #assign("res", runTestSuite(testSuiteTest), envir=testEnv)
  #checkTrue( is(get("res", envir=testEnv), "RUnitTestData"))
  
  ##  error handling
  ##
  ##  useOwnErrorHandler
  ##  type logical
  tS <- testSuiteTest
  shouldBomb( runTestSuite(tS, useOwnErrorHandler=integer(1)))
  ##  length 1
  shouldBomb( runTestSuite(tS, useOwnErrorHandler=logical(0)))
  shouldBomb( runTestSuite(tS, useOwnErrorHandler=logical(2)))
  shouldBomb( runTestSuite(tS, useOwnErrorHandler=as.logical(NA)))
  
  ##  testSuite
  

}
