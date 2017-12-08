package mutara;

import java.sql.ResultSet;

public class PatientCharacteristics {
    public static void patient_Data_Characteristics(DBConnection db) {
        ResultSet r;
//        db.runQuery("SELECT * FROM DEMOGRAPHIC WHERE AGE != 0 GROUP BY ISR; ");// is supposed to pull age and gender from the database. I'm not sure this works if you have other method feel free to use it);
        r = db.runQuery("SELECT * FROM DRUG dr, DEMOGRAPHIC de " +
                "WHERE dr.ISR = de.ISR;");//sets up table that stores the data


        double avg_age = get_avg_age(r);
        double std_age = get_std_age(r);
        double male_female_ratio = get_male_female_ratio(r);
        int numPatients = get_num_of_patients(r);
        System.out.println("The total number of patients is :");
        System.out.println(numPatients);
        System.out.println("average age is :");
        System.out.println(avg_age);
        System.out.println("std age is :");
        System.out.println(std_age);
        System.out.println("male to female ratio is :");
        System.out.println(male_female_ratio);


    }

    public static double get_std_age(ResultSet rs) {
        double std;
        double sum = 0.0;
        double mean = get_avg_age(rs);
        int count = 0;

        try {
            rs.beforeFirst();
            while (rs.next()) {
                sum = sum + Math.pow((getAge(rs) - mean), 2);
                count++;
            }
        } catch (Exception e) {
            System.err.println("Error getting std age: " + e);
        }
        std = Math.sqrt(sum / (count - 1));
        return std;

    }

    public static double get_avg_age(ResultSet rs) {
        double age = 0;
        double sum = 0;
        double average = 0;
        int count = 0;
        try {
            rs.beforeFirst();
            while (rs.next()) {
                age = getAge(rs);
                if(age != 0) {
                    sum += age;
                    count++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting standard age: " + e);
        }
        average = sum / count;
        return average;
    }

    private static double getAge(ResultSet rs) throws Exception{
        double age = 0;
        if (rs.getString("AGE_COD").equals("DEC")) {
            age = Integer.parseInt(rs.getString("AGE")) * 10;
        } else if (rs.getString("AGE_COD").equals("MON")) {
            age = Integer.parseInt(rs.getString("AGE")) / 12;
        } else if (rs.getString("AGE_COD").equals("WK")) {
            age = Integer.parseInt(rs.getString("AGE")) / 52;
        } else if (rs.getString("AGE_COD").equals("DY")) {
            age = Integer.parseInt(rs.getString("AGE")) / 365;
        } else if (rs.getString("AGE_COD").equals("HR")) {
            age = Integer.parseInt(rs.getString("AGE")) / 8760;
        } else if (rs.getString("AGE_COD").equals("YR")) {
            age = Integer.parseInt(rs.getString("AGE")) / 1;
        }
        return age;
    }

    public static double get_male_female_ratio(ResultSet rs) {
        Double male_sum = 0.0;
        Double female_sum = 0.0;
        try {
            rs.beforeFirst();
            while (rs.next()) {
                if (rs.getString("GNDR_COD").equals("M"))
                    male_sum++;
                else
                    female_sum++;
            }
        } catch (Exception e){
            System.err.println("Error getting male female ratio: " + e);
        }
        return male_sum / female_sum;
    }

    public static int get_num_of_patients(ResultSet rs) {
        int totalPatients = 0;
        try {
            rs.beforeFirst();
            while (rs.next()) {
                totalPatients++;
            }
        } catch (Exception e){
            System.err.println("Error getting num patents: " + e);
        }
        return totalPatients;
    }
}