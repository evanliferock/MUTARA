package mutara;

import java.time.LocalDate;
import java.util.List;

public class SupportCalculator {

    private List<List<Event>> userSubsequences;
    private List<List<Event>> nonUserSubsequences;
    private String drug;
    private String reaction;
    private double supportUnExAC;
    private double supportAT;
    private double supportUnExC;
    private double unExLev;

    public SupportCalculator(List<List<Event>> userSubsequences, List<List<Event>> nonUserSubsequences, String drug, String reaction) {
        this.userSubsequences = userSubsequences;
        this.nonUserSubsequences = nonUserSubsequences;
        this.drug = drug;
        this.reaction = reaction;
    }

    private void updateBasedOnNonUserSequence(List<Event> row){
        for(Event e  : row){
            if(e instanceof Diagnosis && ((Diagnosis) e).getSymptomName().equals(reaction)){
                supportUnExC++;
                break;
            }
        }
    }


    private void updateBasedOnUserSequence(List<Event> row){
        for(Event e  : row){
            if(e instanceof Diagnosis && ((Diagnosis) e).getSymptomName().equals(reaction)){
                supportUnExC++;
                supportUnExAC++;
                break;
            }
        }
    }


    private void calculateSupports(){
        double total = userSubsequences.size() + nonUserSubsequences.size();
        supportAT = userSubsequences.size() / total;
        for(List<Event> list : userSubsequences){
            updateBasedOnUserSequence(list);
        }

        for (List<Event> list : nonUserSubsequences){
            updateBasedOnNonUserSequence(list);
        }

        supportUnExAC /= total;
        supportUnExC /= total;
    }


    public DiagnosisScore calculateUnexLeverage() {
        calculateSupports();
        unExLev = supportUnExAC - (supportAT * supportUnExC);
        return new DiagnosisScore(drug, reaction, unExLev);
    }
}