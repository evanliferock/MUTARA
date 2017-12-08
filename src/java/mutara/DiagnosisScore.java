package mutara;

public class DiagnosisScore {
    private String A;
    private String C;
    private double unexpectedLeverage;

    public DiagnosisScore(String a, String c, double unexpectedLeverage) {
        A = a;
        C = c;
        this.unexpectedLeverage = unexpectedLeverage;
    }

    public String getA() {
        return A;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public double getUnexpectedLeverage() {
        return unexpectedLeverage;
    }

    public void setUnexpectedLeverage(double unexpectedLeverage) {
        this.unexpectedLeverage = unexpectedLeverage;
    }
}