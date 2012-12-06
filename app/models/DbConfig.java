package models;

import play.data.validation.Constraints.Required;

public class DbConfig {
    @Required
    public String domain;
    
    @Required
    public int port;
}
