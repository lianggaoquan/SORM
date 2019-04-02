package com.company.sorm.utils;

import com.company.sorm.bean.ColumnInfo;
import com.company.sorm.bean.JavaFieldGetSet;
import com.company.sorm.bean.TableInfo;
import com.company.sorm.core.DBManager;
import com.company.sorm.core.TypeConvertor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了生成Java文件（源代码）常用的操作
 */
public class JavaFileUtils {
    /**
     * 根据字段信息生成java属性信息，如： varchar username -> private String username；以及相应的get/set方法
     * @param column 字段信息
     * @param convertor 类型转换器
     * @return java属性和get/set方法
     */
    public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor){
        JavaFieldGetSet jfgs = new JavaFieldGetSet();
        String javaFieldType = convertor.databaseType2JavaType(column.getDataType());

        jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");

        //生成get方法
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2Upper(column.getName())+"(){\n");
        getSrc.append("\t\treturn "+column.getName()+";\n");
        getSrc.append("\t}\n");

        jfgs.setGetInfo(getSrc.toString());


        //生成set
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void set"+StringUtils.firstChar2Upper(column.getName())+"(");
        setSrc.append(javaFieldType + " "+column.getName()+"){\n");
        setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
        setSrc.append("\t}\n");

        jfgs.setSetInfo(setSrc.toString());

        return jfgs;
    }


    /**
     * 根据表信息生成java类
     * @param tableInfo
     * @param convertor
     * @return
     */
    public static String createJavaSRC(TableInfo tableInfo, TypeConvertor convertor){

        Map<String,ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields = new ArrayList<JavaFieldGetSet>();

        for (ColumnInfo c:columns.values()){
            javaFields.add(createFieldGetSetSRC(c,convertor));
        }

        StringBuilder src = new StringBuilder();
        //生成package语句
        src.append("package "+ DBManager.getConf().getPoPackage()+";\n\n");

        //生成 import 语句
        src.append("import java.sql.*;\n");

        //生成类声明语句
        src.append("public class "+StringUtils.firstChar2Upper(tableInfo.getTname())+" {\n");

        //生成属性列表
        for(JavaFieldGetSet jfgs:javaFields){
            src.append(jfgs.getFieldInfo());
        }

        src.append("\n\n");
        //get
        for(JavaFieldGetSet jfgs:javaFields){
            src.append(jfgs.getGetInfo());
        }

        src.append("\n\n");
        //set
        for(JavaFieldGetSet jfgs:javaFields){
            src.append(jfgs.getSetInfo());
        }

        //生成类结束
        src.append("}\n");
        return src.toString();


    }

    //同步表结构到po包
    public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor){
        String src = createJavaSRC(tableInfo,convertor);

        String srcPath = DBManager.getConf().getSrcPath()+"\\";
        String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.","\\\\");

        File f = new File(srcPath+packagePath);

        //如果目录不存在，建立
        if(!f.exists()){
            f.mkdirs();
        }

        BufferedWriter bw = null;

        try{
            bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()+"/"+StringUtils.firstChar2Upper(tableInfo.getTname())+".java"));
            bw.write(src);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(bw != null){
                    bw.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }



}
