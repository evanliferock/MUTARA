package mutara;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args){
        DBConnection dbConnection = new DBConnection("sshusername: look at lab 2 for what these credentials should be",
                "sshpassword",
                "dbusernam",
                "dbpassword");
        DBDataGetter db = new DBDataGetter(dbConnection);
        PatientCharacteristics.patient_Data_Characteristics(dbConnection);
        List<String> drugs = db.getDrugNames();
        List<String> diagnosis = db.getDiagnosisNames();
        dbConnection.closeConnection();
        /*
        1. Initialise parameters, such as the antecedent A, event types of interest, the study
        period [tS, tE], time period lengths Te, Tr, Tb, and Tc.
                - The antecedent A is specified to restrict the search space, e.g., A6. The
                    sequences having A are called user sequences, and otherwise nonuser
                    sequences.
                – Event types of interest determine the possible candidates for the consequent
                    C, e.g., diagnoses C1-C5.
                – A study period is specified by [tS, tE] according to the antecedent A. User
                    sequences that do not contain A in this period are ignored.
                – The time lengths Te, Tr, Tb, and Tc indicate lengths of, respectively, the effect
                    period, the reference period, the period between the first A and the starting
                    point of the reference period, and the control period as illustrated in Fig. 1.
        */
        Scanner in = new Scanner(System.in);
        System.out.println("How many different randomly selected Drugs would you like to test?");
        System.out.println("There are " + drugs.size() + " different drugs. Some of these drugs are bad data");
        System.out.print("Input an Integer: ");
        int numDrugs = Math.abs(in.nextInt());
        numDrugs = (numDrugs > drugs.size() ? drugs.size() : numDrugs);

        System.out.println("How many different randomly selected diagnosis would you like to test?");
        System.out.println("There are " + diagnosis.size() + " different diagnosis. Some of these diagnosis are bad data");
        System.out.print("Input an Integer: ");
        int numDiagnosis = Math.abs(in.nextInt());
        numDiagnosis = (numDiagnosis > diagnosis.size() ? diagnosis.size() : numDiagnosis);

        Collections.shuffle(drugs);
        Collections.shuffle(diagnosis);

        List<String> drugNamesToCheck = new ArrayList<>();

        for(int i = 0; i < numDrugs; i++){
            drugNamesToCheck.add(drugs.get(i));
        }

        List<String> diagnosisNamesToCheck = new ArrayList<>();

        for(int i = 0; i < numDiagnosis; i++){
            diagnosisNamesToCheck.add(diagnosis.get(i));
        }

        List<ParameterHolder> theParams = new ArrayList<>();

        for(String drug : drugNamesToCheck) {
            theParams.add(Functions.setupParameters(drug, diagnosisNamesToCheck));
        }

        List<DiagnosisScore> scoredEvents = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Future<List<DiagnosisScore>>> upcomingScores = Collections.synchronizedList(new ArrayList<>());
        for(ParameterHolder params : theParams) {
            upcomingScores.add(executorService.submit(new ScoredEventGetter(params, db)));
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.HOURS);
            for (Future<List<DiagnosisScore>> future : upcomingScores) {
                scoredEvents.addAll(future.get());
            }
        } catch (Exception e) {
            System.err.println("Error on concurrent shutdown: " + e);
        }

        Functions.displayTopTen(scoredEvents);

    }
}
