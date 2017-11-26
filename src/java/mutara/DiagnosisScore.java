package mutara;

public class DiagnosisScore {
    private Diagnosis C;
    private double unexpectedLeverage;

    public DiagnosisScore(Diagnosis c, double unexpectedLeverage) {
        C = c;
        this.unexpectedLeverage = unexpectedLeverage;
    }

    public Diagnosis getC() {
        return C;
    }

    public void setC(Diagnosis c) {
        C = c;
    }

    public double getUnexpectedLeverage() {
        return unexpectedLeverage;
    }

    public void setUnexpectedLeverage(double unexpectedLeverage) {
        this.unexpectedLeverage = unexpectedLeverage;
    }
}