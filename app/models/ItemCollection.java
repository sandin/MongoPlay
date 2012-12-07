package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class ItemCollection implements Node {
    private String collectionName;
    private Node parent;
    private DBCollection dbCollection;
    
    private long numOfRows;
    private List<Node> children;
    
    public ItemCollection(DBCollection dbCollection, String collectionName, Node parent) {
        this.collectionName = collectionName;
        this.parent = parent;
        this.dbCollection = dbCollection;
        
        DBCursor cursor = dbCollection.find();
        this.numOfRows = cursor.size();
        cursor.close();
    }

    @Override
    public Type getNodeType() {
        return Node.Type.ITEM_COLLECTION;
    }

    @Override
    public String getName() {
        return collectionName;
    }

    @Override
    public long getCount() {
        return numOfRows;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    /**
     * @deprecated DO NOT USE THIS METHOD, use getChildren(int pageNum, int pageSize) instead of this
     * @return
     */
    @Override
    public List<ItemObject> getChildren() {
        return getChildren(1, Integer.MAX_VALUE);
    }
    
    @Override
    public List<ItemObject> getChildren(int pageNum, int pageSize) {
        DBCursor cursor= dbCollection.find().skip(pageSize * (pageNum-1)).limit(pageSize);
        if (cursor.size() == 0) {
            return null;
        }
        
        List<ItemObject> children = new ArrayList<ItemObject>();
        Iterator<DBObject> iterator = cursor.iterator();
        while (iterator.hasNext()) {
            DBObject dbObject = iterator.next();
            children.add(new ItemObject(dbObject, this));
        }
        cursor.close();
        return children;
    }
    
    public List<ItemObject> findChildren(int pageNum, int pageSize, DBObject query) {
     
        System.out.println("query:" + query);
        DBCursor cursor = dbCollection.find(query).skip(pageSize * (pageNum-1)).limit(pageSize);
        if (cursor == null || cursor.size() == 0) {
            return null;
        }
        
        List<ItemObject> children = new ArrayList<ItemObject>();
        Iterator<DBObject> iterator = cursor.iterator();
        while (iterator.hasNext()) {
            DBObject dbObject = iterator.next();
            children.add(new ItemObject(dbObject, this));
        }
        cursor.close();
        return children;
    }

    @Override
    public String toString() {
        return "ItemCollection [collectionName=" + collectionName + ", parent="
                + parent + ", numOfRows=" + numOfRows + "]";
    }
    

}
