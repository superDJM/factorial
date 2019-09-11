/**
 * 实现了几种求超大整数阶乘的方法
 * <p>
 * {@code longMultiplication}
 * 使用jdk的{@code BigInteger}直接做迭代，主要做性能基准和结果对比
 * <p>
 * {@code multiplyByInt}
 * 模仿jdk的{@code BigInteger}实现了大数的储存和大数的乘积计算
 * <p>
 * {@code binarySplit}
 * 二分分割递归相乘,使用jdk的{@code BigInteger}做大数乘法和大数储存
 * <p>
 * {@code prime}
 * 质因数分解n！,使用jdk的{@code BigInteger}做大数乘法和大数储存
 * <p>
 * {@code moessner}
 * 使用一个独特的算法只用加法来实现n!
 * <p>
 *
 * @author jamiedeng
 * @date 2019/9/9
 * @link https://thatsmaths.com/2017/09/14/moessners-magical-method/
 * @link https://en.wikipedia.org/wiki/Stirling%27s_approximation
 * @link https://gmplib.org/manual/Factorial-Algorithm.html
 * 
 */
public class Factorial {
    /**
     * 10^9
     */
    private final static int INT_RADIX = 0x3b9aca00;

    /**
     * 2^32
     */
    private final static long LONG_MASK = 0xffffffffL;

    /**
     * n!的缓存long数组，最大值为long能表达的数
     */
    private static final long[] FACTORIALS_CACHE = new long[]{1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};

    /**
     * 检查阶乘数的值
     *
     * @param n 阶乘数
     * @throws ArithmeticException n不能小于0
     */
    private static void checkArgument(int n) {
        //阶乘数不能少于0
        if (n < 0) {
            throw new ArithmeticException("Factorial: n has to be >= 0, but was " + n);
        }

    }

    /**
     * 大整数阶乘-jdk bigInteger版本
     * <p>
     * 默认使用长乘法
     * <p>
     * 时间复杂度：O(n^2logn)
     * <p>
     * 外层n次循环
     * 里层乘法因为是大数乘小数，时间复杂度为nlogn
     * T(n) = n * nlogn
     * <p>
     * 空间复杂度：O(nlogn)
     * <p>
     * BigInteger存储大数部分的空间复杂度是nlogn
     *
     * @param n 阶乘因子
     * @return 阶乘结果字符串
     */
    public static String longMultiplication(int n) {
        java.math.BigInteger ans = java.math.BigInteger.valueOf(1);
        for (int i = 2; i <= n; i++) {
            ans = ans.multiply(java.math.BigInteger.valueOf(i));
        }
        return ans.toString();
    }

