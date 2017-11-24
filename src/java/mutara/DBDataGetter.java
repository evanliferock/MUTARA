package mutara;

import java.util.ArrayList;

public class DBDataGetter {
    private String username;
    private String password;
    int numPeople;

    public DBDataGetter(String username, String password, int numUsers) {
        this.username = username;
        this.password = password;
        this.numPeople = numUsers;
    }

    /**
     * Gets data for a certain number of people from the MySQL server. It will parse the data into Events that are
     * either Drug or Symptoms
     * @return
     */
    public ArrayList<Event> getUserSequences(){
        return new ArrayList<>();
    }
}
