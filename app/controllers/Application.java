package controllers;

import java.util.Iterator;
import java.util.List;

import models.DbConfig;
import models.DirCollections;
import models.DirDatabases;
import models.ItemCollection;
import models.ItemObject;
import models.Node;

import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import com.lds.mongo.db.MongdbHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;

public class Application extends Controller {
    final static Form<DbConfig> dbConfigForm = form(DbConfig.class);
    private static DbConfig dbConfig;
    
    public static Result index() {
        String message = flash("message");
        return ok(index.render(message, dbConfigForm));
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
        
        if (mongo == null) {
            flash("message", "无法连接到数据库");
            return redirect("/");
        }
        
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

    public static Result dropCollection(String dbname, String collectionName) {
        if (collectionName == null || dbname == null) {
            return badRequest("Missing parameters, [collection] and [db]");
        }
        Mongo mongo = getMongo();
        if (mongo == null) {
            return badRequest("Cann't connect to the database");
        }
        ObjectNode result = Json.newObject();
        result.put("status", true);
        result.put("message", "");

        try {
            DB db = mongo.getDB(dbname);
            DBCollection collection = db.getCollection(collectionName);
            collection.drop(); // drop this collection
            System.out.println("drop collection: " + collectionName);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", false);
            result.put("message", e.getMessage());
        }

        return ok(result);
    }
    
    private static Mongo getMongo() {
        String host = session("host");
        int port = Integer.parseInt(session("port"));
        
        MongdbHelper dbHelper = new MongdbHelper(host, port);
        Mongo mongo = dbHelper.connect();
        mongo.getDatabaseNames();
        return mongo;
    }
    
    public static Result objectsDelete(String dbname, String collectionName, String objectId) {
        ObjectNode result = Json.newObject();
        result.put("status", true);
        result.put("message", "");
        
        Mongo mongo = getMongo();
        DB db = mongo.getDB(dbname);
        DBCollection collection = db.getCollection(dbname);
        
        System.out.println("objectId: " + objectId);
        DBObject query = new BasicDBObject();
        ObjectId oid = new ObjectId(objectId);
        System.out.println("oid: " + oid.getTime());
        System.out.println("oid: " + oid.getMachine());
        System.out.println("oid: " + oid.getInc());
        query.put("_id", oid);
        DBObject obj = collection.findOne(query);
        if (obj == null) {
            result.put("message", objectId + " 不存在!");
            result.put("status", false);
        } else {
            Iterator<String> it = obj.keySet().iterator();
            while (it.hasNext()) {
                String name = it.next();
                Object value = obj.get(name);
                System.out.println(name + " : " + value);
            }
        }
        return ok(result);
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