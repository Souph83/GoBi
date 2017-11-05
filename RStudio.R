setwd("~/Desktop/GoBi")

gencode<-read.table("gencode.out", header=TRUE)
bases1<-gencode$max_skipped_bases
exons1<-gencode$max_skipped_exon
vec1<-cbind(rep("gencode", times=length(bases1)))

HS3767<-read.table("HS37.67.out", header=TRUE)
bases2<-HS3767$max_skipped_bases
exons2<-HS3767$max_skipped_exon
vec2<-cbind(rep("HS37.67", times=length(bases2)))

HS3775<-read.table("HS37.75.out", header=TRUE)
bases3<-HS3775$max_skipped_bases
exons3<-HS3775$max_skipped_exon
vec3<-cbind(rep("HS37.75", times=length(bases3)))

HS3886<-read.table("HS38.86.out", header=TRUE)
bases4<-HS3886$max_skipped_bases
exons4<-HS3886$max_skipped_exon
vec4<-cbind(rep("HS38.86", times=length(bases4)))

HS3890<-read.table("HS38.90.out", header=TRUE)
bases5<-HS3890$max_skipped_bases
exons5<-HS3890$max_skipped_exon
vec5<-cbind(rep("HS38.90", times=length(bases5)))

Mus<-read.table("Mus.out", header=TRUE)
bases6<-Mus$max_skipped_bases
exons6<-Mus$max_skipped_exon
vec6<-cbind(rep("Mus", times=length(bases6)))

Sc<-read.table("Sc.out", header=TRUE)
bases7<-Sc$max_skipped_bases
exons7<-Sc$max_skipped_exon
vec7<-cbind(rep("Sc", times=length(bases7)))


vec<-c(vec1,vec2,vec3,vec4,vec5,vec6,vec7)
bases<-c(bases1,bases2,bases3,bases4,bases5,bases6,bases7)
exons<-c(exons1,exons2,exons3,exons4,exons5,exons6,exons7)

boxplot(bases~vec, main="Distribution of the maximal number of skipped bases")

boxplot(exons~vec, main="Distribution of the maximal number of skipped exons")


allData<-rbind(gencode,HS3767,HS3775,HS3886,HS3890,Mus,Sc)

allDataSorted<-allData[with(allData, order(-max_skipped_bases)), ]