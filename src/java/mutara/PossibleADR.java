package mutara;

public class PossibleADR {
    private String drugName;
    private String diagnosisName;

    public PossibleADR(String drugName, String diagnosisName) {
        this.drugName = drugName;
        this.diagnosisName = diagnosisName;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }
}
