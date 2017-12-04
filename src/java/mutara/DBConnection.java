package mutara;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * Get the JAR to make this work from http://www.jcraft.com/jsch/
 * Add it to lib folder in project directory and add the folder as a library
 * in the IDE
 */
public class DBConnection{
    private Connection con;
    private Session session;
    private String sshUsername;
    private String sshPassword;
    private String dbUsername;
    private String dbPassword;
    private boolean isSetup;

    public DBConnection(String sshUsername, String sshPassword, String dbUsername, String dbPassword) {
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.con = null;
        this.session = null;
        this.isSetup = false;
    }

    public void setupConnection(){
        if(!isSetup) {
            try {
                int localPort = 5656;
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


                String url = "jdbc:mysql://127.0.0.1:" + localPort + "/esrock_DB?zeroDateTimeBehavior=convertToNull";
                try {
                    con = DriverManager.getConnection(url, dbUsername, dbPassword);
                    System.out.println ("Database connection established");
                } catch (Exception e) {
                    System.err.println("Error on setting up connection: " + e);
                }
            } catch (JSchException e) {
                System.err.println("Error connection to ssh: " + e);
            }
            isSetup = true;
        }
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
            if(session != null && session.isConnected()){
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error on closing ssh: " + e);
        }
        isSetup = false;
    }

}