package ua.anon.unfeeling.transportanother;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

class WorkWithDataBase{

    private static Connection connection;

    private static synchronized Connection setInstance(){
        if(connection==null) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                connection = DriverManager.getConnection("jdbc:mysql://ftp.oberig.rv.ua:3306/oberigrv_carstop?noAccessToProcedureBodies=true",
                        "oberigrv_carstop", "potq32ha");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else {
            return connection;
        }

        return connection;
    }

    public String[] getStatus(int id){
        String[] status = new String[5];

        CallableStatement statement;

        try {
            connection = setInstance();
            statement = connection.prepareCall("{call stat_user (?,?,?,?,?,?)}");
            statement.setInt(1, id);
            statement.registerOutParameter(2, Types.SMALLINT);
            statement.registerOutParameter(3, Types.SMALLINT);
            statement.registerOutParameter(4, Types.SMALLINT);
            statement.registerOutParameter(5, Types.SMALLINT);
            statement.registerOutParameter(6, Types.VARCHAR);
            statement.executeQuery();

            status[0]= String.valueOf(statement.getInt(2));
            status[1]= String.valueOf(statement.getInt(3));
            status[2]= String.valueOf(statement.getInt(4));
            status[3]= String.valueOf(statement.getInt(5));
            status[4]= statement.getString(6);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int[] setNumberPhone(Long numberPhone) {
        int[] data = new int[2];

        CallableStatement statement;
        try {
            connection = setInstance();
            statement = connection.prepareCall("{call login_ (?,?,?)}");
            statement.setLong(1, numberPhone);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.registerOutParameter(3,Types.INTEGER);
            statement.executeQuery();

            data[0]=statement.getInt(2);
            data[1]=statement.getInt(3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public double[] search(int id, int driver, int target, double x, double y){
        double[] data = new double[10];

        CallableStatement statement;
        try {
            connection = setInstance();
            statement = connection.prepareCall("{call search_(?,?,?,?,?,?,?,?,?,?,?)}");
            statement.setInt(1,id);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setInt(2, driver);
            statement.setInt(3,target);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.setDouble(4, x);
            statement.setDouble(5, y);
            statement.registerOutParameter(4, Types.DOUBLE);
            statement.registerOutParameter(5, Types.DOUBLE);
            statement.setString(6, "");
            statement.registerOutParameter(7, Types.SMALLINT);
            statement.registerOutParameter(8, Types.SMALLINT);
            statement.registerOutParameter(9,Types.SMALLINT);
            statement.registerOutParameter(10,Types.SMALLINT);
            statement.registerOutParameter(11, Types.SMALLINT);
            statement.executeQuery();

            data[0] = statement.getInt(1);
            data[1] = statement.getDouble(4);
            data[2] = statement.getDouble(5);
            data[3] = statement.getInt(7);
            data[4] = statement.getInt(8);
            data[5] = statement.getInt(9);
            data[6] = statement.getInt(10);
            data[7] = statement.getInt(11);
            data[8] = statement.getInt(3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public double[] search(int id, int driver, int target, double x, double y, String exeption){
        double[] data = new double[10];

        CallableStatement statement;
        try {
            connection = setInstance();
            statement = connection.prepareCall("{call search_(?,?,?,?,?,?,?,?,?,?,?)}");
            statement.setInt(1,id);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setInt(2, driver);
            statement.setInt(3,target);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.setDouble(4, x);
            statement.setDouble(5, y);
            statement.registerOutParameter(4, Types.DOUBLE);
            statement.registerOutParameter(5, Types.DOUBLE);
            statement.setString(6, exeption);
            statement.registerOutParameter(7, Types.SMALLINT);
            statement.registerOutParameter(8, Types.SMALLINT);
            statement.registerOutParameter(9,Types.SMALLINT);
            statement.registerOutParameter(10,Types.SMALLINT);
            statement.registerOutParameter(11, Types.SMALLINT);
            statement.executeQuery();

            data[0] = statement.getInt(1);
            data[1] = statement.getDouble(4);
            data[2] = statement.getDouble(5);
            data[3] = statement.getInt(7);
            data[4] = statement.getInt(8);
            data[5] = statement.getInt(9);
            data[6] = statement.getInt(10);
            data[7] = statement.getInt(11);
            data[8] = statement.getInt(3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void sos(int idUser, @SuppressWarnings("SameParameterValue") int idContact, double x, double y){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call sos_ (" + idUser + ","+idContact+","+x+","+y+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double[] onlineStart(int id, int driver, int target, double x, double y){
        double[] data = new double[9];
        try {
            connection = setInstance();
            CallableStatement statement = connection.prepareCall("call online_start(?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, id);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setInt(2, driver);
            statement.setInt(3, target);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.setDouble(4, x);
            statement.registerOutParameter(4, Types.DOUBLE);
            statement.setDouble(5, y);
            statement.registerOutParameter(5, Types.DOUBLE);
            statement.registerOutParameter(6, Types.SMALLINT);
            statement.registerOutParameter(7, Types.SMALLINT);
            statement.registerOutParameter(8,Types.SMALLINT);
            statement.registerOutParameter(9,Types.SMALLINT);
            statement.registerOutParameter(10, Types.SMALLINT);
            statement.executeQuery();

            data[0] = statement.getInt(1);
            data[8] = statement.getInt(3);
            data[1] = statement.getDouble(4);
            data[2] = statement.getDouble(5);
            data[3] = statement.getInt(6);
            data[4] = statement.getInt(7);
            data[5] = statement.getInt(8);
            data[6] = statement.getInt(9);
            data[7] = statement.getInt(10);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public double[] contactSet(int id, double x, double y){
        double[] xy = new double[2];

        try {
            connection = setInstance();

            CallableStatement statement = connection.prepareCall("call contact_set (?,?,?)");

            statement.setInt(1,id);
            statement.setDouble(2, x);
            statement.setDouble(3, y);

            statement.registerOutParameter(2, Types.DOUBLE);
            statement.registerOutParameter(3,Types.DOUBLE);
            statement.executeQuery();

            xy[0] = statement.getDouble(2);
            xy[1] = statement.getDouble(3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return xy;
    }

    public void contactEnd(int id, int rating){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call contact_end (" + id + ","+rating+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String contactStatus (int id){
        String status="";
        try {
            connection = setInstance();

            CallableStatement statement = connection.prepareCall("call contact_status (?,?)");

            statement.setInt(1, id);
            statement.registerOutParameter(2, Types.VARCHAR);
            statement.executeQuery();

            status = statement.getString(2);
            System.out.println(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int contact (int idUser, int idDriver, double x, double y, int driver){
        int idContact = 0;
        try {
            connection = setInstance();

            CallableStatement statement = connection.prepareCall("call contact_ (?,?,?,?,?)");

            statement.setInt(1,idUser);
            statement.setInt(2,idDriver);
            statement.setDouble(3, x);
            statement.setDouble(4, y);
            statement.setDouble(5, driver);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.executeQuery();

            idContact = statement.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idContact;
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

    public double[] ping(int idUser, int idPing,int driver, double x, double y){
        double[] xy = new double[3];

        CallableStatement statement;
        try {
            connection = setInstance();
            statement = connection.prepareCall("{call ping_ (?,?,?,?,?,?)}");
            statement.setInt(1, idUser);
            statement.setInt(2,idPing);
            statement.setInt(3, driver);
            statement.setDouble(4, x);
            statement.setDouble(5, y);
            statement.registerOutParameter(4, Types.DOUBLE);
            statement.registerOutParameter(5, Types.DOUBLE);
            statement.registerOutParameter(6,Types.BIGINT);
            statement.executeUpdate();

            xy[0] = statement.getDouble(4);
            xy[1] = statement.getDouble(5);
            xy[2] = statement.getInt(6);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return xy;
    }

    public void pingSet(int id, int driver,Double x, Double y){
        try {
            connection = setInstance();
            Statement statement = connection.createStatement();
            statement.execute("call ping_set ("+id+","+driver+"," + x + "," + y + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
