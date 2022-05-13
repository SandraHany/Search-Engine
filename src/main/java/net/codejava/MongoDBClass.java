package net.codejava;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBClass {
	
		private MongoClient mongoClient;
	    private MongoDatabase db; 
	    public MongoDBClass() {
			
		this. mongoClient = MongoClients.create("mongodb+srv://Sandra:fmCs6CAZx0phSrjs@cluster0.real4.mongodb.net/SearchEngine?retryWrites=true&w=majority");
	    this. db = mongoClient.getDatabase("SearchEngine");		
			
	    }
	    public MongoCollection<Document> DBgetCollection(String CollectionName) {
	    	return db.getCollection(CollectionName);
	    }
	    boolean wordExists(String word) {
	    	MongoCursor<org.bson.Document> cursor=null;  
	    	BasicDBObject  criteria = new BasicDBObject();
			criteria.append("word",word);
			cursor = DBgetCollection("Indexer").find(criteria).cursor();  
			 if(!cursor.hasNext()) {
				 return false;
			 }
			 return true;
	    }
	
}
