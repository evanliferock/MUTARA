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

    public DBDataGetter(String sshUsername, String sshPassword, String dbUsername,String dbPassword) {
        this.dbConnection = new DBConnection(sshUsername, sshPassword, dbUsername, dbPassword);
        sequences = null;
        drugNames = new ArrayList<>();
    }

    public DBDataGetter(DBConnection dbConnection){
        this.dbConnection = dbConnection;
        sequences = null;
        drugNames = new ArrayList<>();
    }


    public List<String> getDrugNames() {
        setupData();
        return drugNames;
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

    public List<String> getReactionsForDrug(String drug){
        setupData();
        String newDrug = drug.replace("'", "\\'");
        List<String> reactions = new ArrayList<>();
        ResultSet r = dbConnection.runQuery("SELECT DISTINCT(PT) FROM DRUG d, REACTION r " +
                "WHERE d.ISR = r.ISR AND d.DRUGNAME = ?;", drug);
        try {
            while (r.next())
                reactions.add(r.getString("PT"));
        } catch (Exception e) {
            System.err.println("Error getting reactions for: " + drug + ", " + e);
        }
        return reactions;
    }


    private void setupData(){
        if(sequences == null) {
            dbConnection.setupConnection();
            Map<String, List<Event>> map = new HashMap<>();
            getDrugs(map);
            getDiagnosis(map);
            setupDrugs();

            sequences = Collections.synchronizedList(new ArrayList<>(map.values()));

            sortSequences();
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


    private void sortSequences(){
//        this.numbers = values;
//        number = values.length;
        List<Event> helper;

        for(List<Event> sequence : sequences) {
            helper = new ArrayList<>(sequence);
            mergesort(0, sequence.size() - 1, sequence, helper);
        }
    }

    // From http://www.vogella.com/tutorials/JavaAlgorithmsMergesort/article.html
    private void mergesort(int low, int high, List<Event> events, List<Event> helper) {
        // check if low is smaller than high, if not then the array is sorted
        if (low < high) {
            // Get the index of the element which is in the middle
            int middle = low + (high - low) / 2;
            // Sort the left side of the array
            mergesort(low, middle, events, helper);
            // Sort the right side of the array
            mergesort(middle + 1, high, events, helper);
            // Combine them both
            merge(low, middle, high, events, helper);
        }
    }

    // From http://www.vogella.com/tutorials/JavaAlgorithmsMergesort/article.html
    private void merge(int low, int middle, int high, List<Event> events, List<Event> helper) {

        // Copy both parts into the helper array
        for (int i = low; i <= high; i++) {
            helper.set(i,events.get(i));
        }

        int i = low;
        int j = middle + 1;
        int k = low;
        // Copy the smallest values from either the left or the right side back
        // to the original array
        while (i <= middle && j <= high) {
            // i is before j or (i is the same day as j and i is a drug) then i goes before j else otherwise
            if (helper.get(i).getDate().compareTo(helper.get(j).getDate()) < 0 ||
                    (helper.get(i).getDate().equals(helper.get(j).getDate()) &&
                            helper.get(i) instanceof Drug)) {
                events.set(k, helper.get(i));
                i++;
            } else {
                events.set(k, helper.get(j));
                j++;
            }
            k++;
        }
        // Copy the rest of the left side of the array into the target array
        while (i <= middle) {
            events.set(k, helper.get(i));
            k++;
            i++;
        }
        // Since we are sorting in-place any leftover elements from the right side
        // are already at the right position.

    }


    private Map<user ,List<List<Event>>> seperateByDrug(String drugName){
        Map<user, List<List<Event>>> map = null;
        if(sequences != null) {
            map = new HashMap<>();
            map.put(user.USER, new ArrayList<>());
            map.put(user.NONUSER, new ArrayList<>());
            boolean isUser;
            for (List<Event> list : sequences) {
                isUser = false;
                for (Event event : list) {
                    if (event instanceof Drug && ((Drug) event).getDrugName().equals(drugName)) {
                        isUser = true;
                        break;
                    }
                }
                if(isUser)
                    map.get(user.USER).add(list);
                else
                    map.get(user.NONUSER).add(list);
            }

        }
        return map;
    }
}
