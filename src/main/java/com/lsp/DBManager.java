package com.lsp;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/*
 * Created by lsp on 05.10.15.
 */
class DBManager {
    private Connection connection = null;
    private String DB_URL;
    private String DB_USERID;
    private String DB_PASSWORD;

    public DBManager() {
        //this.DB_URL = "jdbc:mysql://localhost/mosadm";
        //this.DB_USERID = "mosadm";
        //this.DB_PASSWORD = "mosadm";
        this.DB_URL = "jdbc:mysql://192.168.32.102/mosDBase?useUnicode=true&characterEncoding=UTF-8";
        this.DB_USERID = "mosdbase";
        this.DB_PASSWORD = "mosdbase";
        this.getConnection();
    }
    public DBManager(String DB_HOST, String DB, String DB_USERID, String DB_PASSWORD) {
        this.DB_URL = "jdbc:mysql://l"+DB_HOST+"/"+DB;
        this.DB_USERID = DB_USERID;
        this.DB_PASSWORD = DB_PASSWORD;
        this.getConnection();
    }

    private Connection getConnection()
    {
        if (connection == null)
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USERID, DB_PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
        }
    public Statement getStatement(){
        Connection conn=getConnection();
        java.sql.Statement st= null;
        try {
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return st;
    }
    public void closeStatement(Statement st){
        try {
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //closeConnection();
    }
    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putMapIntoDB(Map<String, String> map,String table,String key,String value) throws SQLException {
        Statement stm = getStatement();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //UPDATE Computers SET value=(Value from map) WHERE key=(Key from map);
            //INSERT INTO Computers SET value=(Value from map) WHERE key=(Key from map);
            int rs = stm.executeUpdate("UPDATE "
                    + table+" SET "
                    +value+"=\'"
                    + entry.getValue()
                    + "\' WHERE "
                    +key +"=\'"
                    + entry.getKey()
                    + "\';");
            if (rs == 0) {
                stm.executeUpdate("INSERT INTO "
                        + table + " SET "
                        + value + "=\'"
                        + entry.getValue()
                        + "\',"
                        + key + "=\'"
                        + entry.getKey()
                        + "\';");
            }
        }
        closeStatement(stm);
    }
    public void addMapArrayIntoDB(Map<String, String[]> map,String table,String key,String value1,String value2) throws SQLException {
        Statement stm = getStatement();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            //UPDATE Computers SET value=(Value from map) WHERE key=(Key from map);
            //INSERT INTO Computers SET value=(Value from map) WHERE key=(Key from map);
            if(!stm.executeQuery("SELECT * FROM " + table + " WHERE " + key + "=\"" + entry.getKey() + "\";").first()){
                stm.executeUpdate("INSERT INTO "
                        + table + " SET "
                        + value1 + "=\""
                        + entry.getValue()[0]
                        + "\","
                        + value2 + "=\""
                        + entry.getValue()[1]
                        + "\","
                        + key + "=\""
                        + entry.getKey()
                        + "\";");
            }
        }
        closeStatement(stm);
    }
    public void testGetDB() throws SQLException {
        Statement stm=getStatement();
        ResultSet res=stm.executeQuery(Computer.getSqlSelect());
        while (res.next()){
            System.out.println(res.toString());
        }
    }
    public ArrayList<Computer> getListComputerFromDB() throws SQLException {
        Statement stm = getStatement();
        ResultSet res=stm.executeQuery(Computer.getSqlSelect());
        ArrayList<Computer> list=new ArrayList<Computer>();
        while (res.next()){
            String[]ar = new String[Computer.getNames().length];
            for (int i = 0; i < Computer.getNames().length; i++) {
                ar[i]=res.getString(i+2);
            }
            list.add(new Computer(res.getInt(1), ar,new User("testuser")));
        }
        res.close();
        //closeStatement(stm);
        return list;
    }
    public ArrayList<User> getListUserFromDB() throws SQLException {
        Statement stm = getStatement();
        ResultSet res=stm.executeQuery(User.getSqlSelect());
        ArrayList<User> list=new ArrayList<User>();
        while (res.next()){
            String[]ar = new String[User.getNames().length];
            for (int i = 0; i < User.getNames().length; i++) {
                ar[i]=res.getString(i+2);
            }
            list.add(new User(res.getInt(1), ar));
        }
        res.close();
        //closeStatement(stm);
        return list;
    }
    public void saveComputerDB(Collection<Computer> computers) throws SQLException {
        Statement stm=getStatement();
        Iterator<Computer> iter=computers.iterator();
        while(iter.hasNext()) {
            Computer computer=iter.next();
            if(computer.isUpdated()){
                int rs=stm.executeUpdate(computer.getSqlUpdate());
                if(rs==0){
                    stm.executeUpdate(computer.getSqlInsert());
                }
                computer.setIsUpdated(false);
            }
            if(computer.isDeleted()){
                stm.executeUpdate(computer.getSqlDelete());
                iter.remove();
            }
        }
    }

    public void saveUserDB(Collection<User> users) throws SQLException {
        Statement stm=getStatement();
        Iterator<User> iter=users.iterator();
        while(iter.hasNext()) {
            User user=iter.next();
            if(user.isUpdated()){
                int rs=stm.executeUpdate(user.getSqlUpdate());
                if(rs==0){
                    stm.executeUpdate(user.getSqlInsert());
                }
                user.setIsUpdated(false);
            }
            if(user.isDeleted()){
                stm.executeUpdate(user.getSqlDelete());
                iter.remove();
            }
        }
    }
}
