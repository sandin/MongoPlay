package controllers;

import java.util.List;

import models.DbConfig;
import models.DirCollections;
import models.DirDatabases;
import models.ItemCollection;
import models.ItemObject;
import models.Node;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import com.lds.mongo.db.MongdbHelper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class Application extends Controller {
    final static Form<DbConfig> dbConfigForm = form(DbConfig.class);
    private static DbConfig dbConfig;
    /*
    static {
        dbConfig.port = 27017;
        dbConfig.domain = "localhost";
    }
    */
    
    public static Result index() {
        return ok(index.render(dbConfigForm));
    }
    
    public static Result databases() {
        Form<DbConfig> dbConfigForm = form(DbConfig.class).bindFromRequest();
        if (dbConfigForm != null && !dbConfigForm.hasErrors()) {
            dbConfig = dbConfigForm.get();
        } else {
            dbConfig = new DbConfig();
            dbConfig.domain = session("host");
            dbConfig.port = Integer.parseInt(session("port"));
        }
        System.out.println("dbConfig: " + dbConfig.domain + ":" + dbConfig.port);
        
        MongdbHelper dbHelper = new MongdbHelper(dbConfig.domain, dbConfig.port);
        Mongo mongo = dbHelper.connect();
        session("host", dbConfig.domain);
        session("port", dbConfig.port+"");
        
        DirDatabases root = new DirDatabases(mongo);
        List<Node> dbs = root.getChildren(); 
        for (Node database : dbs) {
            System.out.println("item: " + database);
        }
        
        
        return ok(databases.render(dbConfig, dbs));
    }
    
    public static Result collections(String dbname) {
        String host = session("host");
        int port = Integer.parseInt(session("port"));
        
        MongdbHelper dbHelper = new MongdbHelper(host, port);
        Mongo mongo = dbHelper.connect();
        
        DirCollections dir = new DirCollections(mongo, dbname, new DirDatabases(mongo));
        List<Node> list = dir.getChildren();
        
        return ok(collections.render(dbname, list));
    }
    
    public static Result objects(String dbname, String collectionName) {
        String host = session("host");
        int port = Integer.parseInt(session("port"));
        
        MongdbHelper dbHelper = new MongdbHelper(host, port);
        Mongo mongo = dbHelper.connect();
        
        DB db = mongo.getDB(dbname);
        DBCollection dbCollection = db.getCollection(collectionName);
        DirCollections parent = new DirCollections(mongo, dbname, new DirDatabases(mongo));
        
        ItemCollection itemCollection = new ItemCollection(dbCollection, collectionName, parent);
        
        DynamicForm form = new DynamicForm();
        DynamicForm params = form.bindFromRequest();
        String query = params.get("q");
        
        String message = null;
        DBObject ref = null;
        if (query != null && !"".equals(query)) {
            try {
                ref = (DBObject) JSON.parse(query);
            } catch (Exception e) { // JSONParseException
                e.printStackTrace();
                message = "JSON格式错误: " + query;
            }
        }
        
        List<ItemObject> list = null;
        if (ref != null) {
            list = itemCollection.findChildren(1, 50, ref);
        } else {
            list = itemCollection.getChildren(1, 50);
        }
        
        System.out.println("list: " + list);
        return ok(objects.render(message, query, dbname, collectionName, (List<ItemObject>)list));
    }

}