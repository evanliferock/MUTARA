package mutara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBDataGetter {
    public enum user{
        USER, NONUSER
    }
    private String username;
    private String password;

    public DBDataGetter(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets data for a certain number of people from the MySQL server. It will parse the data into Events that are
     * either Drug or Symptoms. It will return a uneven tables which each row being a list of events. The Data will
     * Be separated into User and Nonuser tables
     * @return
     */
    public Map<user ,List<List<Event>>> getUserSequences(){
        return new HashMap<>();
    }
}
