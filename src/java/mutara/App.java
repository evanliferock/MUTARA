package mutara;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args){
        DBDataGetter db = new DBDataGetter("sshusername: look at lab 2 for what these credentials should be",
                "sshpassword",
                "dbusernam",
                "dbpassword");
        List<String> list = db.getDrugNames();
        Map<DBDataGetter.user, List<List<Event>>> data = db.getUserSequences("IRON");
        List<List<Event>> userSequences = data.get(DBDataGetter.user.USER);
        List<List<Event>> nonUserSequences = data.get(DBDataGetter.user.NONUSER);
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
        ParameterHolder params = Functions.setupParameters();
        /*
        2. Prepare user subsequences from user sequences which have A during the study
        period: choose event types from the hazard period, and exclude some of them
        based on the user-based exclusion with respect to the antecedent A;
        */
        List<List<Event>> userSubsequences = Functions.userBasedExclusion(params, userSequences);
        /*
        3. Choose nonuser subsequences from the control period from nonuser sequences;
        */
        List<List<Event>> nonUserSubsequences = Functions.nonUserSubsectioning(params, nonUserSequences);
        /*
        4. Calculate supports and unexpected-leverage of each event type of interest;
        */
        List<DiagnosisScore> scoredEvents = Functions.scoreEvents(params, userSubsequences, nonUserSubsequences);
        /*
        5. Rank the event types in the descending order of unexpected-leverage, and return
                the top 10 UTARs with high unexpected-leverage.
        */
        Functions.displayTopTen(scoredEvents);
    }
}
