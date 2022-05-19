package net.codejava;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBClass {
		private MongoClient mongoClientIndexer;
		private MongoDatabase dbIndexer;
		private MongoCollection indexerCollection; 
		private MongoCollection indexedLinksCollection; 
   
	
	    public MongoDBClass() {
	    	//2 clusters to avoid limited free space quota
	    	this.mongoClientIndexer= MongoClients.create("mongodb://Sandra:fmCs6CAZx0phSrjs@cluster0-shard-00-00.real4.mongodb.net:27017,cluster0-shard-00-01.real4.mongodb.net:27017,cluster0-shard-00-02.real4.mongodb.net:27017/SearchEngine?ssl=true&replicaSet=atlas-bhl30t-shard-0&authSource=admin&retryWrites=true&w=majority");
	        this.dbIndexer = mongoClientIndexer.getDatabase("SearchEngine");	       
	        this.indexerCollection=dbIndexer.getCollection("indexers"); //contains inverted index
	        this.indexedLinksCollection=dbIndexer.getCollection("indexedlinks"); //contains previously indexed  links to avoid indexing them again
	    }
	    public MongoCollection getIndexerCollection() {
	    	return indexerCollection;
	    }
	    public MongoCollection getIndexedLinksCollection() {
	    	return indexedLinksCollection;
	    }
	    
	
}