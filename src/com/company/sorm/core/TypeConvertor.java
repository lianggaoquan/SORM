package com.company.sorm.core;

/**
 * 负责java数据类型与数据库数据类型的互相转换
 */
public interface TypeConvertor {
    /**
     * 将数据库数据类型转换为java数据类型
     * @param columnType
     * @return
     */
    public String databaseType2JavaType(String columnType);

    /**
     * 负责将java数据类型转换为数据库数据类型
     * @param javaDataType
     * @return
     */
    public String javaType2DatabaseType(String javaDataType);

}
