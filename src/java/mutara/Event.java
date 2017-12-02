package mutara;

import java.time.LocalDate;

public abstract class Event {
    private LocalDate date;

    public Event(LocalDate date) {
        this.date = date;
    }
    // Whatever else is communal

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
