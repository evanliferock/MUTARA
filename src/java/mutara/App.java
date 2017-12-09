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

        Collections.shuffle(drugs);

        List<String> drugNamesToCheck = new ArrayList<>();

        for(int i = 0; i < numDrugs; i++){
            drugNamesToCheck.add(drugs.get(i));
        }

        List<DiagnosisScore> scoredEvents = new ArrayList<>();

        int numThreads = Math.min(100, numDrugs);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<DBDataGetter> dbDataGetterList = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            dbDataGetterList.add(db.provideNewDBGetter());
        }

        List<Future<List<DiagnosisScore>>> upcomingScores = new ArrayList<>();

        for(int i = 0; i < drugNamesToCheck.size(); i++) {
            upcomingScores.add(executorService.submit(
                    new ScoredEventGetter(drugNamesToCheck.get(i),
                            dbDataGetterList.get(i % numThreads))));
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

        for (DBDataGetter aDB : dbDataGetterList) {
            aDB.shutdown();
        }


        Functions.displayTopTen(scoredEvents);

    }
}
