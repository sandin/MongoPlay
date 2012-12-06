package models;

import java.util.List;

public class ItemField implements Node {
    private String name;
    private Object value;
    private Node parent;
    
    public ItemField(String name, Object value, Node parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
    }

    @Override
    public Type getNodeType() {
        return Node.Type.ITEM_FIELD;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public Object getValue() {
        return value;
    }

    @Override
    public long getCount() {
        return 0;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public List<Node> getChildren() {
        return null; // bottom
    }

    @Override
    public List<Node> getChildren(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public String toString() {
        return "ItemField [name=" + name + ", value=" + value + ", parent="
                + parent + "]";
    }

}