    /**
     * 大整数阶乘-大整数乘小数版本
     * <p>
     * 用int型数组储存大数，用long储存两个int变量的乘积。
     * 数组的每个元素表示9位十进制数。在运算时，从这个数的最后一个元素开始，
     * 依次乘以当前乘数i(i=1..n)并加上上次的进位,其和的低9位数依然存在原位置，高几位向前进位。
     * <p>
     * 位数为x=log10(n)
     * <p>
     * 时间复杂度:O(n^2logn)
     * <p>
     * 外层n次循环，里层(x!)次乘法
     * T(n) = n*x! = n * logn! = n * nlogn
     * T(n) = O(n^2logn)
     * <p>
     * 空间复杂度:O(nlogn)
     * <p>
     * 使用int数组存储阶乘结果的位数，每个元素储存10^9进制数，可以表示9位
     * 所以空间复杂度就是位数(nlogn)/9 = O(nlogn)
     *
     * @param n 阶乘数
     * @link https://en.wikipedia.org/wiki/Stirling%27s_approximation
     */
    public static String multiplyByInt(int n) {

        checkArgument(n);

        //缓存
        if (n < FACTORIALS_CACHE.length) {
            return java.math.BigInteger.valueOf(FACTORIALS_CACHE[n]).toString();
        }

        //head为有效位的下标，tail是数组最后一位下标，p是当前遍历的下标表
        int head, tail, p;
        //carry是进位，product是每次乘积的结果
        long carry, product;
        //阶乘位数
        long factorialLen = getFactorialLen(n);
        //数组长度=位数/每个元素储存的位数，加1是为了设置大一点，防止溢出
        int digit = (int) ((factorialLen / Math.log10(INT_RADIX)) + 1);
        //初始化一个数组
        int[] buff = new int[digit];
        head = tail = digit - 1;
        //初始化个位为1
        buff[tail] = 1;

        for (int i = 2; i <= n; i++) {
            //被乘数是int，只需从高位循环大数的数组，计算乘积和进位
            for (carry = 0, p = tail; p >= head; p--) {
                product = (buff[p] & LONG_MASK) * i + carry;
                buff[p] = (int) (product % INT_RADIX);
                carry = product / INT_RADIX;
            }

            //处理最后的进位
            if (carry > 0) {
                buff[--head] = (int) carry;
            }
        }

        //转化成字符串表示
        StringBuilder sb = new StringBuilder(buff.length);
        sb.append(String.format("%d", buff[head]));
        for (int j = head + 1; j <= tail; j++) {
            sb.append(String.format("%09d", buff[j] & LONG_MASK));
        }
        return sb.toString();
    }


    /**
     * 大整数阶乘-二分拆分递归相乘版本。
     * <p>
     * 大数的储存和乘法依赖{@code java.math.bigInteger}的实现
     * 由于BigInteger会根据两个大数位数因子来判断使用哪个算法，
     * 当两个因数均小于 2^32×80 时，用二重循环直接相乘，复杂度为O(n2)，n为因数位数
     * 当两个因数均小于 2^32×240时，采用 Karatsuba algorithm，其复杂度为O(nlog2(3))≈O(n^1.585)
     * 否则采用Toom-Cook multiplication，其复杂度为O(nlog3(5))≈O(n^1.465)
     * <p>
     * 优化点：利用二分递归，可以平衡乘数的大小并使得乘数更大。容易触发更高效的算法
     * <p>
     * 时间复杂度:O(nlogn^1.5)
     * <p>
     * 外层n次循环乘法,乘法的复杂度因为采用不同算法算平均值O(n^1.5)
     * T(n) = n * (nlogn)^1.5
     * T(n) = O(n(nlogn)^1.5)
     * <p>
     * 空间复杂度:O(nlogn)
     * <p>
     * 参考BigInteger使用int数组2^32空间储存大数的值，
     *
     * @param n 阶乘数
     * @return 阶乘结果字符串
     * @link https://gmplib.org/manual/Factorial-Algorithm.html
     */
    public static String binarySplit(int n) {

        checkArgument(n);

        if (n < FACTORIALS_CACHE.length) {
            return java.math.BigInteger.valueOf(FACTORIALS_CACHE[n]).toString();
        }
        //2的幂计算起来，单独做位移运算
//        int bits = 0;
//        for (int i = 2; i <= n; i++) {
//            if (Integer.bitCount(i) == 1) {
//                bits += Integer.numberOfTrailingZeros(i);
//                continue;
//            }
////            ans = ans.multiply(java.math.BigInteger.valueOf(i));
//        }
        java.math.BigInteger ans = subFactorial(FACTORIALS_CACHE.length, n);

//        return ans.shiftLeft(bits).toString();
        return ans.multiply(java.math.BigInteger.valueOf(FACTORIALS_CACHE[FACTORIALS_CACHE.length - 1])).toString();
    }

