package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Set of Collections
 * 
 * @author lds
 */
public class DirCollections implements Node {
    private String dbname;
    private Mongo mongo;
    private long numOfCollcetions;
    private DB db;
    private Set<String> collectionNames;
    private Node parent;
    private List<Node> children;
    
    public DirCollections() {
    }
    
    public DirCollections(Mongo mongo, String dbname, Node parent) {
        this.dbname = dbname;
        this.mongo = mongo;
        this.parent = parent;
        
        this.db = mongo.getDB(dbname);
        this.collectionNames = db.getCollectionNames();
        this.numOfCollcetions = collectionNames.size();
        
        this.children = new ArrayList<Node>();
        Iterator<String> it = collectionNames.iterator();
        while (it.hasNext()) {
            String collectionName = it.next();
            DBCollection dbCollection = db.getCollection(collectionName);
            children.add(new ItemCollection(dbCollection, collectionName, this));
        }
    }

    @Override
    public Type getNodeType() {
        return Node.Type.DIR_COLLECTIONS;
    }

    @Override
    public String getName() {
        return dbname;
    }

    @Override
    public long getCount() {
        return numOfCollcetions;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }
    
    @Override
    public List<Node> getChildren(int pageNum, int pageSize) {
        return children;
    }

    @Override
    public String toString() {
        return "DirCollections [dbname=" + dbname + ", numOfCollcetions="
                + numOfCollcetions + ", collectionNames=" + collectionNames
                + ", parent=" + parent + "]";
    }

  
    
    

}
