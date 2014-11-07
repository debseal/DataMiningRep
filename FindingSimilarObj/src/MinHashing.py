#from nltk import word_tokenize
#from nltk.corpus import stopwords
import time
from sets import Set
import numpy
import sys
import random

totalShingle=Set()
SHINGLE_SIZE=9
NO_OF_HASH_FUNC=200


def readFile(inFile):
    sentDict={}
    i=0
    with open(inFile,'r') as fileObj:
        for line in fileObj:
            if i>=100:
                break
            spaceIndex=line.index(' ')
            sentNumber = int(line[:spaceIndex])
            print sentNumber,line[spaceIndex:]
            shinglesForCurrLine=createShingles(line[spaceIndex:].lstrip().rstrip('\n'), SHINGLE_SIZE)
            #print shinglesForCurrLine
            sentDict[sentNumber] = shinglesForCurrLine
            global totalShingle
            totalShingle = totalShingle | shinglesForCurrLine 
            i += 1 
    return sentDict
        
def createSparseMatrix(sentDict):
    ShingleRowNumMap={}

    print len(sentDict)
    print len(totalShingle)
    
    for idx, shingle in enumerate(sorted(totalShingle)):
        ShingleRowNumMap[shingle]= idx
    
    sparseMatrix =numpy.zeros((len(totalShingle),len(sentDict)), dtype=int) 

    for key, val in sentDict.items():
        for shingle in val:
            #print wordRowNumMap[word],key
            sparseMatrix[ShingleRowNumMap[shingle],key] = 1
            
    return sparseMatrix

def createShingles(inStr, size):
    shingles = Set()
    for i in range(0,len(inStr),size):
        shingles.add(inStr[i:(i+size)].lower())
    return shingles 

# (a*x+b) mod p) mod n
# n = # of hash functions
# p = prime number nearest to n, p >> n better
# a random number between 1 and p-1
# b random number between 0 and p-1
def createHashFunc(x,noOfShingles):
    i=0
    hashFunc=[]
    random.seed(100)
    nearestPrime= 15
    while(i < NO_OF_HASH_FUNC):
        a= random.randint(1, nearestPrime-1)
        b= random.randint(0, nearestPrime-1)
        hashCode = ((a*x + b) % nearestPrime) % noOfShingles 
        hashFunc.append(hashCode)
        i+=1
    return hashFunc

def createSignatureMatrix(sparseMatrix):
    noOfRow = NO_OF_HASH_FUNC
    noOfCol=sparseMatrix.shape[1]
        
    signatureMatrix = numpy.full((noOfRow,noOfCol), sys.maxint, dtype=int)

    minHashAlgo(signatureMatrix,sparseMatrix)
    
    return signatureMatrix

def minHashAlgo(signatureMatrix,sparseMatrix): 
    for eachRow in range(sparseMatrix.shape[0]):
        hashPermutCol = createHashFunc(eachRow,sparseMatrix.shape[0])
        #print hashPermutCol
        for eachCol in range(sparseMatrix.shape[1]):
            if sparseMatrix[eachRow,eachCol] == 1:
                for indx, eachHashRow in enumerate(hashPermutCol):
                    if eachHashRow < signatureMatrix[indx,eachCol]:
                        #print "I am in with", eachHashRow
                        signatureMatrix[indx,eachCol] = eachHashRow
    return signatureMatrix
   
def main():
    sentDictSTime= time.clock()
    print sentDictSTime
    sentDict = readFile("../../../sentences.txt")
    sentDictETime= time.clock()
    print "Time Taken to read the 500 MB file :", sentDictETime - sentDictSTime
    sparseMatrix=createSparseMatrix(sentDict)
    print sum(sparseMatrix[:,0])
    print sum(sparseMatrix[:,1])
    
    sigMatrix=createSignatureMatrix(sparseMatrix)
    print sigMatrix[:,0]
    print sigMatrix[:,1]
    
if __name__ == '__main__':
    main()
    


