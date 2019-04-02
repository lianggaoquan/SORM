package com.company.sorm.utils;

/**
 * 封装了字符串常用的操作
 */
public class StringUtils {
    /**
     * 将目标字符串首字母变大写
     * @param str
     * @return
     */
    public static String firstChar2Upper(String str){
        // abc -> Abc
        return str.toUpperCase().substring(0,1) + str.substring(1);

    }
}
