

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * <pre>
 * DO类生成工具 
 * 侵权必究^_^
 * </pre>
 * 
 * @author chenke
 * @date 2014-6-19 下午12:48:45
 */
public class DataobjectMaker {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://192.168.107.211:33062/sso";
        String username = "root";
        String passwd = "root";
        String tableName = "users";
        Connection conn = DriverManager.getConnection(url, username, passwd);
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
        String tbStrs[] = tableName.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tbStrs.length; i++) {
            sb.append(tbStrs[i].substring(0, 1).toUpperCase()).append(
                    tbStrs[i].substring(1).toLowerCase());
        }
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(sb.toString()
                + "DO.java"));
        OutputStreamWriter sqlOs = new OutputStreamWriter(new FileOutputStream("sql.txt"));
        StringBuilder sqlsb = new StringBuilder();
        StringBuilder writesb = new StringBuilder();
        writesb.append("/*this file is auto make by DataobjectMaker author is chenke*/\r\n");
        writesb.append("public class" + " " + sb.toString() + "DO implements Serializable {\r\n");
        writesb.append("private static final long serialVersionUID = 1L;\r\n");
        sqlsb.append("select ");
        while (colRet.next()) {
            String columnName = colRet.getString("COLUMN_NAME");
            String columnType = colRet.getString("TYPE_NAME");
            //            int datasize = colRet.getInt("COLUMN_SIZE");
            //            int digits = colRet.getInt("DECIMAL_DIGITS");
            //            int nullable = colRet.getInt("NULLABLE");
            sqlsb.append(columnName).append(",");
            String type = columnType;
            if (columnType.equalsIgnoreCase("BIGINT")) {
                type = "Long";
            } else if (columnType.toUpperCase().contains("INT")) {
                type = "Integer";
            } else if (columnType.toUpperCase().contains("TIME")) {
                type = "Date";
            } else if (columnType.toUpperCase().contains("CHAR")) {
                type = "String";
            } else if (columnType.equalsIgnoreCase("BIT")) {
                type = "Boolean";
            }

            String cnStrs[] = columnName.split("_");
            StringBuilder nameSB = new StringBuilder();
            for (int i = 0; i < cnStrs.length; i++) {
                if (i == 0) {
                    nameSB.append(cnStrs[i].substring(0, 1).toLowerCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                } else {
                    nameSB.append(cnStrs[i].substring(0, 1).toUpperCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                }
            }
            writesb.append("private ").append(type).append(" ").append(nameSB).append(";\r\n");
        }

        String sql = sqlsb.toString().substring(0, sqlsb.toString().length() - 1) + " from "
                + tableName;
        sqlOs.write(sql);
        sqlOs.flush();
        sqlOs.close();
        //================================flow will create get/set method=========================      
        colRet.beforeFirst();

        while (colRet.next()) {
            String columnName = colRet.getString("COLUMN_NAME");
            String columnType = colRet.getString("TYPE_NAME");
            //            int datasize = colRet.getInt("COLUMN_SIZE");
            //            int digits = colRet.getInt("DECIMAL_DIGITS");
            //            int nullable = colRet.getInt("NULLABLE");
            String type = columnType;
            if (columnType.equalsIgnoreCase("BIGINT")) {
                type = "Long";
            } else if (columnType.toUpperCase().contains("INT")) {
                type = "Integer";
            } else if (columnType.toUpperCase().contains("TIME")) {
                type = "Date";
            } else if (columnType.toUpperCase().contains("CHAR")) {
                type = "String";
            } else if (columnType.equalsIgnoreCase("BIT")) {
                type = "Boolean";
            }

            String cnStrs[] = columnName.split("_");
            StringBuilder nameSB = new StringBuilder();
            for (int i = 0; i < cnStrs.length; i++) {
                nameSB.append(cnStrs[i].substring(0, 1).toUpperCase()).append(
                        cnStrs[i].substring(1).toLowerCase());
            }
            StringBuilder paramSB = new StringBuilder();
            for (int i = 0; i < cnStrs.length; i++) {
                if (i == 0) {
                    paramSB.append(cnStrs[i].substring(0, 1).toLowerCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                } else {
                    paramSB.append(cnStrs[i].substring(0, 1).toUpperCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                }
            }
            //set method
            writesb.append("public void set").append(nameSB).append("(").append(type).append(" ")
                    .append(paramSB).append(")").append(" {\r\n");
            writesb.append("    ").append("this.").append(paramSB).append("=").append(paramSB)
                    .append(";\r\n");
            writesb.append("}\r\n");

            //get method
            writesb.append("public ").append(type).append(" get").append(nameSB).append("(")
                    .append(")").append(" {\r\n");
            writesb.append("    return ").append("this.").append(paramSB).append(";\r\n");
            writesb.append("}\r\n");
        }

        writesb.append("}");
        os.write(writesb.toString());
        os.flush();
        os.close();

        //==============================follow auto make sqlmapping file======================
        colRet.beforeFirst();
        StringBuilder sqlmapSb = new StringBuilder();
        OutputStreamWriter sqlmapOS = new OutputStreamWriter(new FileOutputStream("sqlmap-mapping-"
                + sb.toString().toLowerCase() + ".xml"));
        sqlmapSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
        sqlmapSb.append("<!DOCTYPE sqlMap PUBLIC \"-//iBATIS.com//DTD SQL Map 2.0//EN\" \"http://www.ibatis.com/dtd/sql-map-2.dtd\">\r\n");
        sqlmapSb.append("<!-- ========================================================== -->\r\n");
        sqlmapSb.append("<!-- Configuration for ibatis sqlmap mapping. -->\r\n");
        sqlmapSb.append("<!-- ========================================================== -->\r\n");
        sqlmapSb.append("<sqlMap namespace=\"").append(sb).append("\">\r\n");
        sqlmapSb.append("   <typeAlias alias=\"TA-").append(sb.toString().toUpperCase())
                .append("\" type=\"xxx." + sb.toString() + "DO\" />\r\n");
        sqlmapSb.append("   <resultMap id=\"RM-" + sb.toString().toUpperCase() + "\" class=\"TA-"
                + sb.toString().toUpperCase() + "\">\r\n");

        while (colRet.next()) {
            String columnName = colRet.getString("COLUMN_NAME");
            String columnType = colRet.getString("TYPE_NAME");
            //            int datasize = colRet.getInt("COLUMN_SIZE");
            //            int digits = colRet.getInt("DECIMAL_DIGITS");
            //            int nullable = colRet.getInt("NULLABLE");
            String type = columnType;
            if (columnType.equalsIgnoreCase("BIGINT")) {
                type = "java.lang.Long";
            } else if (columnType.toUpperCase().contains("INT")) {
                type = "java.lang.Integer";
            } else if (columnType.toUpperCase().contains("TIME")) {
                type = "java.util.Date";
            } else if (columnType.toUpperCase().contains("CHAR")) {
                type = "java.lang.String";
            } else if (columnType.equalsIgnoreCase("BIT")) {
                type = "java.lang.Boolean";
            }

            String cnStrs[] = columnName.split("_");
            StringBuilder nameSB = new StringBuilder();
            for (int i = 0; i < cnStrs.length; i++) {
                if (i == 0) {
                    nameSB.append(cnStrs[i].substring(0, 1).toLowerCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                } else {
                    nameSB.append(cnStrs[i].substring(0, 1).toUpperCase()).append(
                            cnStrs[i].substring(1).toLowerCase());
                }
            }

            sqlmapSb.append("    <result property=\"" + nameSB + "\" column=\"" + columnName
                    + "\" jdbcType=\"" + columnType + "\" javaType=\"" + type + "\" />\r\n");

        }

        sqlmapSb.append("   </resultMap>\r\n");
        sqlmapSb.append("   <!-- flow is your sql patten -->\r\n");

        sqlmapSb.append("</sqlMap>\r\n");
        sqlmapOS.write(sqlmapSb.toString());
        sqlmapOS.flush();
        sqlmapOS.close();

    }

}
