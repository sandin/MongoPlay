package models;

import java.util.List;

public interface Node {
    
    Type getNodeType();
    
    String getName();
    
    long getCount();
    
    Node getParent();
    
    List<? extends Node> getChildren();
    
    List<? extends Node> getChildren(int pageNum, int pageSize);
    
    public enum Type {
        /** 数据库 */
        DIR_DATABASES,
        /** 表外 */
        DIR_COLLECTIONS,
        /** 表里  */
        ITEM_COLLECTION,
        /** 列 */
        ITEM_OBJECT,
        /** 字段 */
        ITEM_FIELD,
    }

}
