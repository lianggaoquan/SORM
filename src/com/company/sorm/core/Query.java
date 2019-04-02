package com.company.sorm.core;

import com.company.sorm.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * 负责查询
 */
public interface Query {
    /**
     * 直接执行一个DML语句
     * @param sql
     * @param params
     * @return 执行sql语句后影响了几行记录
     */
    public int executeDML(String sql, Object[] params);

    /**
     * 将一个对象存储到数据库中
     * @param obj
     */
    public void insert(Object obj);

    /**
     * 删除clazz表示类对应的表中的记录（指定id）
     * @param clazz 跟表对应的Class对象
     * @param id 主键的值
     * @return
     */
    public void delete(Class clazz, Object id);  // delete from user where id=2;

    /**
     * 删除对象在数据库中对应的记录，对象所在的类对应到表，对象的值对应到记录
     * @param obj
     */
    public void delete(Object obj);

    /**
     * 更新对象对应的记录并更新指定属性字段
     * @param obj
     * @param fieldNames 要更新的属性列表
     * @return
     */
    public int update(Object obj,String[] fieldNames);  // update user set uname=?

    /**
     * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
     * @param sql
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public List queryRows(String sql, Class clazz, Object[] params);

    /**
     * 查询返回一行记录，并将每行记录封装到clazz指定的类的对象中
     * @param sql
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Object queryUniqueRows(String sql, Class clazz, Object[] params);


    /**
     * 返回一个值（一行一列），并将该值返回
     * @param sql
     * @param params
     * @return
     */
    public Object queryValue(String sql, Object[] params);


    /**
     * 返回一个数字，并将该值返回
     * @param sql
     * @param params
     * @return
     */
    public Number queryNumber(String sql, Object[] params);
}
