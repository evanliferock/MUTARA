package mutara;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DBDataGetter {
    public enum user{
        USER, NONUSER
    }
    private DBConnection dbConnection;
    // stores the sequences once they are retrieved from the db
    private List<List<Event>> sequences;
    private List<String> drugNames;
    private List<String> diagnosisNames;

    public DBDataGetter(String sshUsername, String sshPassword, String dbUsername,String dbPassword) {
        this.dbConnection = new DBConnection(sshUsername, sshPassword, dbUsername, dbPassword);
        sequences = null;
        drugNames = new ArrayList<>();
        diagnosisNames = new ArrayList<>();
    }


    public List<String> getDrugNames() {
        setupData();
        return drugNames;
    }

    public List<String> getDiagnosisNames() {
        setupData();
        return diagnosisNames;
    }


    /**
     * Gets data for a certain number of people from the MySQL server. It will parse the data into Events that are
     * either Drug or Symptoms. It will return a uneven tables which each row being a list of events. The Data will
     * Be separated into User and Nonuser tables. The data will be aggregated by Case Number
     * @return
     */
    public Map<user ,List<List<Event>>> getUserSequences(String drugName){
        setupData();
        return seperateByDrug(drugName);
    }


    private void setupData(){
        if(sequences == null) {
            dbConnection.setupConnection();
            Map<String, List<Event>> map = new HashMap<>();
            getDrugs(map);
            getDiagnosis(map);
            setupDrugs();
            setupDiagnosis();
            dbConnection.closeConnection();

            sequences = new ArrayList<>();
            Collections.addAll(map.values());

            sequences = sortEvents(sequences);
        }
    }


    private void setupDrugs(){
        ResultSet r = dbConnection.runQuery("SELECT DISTINCT(DRUGNAME) from DRUG;");
        try {
            while (r.next())
                drugNames.add(r.getString("DRUGNAME"));
        } catch (Exception e) {
            System.err.println("Error Setting up drugs: " + e);
        }
    }


    private void setupDiagnosis(){
        ResultSet r = dbConnection.runQuery("SELECT DISTINCT(PT) from REACTION;");
        try {
            while (r.next())
                diagnosisNames.add(r.getString("PT"));
        } catch (Exception e) {
            System.err.println("Error Setting up diagnosis: " + e);
        }
    }


    private void getDrugs(Map<String, List<Event>> map){
        ResultSet r = dbConnection.runQuery("SELECT * FROM DRUG dr, DEMOGRAPHIC de " +
                "WHERE dr.ISR = de.ISR;");
        try {
            while(r.next())
                convertRowToDrug(r, map);
        } catch (Exception e) {
            System.err.println("Error while converting drug row: " + e);
        }
    }


    private void convertRowToDrug(ResultSet r, Map<String, List<Event>> map) throws Exception {
        Drug d = new Drug(getAppropriateDate(false, r), r.getString("DRUGNAME"));
        String caseNum = r.getString("CASE_NUM");
        // TODO
        if(!map.containsKey(caseNum))
            map.put(caseNum, new ArrayList<>());
        map.get(caseNum).add(d);
    }


    private void getDiagnosis(Map<String, List<Event>> map){
        ResultSet r = dbConnection.runQuery("SELECT * FROM REACTION r, DEMOGRAPHIC de " +
                "WHERE r.ISR = de.ISR;");
        try {
            while(r.next())
                convertRowToDiagnosis(r, map);
        } catch (Exception e) {
            System.err.println("Error while converting diagnosis row: " + e);
        }
    }


    private void convertRowToDiagnosis(ResultSet r, Map<String, List<Event>> map) throws Exception {
        Diagnosis d = new Diagnosis(getAppropriateDate(true, r), r.getString("PT"));
        String caseNum = r.getString("CASE_NUM");
        // TODO
        if(!map.containsKey(caseNum))
            map.put(caseNum, new ArrayList<>());
        map.get(caseNum).add(d);
    }


    private LocalDate getAppropriateDate(Boolean isDiagnosis, ResultSet r) throws SQLException{
        LocalDate date = null;
        if(r.getDate("REPT_DT") != null)
            date = r.getDate("REPT_DT").toLocalDate();
        else
            date = r.getDate("FDA_DT").toLocalDate();
        return date;
    }


    private List<List<Event>> sortEvents(List<List<Event>> events){
        // TODO
        return new ArrayList<>();
    }


    private Map<user ,List<List<Event>>> seperateByDrug(String drugName){
        // TODO
        return new HashMap<>();
    }
}
