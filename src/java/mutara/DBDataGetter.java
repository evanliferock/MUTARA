package mutara;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class DBDataGetter {
    public enum user{
        USER, NONUSER
    }
    private String username;
    private String password;
    private Connection con;
    private Drug d;

    public DBDataGetter(String username, String password, Drug d) {
        this.username = username;
        this.password = password;
        this.d = d;
        setupConnection();
    }


    private void setupConnection(){
        String url = "jdbc:mysql://147.222.163.1:3306/putYourDBHere";
        try {
            con = DriverManager.getConnection(url, "putYourUser", "PutYourPass");
        } catch (Exception e) {
            System.err.println("Error on setting up connection: " + e);
        }
    }

    /**
     * Gets data for a certain number of people from the MySQL server. It will parse the data into Events that are
     * either Drug or Symptoms. It will return a uneven tables which each row being a list of events. The Data will
     * Be separated into User and Nonuser tables. The data will be aggregated by Case Number
     * @return
     */
    public Map<user ,List<List<Event>>> getUserSequences(){
        Map<String, List<Event>> map = new HashMap<>();
        getDrugs(map);
        getDiagnosis(map);

        List<List<Event>> sequences = new ArrayList<>();
        Collections.addAll(map.values());

        return sortEvents(sequences);
    }


    private void getDrugs(Map<String, List<Event>> map){
        ResultSet r = runQuery("SELECT * FROM DRUG dr, DEMOGRAPHIC de " +
                "WHERE dr.ISR = de.ISR;");
        try {
            while(r.next())
                convertRowToDrug(r, map);
        } catch (Exception e) {
            System.err.println("Error while getting drugs");
        }
    }


    private void convertRowToDrug(ResultSet r, Map<String, List<Event>> map) throws Exception {
        Drug d = new Drug();
        String caseNum = r.getString("CASE_NUM");
        // TODO
        if(!map.containsKey(caseNum))
            map.put(caseNum, new ArrayList<>());
        map.get(caseNum).add(d);
    }


    private void getDiagnosis(Map<String, List<Event>> map){
        ResultSet r = runQuery("SELECT * FROM REACTION r, DEMOGRAPHIC de " +
                "WHERE r.ISR = de.ISR;");
        try {
            while(r.next())
                convertRowToDiagnosis(r, map);
        } catch (Exception e) {
            System.err.println("Error while getting drugs");
        }
    }


    private void convertRowToDiagnosis(ResultSet r, Map<String, List<Event>> map) throws Exception {
        Diagnosis d = new Diagnosis();
        String caseNum = r.getString("CASE_NUM");
        // TODO
        if(!map.containsKey(caseNum))
            map.put(caseNum, new ArrayList<>());
        map.get(caseNum).add(d);
    }


    private Map<user ,List<List<Event>>> sortEvents(List<List<Event>> events){
        return new HashMap<>();
    }


    private ResultSet runQuery(String query){
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
}
