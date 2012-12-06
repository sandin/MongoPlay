package models;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.Mongo;

/**
 * Set of databases
 * 
 * @author lds
 */
public class DirDatabases implements Node {
    private String name;
    private long numOfdatabases;
    private Mongo mongo;
    
    private List<String> databaseNames;
    private ArrayList<Node> children;
    
    public DirDatabases(Mongo mongo) {
        this.mongo = mongo;
        this.name = mongo.getAddress().getHost() + ":" + mongo.getAddress().getPort();
        this.databaseNames = mongo.getDatabaseNames();
        this.numOfdatabases = databaseNames.size();
        
        this.children = new ArrayList<Node>();
        for (String dbname : databaseNames) {
            children.add(new DirCollections(mongo, dbname, this));
        }
        
    }

    @Override
    public Type getNodeType() {
        return Node.Type.DIR_DATABASES;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCount() {
        return numOfdatabases;
    }

    @Override
    public Node getParent() {
        return null; // It's root
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public List<Node> getChildren(int pageNum, int pageSize) {
        return children;
    }
}
