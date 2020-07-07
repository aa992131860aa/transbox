package com.life.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

/**
 * 数据库连接类
 * 说明:封装了 无参，有参，存储过程的调用
 *
 * @author iflytek
 */
public class ConnectionDB {

    /**
     * 数据库驱动类名称
     */
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    /**
     * 连接字符串
     */
    //private static final String URLSTR = "jdbc:mysql://localhost/transbox?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";

    private static final String URLSTR = "jdbc:mysql://rm-bp1xi42vbwjf5c5duio.mysql.rds.aliyuncs.com/transbox?useUnicode=true&characterEncoding=UTF-8";
     //private static final String URLSTR = "jdbc:mysql://47.98.226.53/transbox?useUnicode=true&characterEncoding=UTF-8";

    /**
     * 用户名
     */
    private static final String USERNAME = "transbox";

    /**
     * 密码
     */
    private static final String USERPASSWORD = "transboxa!d@m#i$n%1(2&3^A";

    /**
     * 创建数据库连接对象
     */
    private Connection connnection = null;

    /**
     * 创建PreparedStatement对象
     */
    private PreparedStatement preparedStatement = null;

    /**
     * 创建CallableStatement对象
     */
    private CallableStatement callableStatement = null;

    /**
     * 创建结果集对象
     */
    private ResultSet resultSet = null;

    static {
        try {
            // 加载数据库驱动程序  
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("数据库加载驱动错误:" + e.getMessage());

        }
    }

    /**
     * 建立数据库连接
     *
     * @return 数据库连接
     */
    public Connection getConnection() {
        try {
            // 获取连接  
            connnection = DriverManager.getConnection(URLSTR, USERNAME,
                    USERPASSWORD);

        } catch (SQLException e) {
            System.out.println("数据库连接错误:" + e.getMessage());
            CommUtils.insertTransboxErrorFile("getConnection="+e.getMessage()+"=数据库连接错误");

        }
        return connnection;
    }

    public static void main(String[] args) {
        new ConnectionDB().getConnection();
    }

    /**
     * insert update delete SQL语句的执行的统一方法
     *
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 受影响的行数
     */
    public int executeUpdate(String sql, Object[] params) {
        // 受影响的行数  
        int affectedLine = 0;

        try {
            // 获得连接  
            connnection = this.getConnection();
            // 调用SQL   
            preparedStatement = connnection.prepareStatement(sql);

            // 参数赋值  
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行  
            affectedLine = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            CommUtils.insertTransboxErrorFile("executeUpdate="+e.getMessage()+"=数据库插入信息错误");

        } finally {
            // 释放资源  
            closeAll();
        }
        return affectedLine;
    }

    /**
     * SQL 查询将查询结果直接放入ResultSet中
     *
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 结果集
     */
    private ResultSet executeQueryRS(String sql, Object[] params) {
        try {
            // 获得连接  
            connnection = this.getConnection();

            // 调用SQL  
            preparedStatement = connnection.prepareStatement(sql);

            // 参数赋值  
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行  
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            CommUtils.insertTransboxErrorFile("executeQueryRS="+e.getMessage()+"=数据库查询信息错误");

        }

        return resultSet;
    }
//    public void getExport(String sql,String transferId){
//    	 // 获得连接  
//        connnection = this.getConnection();  
//        ResultSet rs =null;
//        PreparedStatement ps = null;
//        
//        try {
//			ps = connnection.prepareStatement(sql);
//			ps.setString(1, transferId);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				rs.getArray(columnIndex)
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
//      

    /**
     * SQL 查询将查询结果：一行一列
     *
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 结果集
     */
    public Object executeQuerySingle(String sql, Object[] params) {
        Object object = null;
        try {
            // 获得连接  
            connnection = this.getConnection();

            // 调用SQL  
            preparedStatement = connnection.prepareStatement(sql);

            // 参数赋值  
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行  
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                object = resultSet.getObject(1);
            }

        } catch (SQLException e) {
            CommUtils.insertTransboxErrorFile("executeQuerySingle="+e.getMessage()+"=数据库单个查询错误");

        } finally {
            closeAll();
        }

