# factorial
Calculating large number factorial in java  

see more detail in doc

# Tutorial

Just type and run then check result 

`>cd code && javac Factorial.java && java Factorial 100`


# Performance test

multiplyByInt is the only algorithm without using jdk BigInteger

Base line is longMultiplication algorithm

jdk version:1.8.0_66 ,mac air 2.2 GHz Intel Core i7

100! 
+ longMultiplication  time cost: 3ms | mem cost: 12m
+ multiplyByInt cost: 30ms | mem cost: 12m
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
+ longMultiplication cost: 419096ms | max mem cost: 374874kb
+ multiplyByInt cost: 1218278ms | max mem cost: 21129kb
+ binarySplit cost: 27014ms | max mem cost: 328578kb
+ prime cost: 305147ms | max mem cost:372256kb
+ moessner cost: too slow!!

If you run my code use jdk version under 1.8,
you will get a significantly performance difference when n is a large number.
Because BigInteger had updated their multiply algorithm from [Long multiplication](https://en.wikipedia.org/wiki/Multiplication_algorithm#Long_multiplication)(O(n^2)) 
to the [Karatsuba algorithm](https://link.zhihu.com/?target=https%3A//en.wikipedia.org/wiki/Karatsuba_algorithm)(O(n^1.585))
or the [Toom-Cook multiplication](https://link.zhihu.com/?target=https%3A//en.wikipedia.org/wiki/Toom%25E2%2580%2593Cook_multiplication)(O(n^1.465))

jdk version:1.7.0_60 ,mac air 2.2 GHz Intel Core i7

100! 
+ longMultiplication cost: 3ms
+ multiplyByInt cost: 78ms
+ binarySplit cost: 1ms
+ prime cost: 0ms
+ moessner cost: 28ms

1000! 
+ longMultiplication cost: 29ms
+ multiplyByInt cost: 65ms
+ binarySplit cost: 5ms
+ prime cost: 5ms
+ moessner cost: 27885ms

10000! 
+ longMultiplication cost: 363ms
+ multiplyByInt cost: 344ms
+ binarySplit cost: 230ms
+ prime cost: 255ms
+ moessner cost: too slow!!

100000! 
+ longMultiplication cost: 45730ms
+ multiplyByInt cost: 13256ms
+ binarySplit cost: 26932ms
+ prime cost: 27505ms
+ moessner cost: too slow!!

1000000! 
+ longMultiplication cost: 419096ms
+ multiplyByInt cost: 1218278ms
+ binarySplit cost: 27014ms
+ prime cost: 305147ms
+ moessner cost: too slow!!
