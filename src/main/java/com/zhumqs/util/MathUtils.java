package com.zhumqs.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mingqizhu
 * @date 20191201
 */
@Slf4j
public class MathUtils {

    public static double cot(double a) {
        return Math.cos(a) / Math.sin(a);
    }

    public static double sec(double a) {
        return 1 / Math.cos(a);
    }

    public static double csc(double a) {
        return 1 / Math.sin(a);
    }

    public static double arcsin(double a) {
        return Math.asin(a);
    }

    public static double arccos(double a) {
        return Math.acos(a);
    }

    public static double arctan(double a) {
        return Math.atan(a);
    }

    public static double arccot(double a) {
        return arctan(1 / a);
    }

    public static double arcsec(double a) {
        return arccos(1 / a);
    }

    public static double arccsc(double a) {
        return arcsin(1 / a);
    }

    /**
     * 正矢函数
     * @param a
     * @return
     */
    public static double versin(double a) {
        return 1 - Math.cos(a);
    }

    /**
     * 正矢函数
     * @param a
     * @return
     */
    public static double vercosin(double a) {
        return 1 + Math.cos(a);
    }

    /**
     * 余矢函数
     * @param a
     * @return
     */
    public static double coversin(double a) {
        return 1 - Math.sin(a);
    }

    /**
     * 余矢函数
     * @param a
     * @return
     */
    public static double covercosin(double a) {
        return 1 + Math.sin(a);
    }

    /**
     * 半正矢函数
     * @param a
     * @return
     */
    public static double haversin(double a) {
        return (1 - Math.cos(a)) / 2;
    }

    /**
     * 半正矢函数
     * @param a
     * @return
     */
    public static double havercosin(double a) {
        return (1 + Math.cos(a)) / 2;
    }

    /**
     * 半余矢函数
     * @param a
     * @return
     */
    public static double hacoversin(double a) {
        return (1 - Math.sin(a)) / 2;
    }

    /**
     * 半余矢函数
     * @param a
     * @return
     */
    public static double hacovercosin(double a) {
        return (1 + Math.sin(a)) / 2;
    }

    /**
     * 外正割函数
     * @param a
     * @return
     */
    public static double exsec(double a) {
        return sec(a) - 1;
    }

    /**
     * 外余割函数
     * @param a
     * @return
     */
    public static double excsc(double a) {
        return csc(a) - 1;
    }

    public static double log2(double a) {
        return logN(2, a);
    }

    /**
     * bNum为底zNum的对数。
     * @param bNum 底数。
     * @param zNum 真数。
     * @return 对数值。
     */
    public static double logN(double bNum, double zNum) {
        return Math.log(zNum) / Math.log(bNum);
    }

    /**
     * 对num进行四舍五入操作。
     * @param num 要进行舍入操作的数。
     * @param bit 要保留小数的精确位数。
     * @return 舍入后的结果。
     */
    public static double round(double num, int bit) {
        double tmp = Math.pow(10, bit);
        return Math.round(num * tmp) / tmp;
    }

}
