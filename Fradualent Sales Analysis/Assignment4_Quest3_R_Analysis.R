library(ggplot2)
myData <- read.table("UScongress.csv", sep=",",header = FALSE)
attach(myData)
colnames(myData)<-c("CongNum","RollNum","Month","Day","Year","NumMissing","NumYes","NumNo","NumRepYes","NumRepNo","NumDemYes","NumDemNo","NumNRepYes","NumNRepNo","NumSRepYes","NumSRepNo","NumNDemYes","NumNDemNo","NumSDemYes","NumSDemNo")
summary(myData)
nrow(myData)
ncol(myData)

meanNumRepYes  <- aggregate(x=NumRepYes,by=list(Year),FUN=mean)
meanNumDemYes  <- aggregate(x=NumDemYes,by=list(Year),FUN=mean)
qplot(data=meanNumRepYes,x=meanNumRepYes[,1],y=meanNumRepYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of Republic Yes Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumDemYes,x=meanNumDemYes[,1],y=meanNumDemYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of Democratic Yes Mean",xlab="Year") + scale_size_area()

meanNumRepNo  <- aggregate(x=NumRepNo,by=list(Year),FUN=mean)
meanNumDemNo  <- aggregate(x=NumDemNo,by=list(Year),FUN=mean)
qplot(data=meanNumRepNo,x=meanNumRepNo[,1],y=meanNumRepNo[,2], method = "lm",geom = c("point", "smooth"),ylab="# of Republic No Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumDemNo,x=meanNumDemNo[,1],y=meanNumDemNo[,2], method = "lm",geom = c("point", "smooth"),ylab="# of Democratic No Mean",xlab="Year") + scale_size_area()

meanNumSRepNo  <- aggregate(x=NumSRepNo,by=list(Year),FUN=mean)
meanNumNDemNo  <- aggregate(x=NumNDemNo,by=list(Year),FUN=mean)
meanNumSRepYes  <- aggregate(x=NumSRepYes,by=list(Year),FUN=mean)
meanNumNDemYes  <- aggregate(x=NumNDemYes,by=list(Year),FUN=mean)
qplot(data=meanNumSRepNo,x=meanNumSRepNo[,1],y=meanNumSRepNo[,2], method = "lm",geom = c("point", "smooth"),ylab="# of S Republic No Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumNDemNo,x=meanNumNDemNo[,1],y=meanNumNDemNo[,2], method = "lm",geom = c("point", "smooth"),ylab="# of N Democratic No Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumSRepYes,x=meanNumSRepYes[,1],y=meanNumSRepYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of S Republic Yes Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumNDemYes,x=meanNumNDemYes[,1],y=meanNumNDemYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of N Democratic Yes Mean",xlab="Year") + scale_size_area()


meanNumNRepYes  <- aggregate(x=NumNRepYes,by=list(Year),FUN=mean)
meanNumSDemYes  <- aggregate(x=NumSDemYes,by=list(Year),FUN=mean)
meanNumNRepNo  <- aggregate(x=NumNRepNo,by=list(Year),FUN=mean)
meanNumSDemNo  <- aggregate(x=NumSDemNo,by=list(Year),FUN=mean)

qplot(data=meanNumNRepYes,x=meanNumNRepYes[,1],y=meanNumNRepYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of N Republic Yes Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumSDemYes,x=meanNumSDemYes[,1],y=meanNumSDemYes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of S Democratic Yes Mean",xlab="Year") + scale_size_area()

qplot(data=meanNumNRepNo,x=meanNumNRepNo[,1],y=meanNumNRepNo[,2], method = "lm",geom = c("line", "smooth"),ylab="# of N Republic No Mean",xlab="Year") + scale_size_area()
qplot(data=meanNumSDemNo,x=meanNumSDemNo[,1],y=meanNumSDemNo[,2], method = "lm",geom = c("line", "smooth"),ylab="# of S Democratic No Mean",xlab="Year") + scale_size_area()


meanNumMissingVotes  <- aggregate(x=NumMissing,by=list(Year),FUN=mean)
qplot(data=meanNumMissingVotes,x=meanNumMissingVotes[,1],y=meanNumMissingVotes[,2], method = "lm",geom = c("point", "smooth"),ylab="# of Missing Votes",xlab="Year") + scale_size_area()
