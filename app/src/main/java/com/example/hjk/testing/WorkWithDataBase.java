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

    public void setNumberPhone(Long numberPhone){
        connection = setInstance();
        try {
            CallableStatement statement = connection.prepareCall("{CALL login_ (?,?,?)}");
            statement.setLong(1,numberPhone);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.registerOutParameter(3, Types.TINYINT);
            statement.executeQuery();
            System.out.println(statement.getInt(2)+" "+statement.getInt(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCoordinates(Double x, Double y){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call set_xy ( 67,1,"+x+","+y+");");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
