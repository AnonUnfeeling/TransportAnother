package com.example.hjk.testing;

import android.app.Activity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class WorkWithDataBase extends Activity{

    private static Connection connection=null;

    public static synchronized Connection setInstance(){
        if(connection==null) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                 return DriverManager.getConnection("jdbc:mysql://77.222.139.193:3306/carstop", "root", "");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public int setNumberPhone(Long numberPhone) throws SQLException {
        connection = setInstance();
        CallableStatement statement = null;
        try {
            statement = connection.prepareCall("{call login_ (?,?,?,?,?,?,?)}");
            statement.setLong(1, numberPhone);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.registerOutParameter(3, Types.TINYINT);
            statement.registerOutParameter(4, Types.SMALLINT);
            statement.registerOutParameter(5, Types.SMALLINT);
            statement.registerOutParameter(6, Types.INTEGER);
            statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement.getInt(2);
    }

    public void onlineStart(int id){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call online_start (" + id + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onlineEnd(int id){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call online_end (" + id + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCoordinates(int id,Double x, Double y){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call ping_set ("+id+",1," + x + "," + y + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
