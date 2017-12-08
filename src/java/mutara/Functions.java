package mutara;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Functions {
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

    /*
        2. Prepare user subsequences from user sequences which have A during the study
        period: choose event types from the hazard period, and exclude some of them
        based on the user-based exclusion with respect to the antecedent A;
    */
    public static List<List<Event>> userBasedExclusion(String drug, List<List<Event>> userSequences){
        List<List<Event>> subsequences = new ArrayList<>();
        List<Diagnosis> priorDiagnosis;
        List<Event> currentList;
        Event e;
        for (List<Event> list : userSequences) {
            currentList = new ArrayList<>(list);
            priorDiagnosis = new ArrayList<>();

            // before drug of interest
            for(int i = 0; i < currentList.size(); i++) {
                e = currentList.get(i);
                if (e instanceof Diagnosis) {
                    priorDiagnosis.add((Diagnosis) e);
                } else if (((Drug) e).getDrugName().equals(drug)){
                    break;
                } else
                    currentList.remove(i--);
            }

            // after drug of interest
            for(int i = 0; i < currentList.size(); i++){
                e = currentList.get(i);
                if(e instanceof Diagnosis && isContained((Diagnosis)e, priorDiagnosis))
                    currentList.remove(i--);
                else if (e instanceof Drug && !((Drug) e).getDrugName().equals(drug)){
                    currentList.remove(i--);
                }

            }
            subsequences.add(currentList);
        }
        return subsequences;
    }

    public static boolean isContained(Diagnosis d, List<Diagnosis> dList){
        for(Diagnosis diagnosis : dList)
            if(diagnosis.getSymptomName().equals(d.getSymptomName()))
                return true;
        return false;
    }


    /*
        3. Choose nonuser subsequences from the control period from nonuser sequences;
    */
    public static List<List<Event>> nonUserSubsectioning(List<List<Event>> nonUserSequences){
        List<List<Event>> subsequences = new ArrayList<>();
        List<Event> currentList;
        Event e;
        for(List<Event> list : nonUserSequences){
            currentList = new ArrayList<>(list);
            subsequences.add(currentList);
        }
        return subsequences;
    }


    /*
        4. Calculate supports and unexpected-leverage of each event type of interest;
    */
    public static List<DiagnosisScore> scoreEvents(String drug, List<String> reactions, List<List<Event>> userSubsequences,
                                                   List<List<Event>> nonUserSubsequences){
        List<DiagnosisScore> scoreList= new ArrayList<>();
        for(String reaction : reactions){
            SupportCalculator s = new SupportCalculator(userSubsequences, nonUserSubsequences, drug, reaction);
            scoreList.add(s.calculateUnexLeverage());
        }

        return scoreList;
    }


    /*
        5. Rank the event types in the descending order of unexpected-leverage, and return
                the top 10 UTARs with high unexpected-leverage.
    */
    public static void displayTopTen(List<DiagnosisScore> scores){
        List<DiagnosisScore> newList = new ArrayList<>(scores);
        List<DiagnosisScore> topTen = new ArrayList<>();
        int removeIndex;
        DiagnosisScore score, topScore;
        for(int i = 0; i < (newList.size() > 10 ? 10 : newList.size()); i++) {
            topScore = newList.get(0);
            removeIndex = 0;
            for (int j = 0; j < newList.size(); j++) {
                score = newList.get(j);
                if(score.getUnexpectedLeverage() > topScore.getUnexpectedLeverage()){
                    topScore = score;
                    removeIndex = j;
                }
            }
            topTen.add(topScore);
            newList.remove(removeIndex);
        }

        for (int i = 1; i <= topTen.size(); i++) {
            score = topTen.get(i - 1);
            System.out.println(i + ": " + score.getA() + " => " + score.getC() + " Unexpected Leverage: " + score.getUnexpectedLeverage());
        }
    }
}
