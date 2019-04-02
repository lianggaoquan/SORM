package com.company.sorm.utils;

import java.lang.reflect.Method;

/**
 * 封装了反射常用的操作
 */
public class ReflectUtils {

    /**
     * 调用obj对应属性fieldName的get方法
     * @param fieldName
     * @param obj
     * @return
     */
    public static Object invokeGet(String fieldName,Object obj){
        //通过反射机制，调用属性对应的get或set方法
        try{
            //获取obj对象的onlyPriKey属性值
            Method m = obj.getClass().getMethod("get"+ StringUtils.firstChar2Upper(fieldName));
            return m.invoke(obj,null);


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void invokeSet(Object obj,String columnName,Object columnValue){
        try {
            Method m = obj.getClass().getDeclaredMethod("set"+ StringUtils.firstChar2Upper(columnName),columnValue.getClass());

            m.invoke(obj,columnValue);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
