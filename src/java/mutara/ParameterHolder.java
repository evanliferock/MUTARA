package mutara;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ParameterHolder {
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
    private Drug A; // antecedent A
    private List<Diagnosis> Cs; // possible candidates for the consequent
    private LocalDate tS; // A study period is specified by [tS, tE] according to the antecedent A
    private LocalDate tE; // A study period is specified by [tS, tE] according to the antecedent A
    private Period Te; // the effect period
    private Period Tr; // the reference period
    private Period Tb; // the period between the first A and the starting point of the reference period
    private Period Tc; // the control period

    public Drug getA() {
        return A;
    }

    public void setA(Drug a) {
        A = a;
    }

    public List<Diagnosis> getCs() {
        return Cs;
    }

    public void setCs(List<Diagnosis> cs) {
        Cs = cs;
    }

    public LocalDate gettS() {
        return tS;
    }

    public void settS(LocalDate tS) {
        this.tS = tS;
    }

    public LocalDate gettE() {
        return tE;
    }

    public void settE(LocalDate tE) {
        this.tE = tE;
    }

    public Period getTe() {
        return Te;
    }

    public void setTe(Period te) {
        Te = te;
    }

    public Period getTr() {
        return Tr;
    }

    public void setTr(Period tr) {
        Tr = tr;
    }

    public Period getTb() {
        return Tb;
    }

    public void setTb(Period tb) {
        Tb = tb;
    }

    public Period getTc() {
        return Tc;
    }

    public void setTc(Period tc) {
        Tc = tc;
    }
}
