package mutara;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ScoredEventGetter implements Callable<List<DiagnosisScore>> {
    private String drug;
    private DBDataGetter db;

    public ScoredEventGetter(String drug, DBDataGetter db) {
        this.drug = drug;
        this.db = db;
    }

    @Override
    public List<DiagnosisScore> call() {

        Map<DBDataGetter.user, List<List<Event>>> data = db.getUserSequences(drug);

        List<String> reactions = db.getReactionsForDrug(drug);

        List<List<Event>> userSequences = data.get(DBDataGetter.user.USER);
        List<List<Event>> nonUserSequences = data.get(DBDataGetter.user.NONUSER);

        /*
        2. Prepare user subsequences from user sequences which have A during the study
        period: choose event types from the hazard period, and exclude some of them
        based on the user-based exclusion with respect to the antecedent A;
        */
        List<List<Event>> userSubsequences = Functions.userBasedExclusion(drug, userSequences);
        /*
        3. Choose nonuser subsequences from the control period from nonuser sequences;
        */
        List<List<Event>> nonUserSubsequences = Functions.nonUserSubsectioning(nonUserSequences);
        /*
        4. Calculate supports and unexpected-leverage of each event type of interest;
        */
        return Functions.scoreEvents(drug, reactions, userSubsequences, nonUserSubsequences);
    }
}
