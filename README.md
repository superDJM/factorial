# factorial
Calculating large number factorial in java  

see more detail in doc

# Tutorial

Just type and enter

`>cd code && javac Factorial.java && java Factorial 100`

then run and check result 
# Performance test

jdk version:1.8.0_66 ,mac air 2.2 GHz Intel Core i7

base line is longMultiplication algorithm

100! 
+ longMultiplication cost: 3ms
+ multiplyByInt cost: 30ms
+ binarySplit cost: 0ms
+ prime cost: 1ms
+ moessner cost: 70ms

1000! 
+ longMultiplication cost: 15ms
+ multiplyByInt cost: 63ms
+ binarySplit cost: 13ms
+ prime cost: 6ms
+ moessner cost: 26225ms

10000! 
+ longMultiplication cost: 226ms
+ multiplyByInt cost: 381ms
+ binarySplit cost: 61ms
+ prime cost: 125ms
+ moessner cost: too slow!!

100000! 
+ longMultiplication cost: 5392ms
+ multiplyByInt cost: 10250ms
+ binarySplit cost: 873ms
+ prime cost: 2291ms
+ moessner cost: too slow!!

1000000! 
+ longMultiplication cost: 419096ms
+ multiplyByInt cost: 1218278ms
+ binarySplit cost: 27014ms
+ prime cost: 305147ms
+ moessner cost: too slow!!