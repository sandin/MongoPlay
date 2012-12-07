package com.lds.mongo.db;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.Mongo;


/**
 * Mongdb Open Helper
 * 
 * @author lds
 */
public class MongdbHelper {
	private Logger log = Logger.getLogger(MongdbHelper.class);
	
	private Mongo mongo;
	private Morphia morphia;
	
	private Datastore datastore;
	
	private String host;
	private int port;
	
	public MongdbHelper(String host, int port) {
	    this.host = host;
	    this.port = port;
	    
		connect();
	}
	
	/**
	 * 初始化Mongdb连接
	 * 
	 * <b>外部调用推荐使用 {@link MongdbHelper#getInstance()} 即可。</b>
	 */
	public Mongo connect() {
		try {
			mongo = new Mongo(host, port);
			morphia = new Morphia();
//			datastore = morphia.createDatastore(mongo, Config.MONGDB_NAME);
			log.info("connect to mongodb " + host + ":" + port);
			mongo.getDatabaseNames(); // throw Exception?
			return mongo;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("servlet初始化失败"+e.getMessage());
		}
		return null;
	}
	
	public List<String> getDatabases() {
	    return mongo.getDatabaseNames();
	}
	
	public Set<String> getCollections(String dbname) {
	    DB db = mongo.getDB(dbname);
	    return db.getCollectionNames();
	}
	
	/**
	 * 返回数据库连接
	 * @return 如果 {@link MongdbHelper#useDebugDatabase} 为true, 则仅返回测试数据库连接
	 */
	public Datastore getDs() {
		return datastore;
	}
	
	/* GET & SET */

	public Mongo getMongo() {
		return mongo;
	}

	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	public Morphia getMorphia() {
		return morphia;
	}

	public void setMorphia(Morphia morphia) {
		this.morphia = morphia;
	}

	public Datastore getDatastore() {
		//return datastore;
		return getDs();
	}

	public void setDatastore(Datastore datastore) {
		this.datastore = datastore;
	}

}
