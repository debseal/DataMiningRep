library(ggplot2)
myData <- read.table("engima.txt", sep="",header = FALSE, colClasses=c("character", "numeric"))
year <-substr(myData[,1],0,4)
uniqueYear <-unique(year)
month <- substr(myData[,1],6,nchar(myData[,1]))
monthNum <- as.numeric(unique(month))
factorMonth <- factor(monthNum, levels=monthNum, ordered=TRUE)
myData <- cbind(year, factorMonth, myData[c(-1)])
str(myData)
head(myData)
qplot(data=myData,x=factorMonth,y=V2,color=year, facets =~year, group=year) + geom_line() + ylab("Values") +xlab("Months")
qplot(data=myData,x=year,y=V2,color=factorMonth, facets =~factorMonth, group=factorMonth) + geom_line() + ylab("Values") +xlab("Years")
summary(myData)
attach(myData)
boxplot(V2~year)
ggplot(myData, aes(x=year, y=V2)) + 
  geom_boxplot() + 
  stat_summary(fun.y=median, geom="line", aes(group=1,color="median", colours="red")) +
  stat_summary(fun.y=median, geom="line", aes(group=1, color="median"))