    /**
     * 获取阶乘结果的预估位数
     *
     * @param n 阶乘数
     * @return 阶乘结果的预估位数
     * @link https://en.wikipedia.org/wiki/Stirling%27s_approximation
     */
    private static long getFactorialLen(int n) {
        //使用Stirling公式求n阶乘位数的近似值
        //Trunc(0.5 * Ln(2 * n * 3.1415926) / Ln(10) + n * Ln(n / Exp(1)) / Ln(10)) + 1
        return (long) ((0.5 * Math.log(2 * n * Math.PI) / Math.log(10) + n * Math.log(n / Math.exp(1)) / Math.log(10)) + 1);
    }

    /**
     * 二分递归计算a*a+1*...*b乘积
     *
     * @param a 乘数
     * @param b 被乘数
     * @return a到b之间的数的乘积
     */
    private static java.math.BigInteger subFactorial(int a, int b) {

        switch ((b - a)) {
            case 0:
                return java.math.BigInteger.valueOf(a);
            case 1:
                return java.math.BigInteger.valueOf(a).multiply(java.math.BigInteger.valueOf(b));
            case 2:
                return java.math.BigInteger.valueOf(a).multiply(java.math.BigInteger.valueOf(a + 1)).multiply(java.math.BigInteger.valueOf(b));
        }

        int mid = a + b >>> 1;
        return subFactorial(a, mid).multiply(subFactorial(mid + 1, b));
    }


    /**
     * 快速幂
     * <p>
     * 时间复杂度为O(logn)
     * 空间复杂度为O(logn)
     *
     * @param a 乘数
     * @param b 被乘数
     * @return a^b
     */
    private static java.math.BigInteger quickPow(int a, int b) {
        java.math.BigInteger ans = java.math.BigInteger.ONE;
        java.math.BigInteger tmp = java.math.BigInteger.valueOf(a);
        while (b > 0) {
            if ((b & 1) == 1)
                ans = ans.multiply(tmp);
            tmp = tmp.multiply(tmp);
            b >>= 1;
        }
        return ans;
    }

    /**
     * 大整数阶乘-质数分解计算阶乘版本
     * 先把n！分解成小于n！的质数的幂相乘
     * eg:5! = 120 = 2^3 * 3^1 * 5^1
     * <p>
     * 优化点：
     * 1、使用快速筛而不是将1~N中每一个数字都质因数分解
     * 2、使用幂减少乘法的次数
     *
     * <p>
     * 时间复杂度：O(n^4logn)
     * <p>
     * 素数筛复杂度为O(nloglogn)
     * 累乘部分 乘法复杂度为O(n(nlogn)^1.5)
     * 求幂复杂度为O(nlogn)
     * T(n) = nloglogn + n * ((nlogn)^1.5 + nlogn)
     * T(n) = O(n(nlogn)^1.5)
     * <p>
     * 空间复杂度：O(nlogn)
     * <p>
     * 依赖BigInteger存储大数部分是nlogn
     * 素数筛依赖的标记数据的空间复杂度为O(n)
     *
     * @param n 阶乘数
     * @return 阶乘结果的字符串
     * @link https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
     */
    public static String prime(int n) {
        checkArgument(n);

        if (n < FACTORIALS_CACHE.length) {
            return java.math.BigInteger.valueOf(FACTORIALS_CACHE[n]).toString();
        }

        boolean[] flag = new boolean[n + 1];
        //排除1和2
        flag[0] = flag[1] = true;

        //eratosthenes 从2开始将每个素数的各个倍数，标记成合数
        for (int i = 2; i <= Math.sqrt(n); i++) {
            //如果是一个素数
            if (!flag[i]) {
                for (int j = i * i; j <= n; j += i) {
                    flag[j] = true;
                }
            }
        }
        java.math.BigInteger ans = java.math.BigInteger.ONE;
        //循环素数数组
        for (int i = 2; i <= n; i++) {
            if (!flag[i]) {
                int cnt = 0;
                double tmp;
                //循环计算n/p,n/p^2,n/p^3,直到p^k > n结束
                for (int k = 1; (tmp = Math.pow(i, k)) <= n; k++) {//统计p出现的次数
                    cnt += n / tmp;
                }
                //求质数的cnt幂，再累乘起来
                ans = ans.multiply(java.math.BigInteger.valueOf(i).pow(cnt));
            }
        }
//        int div;
//        int curNum;
//        //循环n求出1到n的所有的素数和对应的数量
//        for (int i = 2; i <= n; i++) {
//            curNum = i;
//            div = 2;
//            while (curNum > 1) {
//                while (curNum % div == 0) {
//                    buff[div]++;
//                    curNum /= div;
//                }
//                div++;
//            }
//        }
//        java.math.BigInteger ans = java.math.BigInteger.ONE;
//        for (int i = 0; i < buff.length; i++) {
//            if (buff[i] > 0) {
//                ans = ans.multiply(java.math.BigInteger.valueOf(i).pow(buff[i]));
//            }
//        }
        return ans.toString();
    }

