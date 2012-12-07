package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class ItemObject implements Node {
    private ObjectId objectId;
    
    private String name;
    private long numOfFields;
    private Node parent;
    private List<ItemField> children;
    
    private DBObject dbObject;
    
    public ItemObject(DBObject dbObject, Node parent) {
        this.dbObject = dbObject;
        this.parent = parent;
        
        this.numOfFields = dbObject.keySet().size();
        this.name = parent.getName();
        this.objectId = (ObjectId) dbObject.get("_id");
        
        this.children = new ArrayList<ItemField>();
        Iterator<String> it = dbObject.keySet().iterator();
        while (it.hasNext()) {
            String fieldName = it.next();
            Object fieldValue = dbObject.get(fieldName);
            children.add(new ItemField(fieldName, fieldValue, this));
        }
    }
    
    static public ObjectId toObjectId(String stringId) {
        return new ObjectId(Base64.decodeBase64(stringId.getBytes()));
    }
    
    static public String objectIdtoString(ObjectId objectId) {
        return new String(Base64.encodeBase64(objectId.toByteArray()));
    }
    
    public String getObjectIdBase64() {
        return objectId.toStringBabble();
    }

    @Override
    public Type getNodeType() {
        return Node.Type.ITEM_OBJECT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getCount() {
        return numOfFields;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public List<ItemField> getChildren() {
        return children;
    }

    @Override
    public List<ItemField> getChildren(int pageNum, int pageSize) {
        return children;
    }

    @Override
    public String toString() {
        return "ItemObject [name=" + name + ", numOfFields=" + numOfFields
                + ", parent=" + parent + "]";
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

}
