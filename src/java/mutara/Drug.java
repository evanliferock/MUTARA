package mutara;

import java.time.LocalDate;

public class Drug extends Event {
    private String drugName;

    public Drug(LocalDate date, String drugName) {
        super(date);
        this.drugName = drugName;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugID) {
        this.drugName = drugName;
    }
}