    /**
     * 一个独特的算法只用加法来算阶乘
     * <p>
     * 时间复杂度 O(n^4logn)
     * <p>
     * moessner本身的时间复杂度为O(n^3)
     * 加法的复杂度为O（nlogn）
     * <p>
     * 空间复杂度 O(nlogn)
     * <p>
     * 依赖BigInteger存储大数部分时间复杂度是nlogn
     * 算法本身的空间复杂度为O(n)
     *
     * @param n 阶乘数
     * @return 阶乘结果字符串
     */
    public static String moessner(int n) {

        checkArgument(n);

        if (n < FACTORIALS_CACHE.length) {
            return java.math.BigInteger.valueOf(FACTORIALS_CACHE[n]).toString();
        }

        java.math.BigInteger[] buff = new java.math.BigInteger[n + 1];
        buff[0] = java.math.BigInteger.ONE;

        for (int i = 1; i <= n; i++) {
            buff[i] = java.math.BigInteger.ZERO;
            for (int j = i; j >= 1; j--) {
                for (int k = 1; k <= j; k++) {
                    buff[k] = buff[k].add(buff[k - 1]);
                }
            }
        }
        return buff[n].toString();
    }


    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("input is invalid");
            System.out.println("usage: javac Factorial.java && java Factorial 100");
            System.exit(0);
        }
        int n = Integer.valueOf(args[0]);
        System.out.println("jdk版本:" + System.getProperty("java.version"));
        System.out.println(n + "的阶乘");

        int timeBegin = (int) System.currentTimeMillis();
        String s = longMultiplication(n);
        int timeFinish = (int) System.currentTimeMillis();
        int time = timeFinish - timeBegin;
        System.out.println("longMultiplication计算耗时: " + time + "毫秒");

        int timeBegin1 = (int) System.currentTimeMillis();
        String s1 = multiplyByInt(n);
        int timeFinish1 = (int) System.currentTimeMillis();
        int time1 = timeFinish1 - timeBegin1;
        System.out.println("multiplyByInt计算耗时: " + time1 + "毫秒");


        int timeBegin2 = (int) System.currentTimeMillis();
        String s2 = binarySplit(n);
        int timeFinish2 = (int) System.currentTimeMillis();
        int time2 = timeFinish2 - timeBegin2;
        System.out.println("binarySplit计算耗时: " + time2 + "毫秒");

        int timeBegin3 = (int) System.currentTimeMillis();
        String s3 = prime(n);
        int timeFinish3 = (int) System.currentTimeMillis();
        int time3 = timeFinish3 - timeBegin3;
        System.out.println("prime计算耗时: " + time3 + "毫秒");

        int timeBegin4 = (int) System.currentTimeMillis();
        String s4 = moessner(n);
        int timeFinish4 = (int) System.currentTimeMillis();
        int time4 = timeFinish4 - timeBegin4;
        System.out.println("moessner计算: " + time4 + "毫秒");

        assert s1.equals(s);
        assert s2.equals(s);
        assert s3.equals(s);
        assert s4.equals(s);
    }

}
