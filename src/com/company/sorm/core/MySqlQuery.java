package com.company.sorm.core;

import com.company.sorm.bean.ColumnInfo;
import com.company.sorm.bean.TableInfo;
import com.company.sorm.utils.JDBCUtils;
import com.company.sorm.utils.ReflectUtils;
import com.company.vo.EmpVO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对mysql数据库的查询
 */
public class MySqlQuery implements Query {

    /**
     * 测试多表联合查询
     */
    public static void testQueryRows(){
        String sql2 = "SELECT e.id,e.empname,salary+bonus 'xinshui',d.dname,d.address FROM emp e\n" +
                "JOIN dept d ON e.deptId=d.id";

        List<EmpVO> list2 = new MySqlQuery().queryRows(sql2,EmpVO.class,null);

        for(EmpVO e: list2){
            System.out.println(e.getEmpname()+"--"+e.getAddress()+"--"+e.getXinshui());
        }
    }

    public static void main(String[] args){
//        List<Emp> list = new MySqlQuery().queryRows("select id,empname,age from emp where age>? and salary<?",
//                Emp.class,new Object[]{2,3000});
//
//        for(Emp e:list){
//            System.out.println(e.getEmpname());
//        }

        Object obj = new MySqlQuery().queryValue("select count(*) from emp where salary > ?",new Object[]{100});

        System.out.println((Number)obj);


    }

    @Override
    public int executeDML(String sql, Object[] params) {
        Connection conn = DBManager.getConn();
        int count = 0;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);

            //给sql设参数
//            if(params!=null){
//                for (int i=0;i<params.length;i++){
//                    ps.setObject(1+i,params[i]);
//                }
//            }

            JDBCUtils.handleParams(ps,params);

            System.out.println(ps);

            count = ps.executeUpdate();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBManager.close(conn);
        }


        return count;
    }

    @Override
    public void insert(Object obj) {
        //insert into 表名 (id,uname,pwd) values(?,?,?)
        Class c = obj.getClass();
        List<Object> params = new ArrayList<Object>();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);

        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getTname()+" (");
        Field[] fs = c.getDeclaredFields();

        int countNotNull = 0;  //计算不为空的属性

        for(Field f:fs){
            String fieldName = f.getName();
            Object fieldValue = ReflectUtils.invokeGet(fieldName,obj);

            if(fieldValue != null){
                countNotNull++;
                sql.append(fieldName+",");
                params.add(fieldValue);
            }
        }

        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for(int i=0;i<countNotNull;i++){
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');
        executeDML(sql.toString(),params.toArray());
    }



    @Override
    public void delete(Class clazz, Object id) {
        //Emp.class,2 --> delete from emp where id=2

        //通过Class找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        String sql = "delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=? ";
        executeDML(sql,new Object[]{id});
    }

    @Override
    public void delete(Object obj) {
        Class c = obj.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey(); //主键

        //通过反射机制，调用属性对应的get或set方法
//        try{
//            //获取obj对象的onlyPriKey属性值
//            Method m = c.getMethod("get"+ StringUtils.firstChar2Upper(onlyPriKey.getName()));
//            Object priKeyValue = m.invoke(obj,null);
//
//            delete(c, priKeyValue);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
        delete(c,priKeyValue);

    }

    @Override
    public int update(Object obj, String[] fieldNames) {
        // obj{"uname","pwd"} -> update 表名 set uname=? pwd=? where id=?
        Class c = obj.getClass();
        List<Object> params = new ArrayList<Object>();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo priKey = tableInfo.getOnlyPriKey();
        StringBuilder sql = new StringBuilder("update "+tableInfo.getTname()+" set ");

        for(String fname:fieldNames){
            Object fvalue = ReflectUtils.invokeGet(fname,obj);
            params.add(fvalue);
            sql.append(fname+" =?,");
        }
        sql.setCharAt(sql.length()-1,' ');
        sql.append(" where ");
        sql.append(priKey.getName()+"=? ");

        params.add(ReflectUtils.invokeGet(priKey.getName(),obj));

        System.out.println(sql.toString());

        executeDML(sql.toString(),params.toArray());
        return 0;
    }

    @Override
    public List queryRows(String sql, Class clazz, Object[] params) {
        Connection conn = DBManager.getConn();
        List list = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);

            JDBCUtils.handleParams(ps,params);

            System.out.println(ps);

            rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();

            //多行
            while (rs.next()){
                if (list==null){
                    list = new ArrayList();
                }
                Object rowObj = clazz.newInstance();


                //多列 select username,pwd,age from user where id=? and age>18
                for (int i=0;i<metaData.getColumnCount();i++){
                    String columnName = metaData.getColumnLabel(i+1);
                    Object columnValue = rs.getObject(i+1);

                    //利用反射，调用rowObj对象的setUsername(String uname)方法，将columnValue的值设置进去
//                    Method m = clazz.getDeclaredMethod("set"+ StringUtils.firstChar2Upper(columnName),columnValue.getClass());
//
//                    m.invoke(rowObj,columnValue);

                    ReflectUtils.invokeSet(rowObj,columnName,columnValue);

                }
                list.add(rowObj);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBManager.close(conn);
        }
        return list;
    }

    @Override
    public Object queryUniqueRows(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql,clazz,params);
        return (list==null && list.size()>0)? null:list.get(0);
    }

    @Override
    public Object queryValue(String sql, Object[] params) {
        Connection conn = DBManager.getConn();
        Object value = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql);

            JDBCUtils.handleParams(ps,params);

            System.out.println(ps);

            rs = ps.executeQuery();


            while (rs.next()){
                // select count(*) from user
                value = rs.getObject(1);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBManager.close(conn);
        }
        return value;
    }

    @Override
    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql,params);
    }
}
