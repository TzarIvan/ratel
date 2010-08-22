#! /tp/bin/Rscript --no-site-file --no-init-file

rm(list = ls())
library(fincad)
library(GSFCore)
library(RUnit)
library(RJDBC)
library(QFCredit)
library(QFEquity)
library(QFFixedIncome)

args <- commandArgs()
args <- args[-(1:match("--args", args))]

if(NROW(args)==0){
    dateList <- getRefLastNextBusinessDates(holidaySource = "financialcalendar", financialCenter = "lnb")
}else{
    dateList <- getRefLastNextBusinessDates(lastBusinessDate = args,holidaySource = "financialcalendar", financialCenter = "lnb")
}

########################################################################

tenorList <- TermStructure$irs[-16]
calculator <- IRSSlopeTri(instrument = "irs",ccy = "eur",dataTimeStamp = Close$LN.irs)

for (i in 1:(NROW(tenorList)-1)){
    for (j in (i+1):NROW(tenorList)){
    
        print(squish(tenorList[j],"-",tenorList[i]))
        
        result <- calculator$updateSlopeTriByTenor(swapTenorShort = tenorList[i],swapTenorLong = tenorList[j],updateTSDB = TRUE,startDate = as.character(dateList$refBusinessDate),endDate = NULL)
        if(is.null(result))quit(save="no", status = -1)
    }
}

quit(save="no", status=0)
                          