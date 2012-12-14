package models;

import java.util.HashMap;
import java.util.Map;

import play.data.validation.Constraints.Required;

public class DbConfig {
    public static final Map<String, String> users =
            new HashMap<String, String>();
    static {
        users.put("admin", "f#sa*^57fsd");
    }
    
    @Required
    public String domain;
    
    @Required
    public int port;
    
    @Required
    public String user;
    
    @Required
    public String password;
    
}
