package mutara;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Get the JAR to make this work from http://www.jcraft.com/jsch/
 * Add it to lib folder in project directory and add the folder as a library
 * in the IDE
 */
public class DBConnection{
    private Connection con;
    private static int localPort;
    private static Session session;
    private String sshUsername;
    private String sshPassword;
    private String dbUsername;
    private String dbPassword;
    private static boolean isSetup;

    static {
        session = null;
        localPort = 5656;
        isSetup = false;
    }

    public DBConnection(String sshUsername, String sshPassword, String dbUsername, String dbPassword) {
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.con = null;
    }

    public DBConnection provideNewConnection(){
        return new DBConnection(sshUsername, sshPassword, dbUsername, dbPassword);
    }

    public void setupConnection(){
        if(con == null) {
            this.con = getNewConnection(sshUsername, sshPassword, dbUsername, dbPassword);
        }
    }

    public static Connection getNewConnection(String sshUsername, String sshPassword, String dbUsername, String dbPassword){
        if(!isSetup) {
            try {
                int destiniationPort = 3306;
                String sqlIP = "147.222.163.1";
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                session = jsch.getSession(sshUsername, sqlIP, 22);
                session.setPassword(sshPassword);
                session.setConfig(config);
                session.connect();
                System.out.println("Connected");
                int assinged_port = session.setPortForwardingL(localPort, sqlIP, destiniationPort);

            } catch (JSchException e) {
                System.err.println("Error connection to ssh: " + e);
            }
            isSetup = true;
        }
        Connection con = null;
        String url = "jdbc:mysql://127.0.0.1:" + localPort + "/esrock_DB?zeroDateTimeBehavior=convertToNull";
        try {
            con = DriverManager.getConnection(url, dbUsername, dbPassword);
            System.out.println ("Database connection established");
        } catch (Exception e) {
            System.err.println("Error on setting up connection: " + e);
        }
        return con;
    }


    public ResultSet runQuery(String query, String drug){
        setupConnection();
        PreparedStatement s = null;
        try {
            s = con.prepareStatement(query);
            s.setString(1, drug);
        } catch (Exception e) {
            System.err.println("Error on getting a Statement: " + e);
        }
        ResultSet r = null;
        try {
            r = s.executeQuery();
        } catch (Exception e) {
            System.err.println("Error on running query with drug: " + drug + ", " + e);
        }
        return r;
    }


    public ResultSet runQuery(String query){
        setupConnection();
        Statement s = null;
        try {
            s = con.createStatement();
        } catch (Exception e) {
            System.err.println("Error on getting a Statement: " + e);
        }
        ResultSet r = null;
        try {
            r = s.executeQuery(query);
        } catch (Exception e) {
            System.err.println("Error on running query: " + e);
        }
        return r;
    }


    public void closeConnection(){
        try {
            if(con != null && !con.isClosed()){
                System.out.println("Closing Database Connection");
                con.close();
            }
        } catch (Exception e) {
            System.err.println("Error on closing sql connection: " + e);
        }
        try {
            if(isSetup && session != null && session.isConnected()){
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error on closing ssh: " + e);
        }
        isSetup = false;
    }

}