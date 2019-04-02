package com.company.sorm.utils;

import java.sql.PreparedStatement;

/**
 * 封装了JDBC常用的操作
 */
public class JDBCUtils {
    public static void handleParams(PreparedStatement ps,Object[] params){
        //给sql设参数
        if(params!=null){
            for (int i=0;i<params.length;i++){
                try {
                    ps.setObject(1+i,params[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
