package mutara;

import java.time.LocalDate;

public class Diagnosis extends Event {
    private String symptomName;

    public Diagnosis(LocalDate date, String symptomName) {
        super(date);
        this.symptomName = symptomName;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }
}