        return object;
    }

    /**
     * 获取结果集，并将结果放在List中
     *
     * @param sql SQL语句
     * @return List
     * 结果集
     */
    public List<Object> excuteQuery(String sql, Object[] params) {
        // 执行SQL获得结果集  
        ResultSet rs = executeQueryRS(sql, params);

        // 创建ResultSetMetaData对象  
        ResultSetMetaData rsmd = null;

        // 结果集列数  
        int columnCount = 0;
        try {
            rsmd = rs.getMetaData();

            // 获得结果集列数  
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e1) {
            CommUtils.insertTransboxErrorFile("executeQuery="+e1.getMessage()+"=数据库查询错误");

        }

        // 创建List  
        List<Object> list = new ArrayList<Object>();

        try {
            // 将ResultSet的结果保存到List中  
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            CommUtils.insertTransboxErrorFile("executeQuery="+e.getMessage()+"=数据库查询保存错误");

        } finally {
            // 关闭所有资源  
            closeAll();
        }

        return list;
    }

    /**
     * 存储过程带有一个输出参数的方法
     *
     * @param sql         存储过程语句
     * @param params      参数数组
     * @param outParamPos 输出参数位置
     * @param SqlType     输出参数类型
     * @return 输出参数的值
     */
    public Object excuteQuery(String sql, Object[] params, int outParamPos, int SqlType) {
        Object object = null;
        connnection = this.getConnection();
        try {
            // 调用存储过程  
            callableStatement = connnection.prepareCall(sql);

            // 给参数赋值  
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    callableStatement.setObject(i + 1, params[i]);
                }
            }

            // 注册输出参数  
            callableStatement.registerOutParameter(outParamPos, SqlType);

            // 执行  
            callableStatement.execute();

            // 得到输出参数  
            object = callableStatement.getObject(outParamPos);

        } catch (SQLException e) {

        } finally {
            // 释放资源  
            closeAll();
        }

        return object;
    }

    /**
     * 关闭所有资源
     */
    private void closeAll() {
        // 关闭结果集对象  
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                CommUtils.insertTransboxErrorFile("closeAll()="+e.getMessage()+"=数据库关闭resultSet错误");

            }
        }

        // 关闭PreparedStatement对象  
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                CommUtils.insertTransboxErrorFile("closeAll()="+e.getMessage()+"=数据库关闭preparedStatement错误");

            }
        }

        // 关闭CallableStatement 对象  
        if (callableStatement != null) {
            try {
                callableStatement.close();
            } catch (SQLException e) {
                CommUtils.insertTransboxErrorFile("closeAll()="+e.getMessage()+"=数据库关闭callableStatement错误");

            }
        }

        // 关闭Connection 对象  
        if (connnection != null) {
            try {
                connnection.close();
            } catch (SQLException e) {
                CommUtils.insertTransboxErrorFile("closeAll()="+e.getMessage()+"=数据库关闭connnection错误");

            }
        }
    }

    /**
     * 关闭所有资源
     */
    public void closeAll(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        // 关闭结果集对象  
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("closeAll sql resultSet:" + e.getMessage());
                CommUtils.insertTransboxErrorFile("closeAll="+e.getMessage()+"=数据库关闭resultSet错误");

            }
        }

        // 关闭PreparedStatement对象  
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("closeAll sql preparedStatement:" + e.getMessage());
                CommUtils.insertTransboxErrorFile("closeAll="+e.getMessage()+"=数据库关闭preparedStatement错误");

            }
        }


        // 关闭Connection 对象  
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("closeAll sql connection" + e.getMessage());
                CommUtils.insertTransboxErrorFile("closeAll="+e.getMessage()+"=数据库关闭connection错误");

            }
        }
    }
}  