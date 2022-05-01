package net.codejava;
import  opennlp.tools.stemmer.PorterStemmer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.bson.BasicBSONObject;
import org.bson.conversions.Bson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
public class Indexer {
	public static void main(String[] args) {
		PorterStemmer stemmer = new PorterStemmer();
		/*for(String dbName : dbNames) {
			System.out.println(dbName);
		}*/
		FileInputStream fis;
		List<String>stopWordsList = new ArrayList<String>();
		List<String>headings = new ArrayList<String>();
		List<String>titles = new ArrayList<String>();
		List<String>normal = new ArrayList<String>();
		//Iterator<String> it_stop_words = stopWordsList.iterator();
		try {
			fis = new FileInputStream("StopWords.txt");
			Scanner sc = new Scanner(fis);
			while(sc.hasNextLine()) {
				stopWordsList.add(sc.nextLine());
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		Set<String> Seed_URLs= new HashSet<String>();	
		List<String> tokens_headings=new ArrayList<String>();
		List<String> tokens_titles=new ArrayList<String>();
		List<String> tokens_normal=new ArrayList<String>();
		List<String> URLs_DB=new ArrayList<String>();
		try {
			//linux-> // 
			//windows-> \\
			 fis = new FileInputStream("SeedSet.txt");
			Scanner sc = new Scanner(fis);
			while(sc.hasNextLine()) {
				Seed_URLs.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<String> it_Seed_URLs = Seed_URLs.iterator(); 
		//Iterator<String> it_tokens = tokens.iterator(); 
		Iterator<String> it_tokens_titles ;
		Iterator<String> it_tokens_headings ;
		Iterator<String> it_tokens_normal ;
		Iterator<String> it_tokens_all= null ;
		org.jsoup.nodes.Document doc = null;
		String it_SeedURLs_value = null;
		String it_tokens_value = null;
		String text = null;
		String arr[] = null;

		// database for inverted file

		//String uri = "mongodb+srv://Sandra:passw0rd@cluster0.real4.mongodb.net/Indexer?retryWrites=true&w=majority";
	    //String uri = "mongodb://Sandra:passw0rd@localhost";
		//MongoClient mongoClient = MongoClients.create(uri);
//ConnectionString connectionString = new ConnectionString("mongodb+srv://Sandra:orm3SQJsTmVEdIdc@cluster0.real4.mongodb.net/Indexer?retryWrites=true&w=majority");
//MongoClientSettings settings = MongoClientSettings.builder()
  //      .applyConnectionString(connectionString)
    //    .build();


		//MongoClient mongoClient = MongoClients.create();

//ConnectionString connectionString = new ConnectionString("mongodb+srv://Sandra:stVaUoUQ1M5YQTQJ@cluster0.real4.mongodb.net/Indexer?retryWrites=true&w=majority");
//MongoClientSettings settings = MongoClientSettings.builder()
  //      .applyConnectionString(connectionString)
    //    .build();
//MongoClient mongoClient = MongoClients.create(settings);
		//MongoClient		mongoClient = MongoClients.create("mongodb://Sandra:sandra@cluster0-shard-00-00.real4.mongodb.net:27017,cluster0-shard-00-01.real4.mongodb.net:27017,cluster0-shard-00-02.real4.mongodb.net:27017/Indexer?ssl=true&replicaSet=atlas-bhl30t-shard-0&authSource=admin&retryWrites=true&w=majority");
		//MongoClient mongoClient	=   MongoClients.create("mongodb://localhost:27017");
	//	MongoClient mongoClient	=   MongoClients.create("mongodb+srv://Sandra:sandra@cluster0.real4.mongodb.net/Indexer?retryWrites=true&w=majority");
          
		
		/* MongoCredential credential = MongoCredential.createCredential("Sandra", "Indexer", "sandra".toCharArray());
		ConnectionString connectionString = new ConnectionString("mongodb://Sandra:sandra@cluster0-shard-00-00.real4.mongodb.net:27017,cluster0-shard-00-01.real4.mongodb.net:27017,cluster0-shard-00-02.real4.mongodb.net:27017/Indexer?ssl=true&replicaSet=atlas-bhl30t-shard-0&authSource=admin&retryWrites=true&w=majority, connectTimeoutMS: 10000000");
		MongoClientSettings settings = MongoClientSettings.builder()
		        .applyConnectionString(connectionString)
		        .credential(credential)
		        .build();*/
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase db = mongoClient.getDatabase("Indexer");
 

		//spring.data.mongodb.uri="mongodb+srv://Sandra:passw0rd@cluster0.real4.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
		//MongoClient mongoClient= MongoClients.create("mongodb+srv://Sandra:passw0rd@cluster0.real4.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
		//MongoDatabase db =mongoClient.getDatabase("SearchEngine0");
		MongoCollection <org.bson.Document> InvertedFile=db.getCollection("InvertedFile");
		db.getCollection("InvertedFile").deleteMany(new org.bson.Document());
		//BasicDBObject document = new BasicDBObject();
		//db.getCollection("InvertedFile").drop();
		//db.createCollection("InvertedFile");
		
		org.bson.Document document0;
		org.bson.Document document_url_tf;
		org.bson.Document document_url_df;
		FindIterable<org.bson.Document> result;
		FindIterable<org.bson.Document> iterable;
		Bson updates;
		UpdateOptions options;
		ArrayList TF_Prev_Arr;
		org.bson.Document TF_Prev_Doc = null;
		Integer TF_Prev;
		int DF_Prev;
		it_Seed_URLs=Seed_URLs.iterator();
		UpdateResult update_result;
		List<org.bson.Document> TFs_URLs;
		BasicDBObject searchQuery=null;
		MongoCursor<org.bson.Document> cursor=null;
		org.bson.Document oldDoc;
		UpdateOptions upsert=null; 
		Bson idFilter=null;
		BasicDBObject criteria;
		String dummy;
		String tag_choice=null;
		while (it_Seed_URLs.hasNext()) {   
			it_SeedURLs_value=it_Seed_URLs.next();
			tokens_titles.clear();
			tokens_headings.clear();
			tokens_normal.clear();
			try {
					//doc = Jsoup.connect(it_SeedURLs_value).timeout(1000000).get();
				 Connection connection = Jsoup.connect(it_SeedURLs_value);
				    
				 connection.userAgent("Mozilla");
				    
				    //set timeout to 10 seconds
				    connection.timeout(10 * 1000);
				    
				    //get the HTML document
				     doc = connection.get();
				    
				    //parse text from HTML
				    
					 headings.addAll(Arrays.asList(doc.select("h1").text().toLowerCase().split(" ")));
			            headings.addAll(Arrays.asList(doc.select("h2").text().toLowerCase().split(" ")));
			            headings.addAll(Arrays.asList(doc.select("h3").text().toLowerCase().split(" ")));
			            headings.addAll(Arrays.asList(doc.select("h4").text().toLowerCase().split(" ")));
			            headings.addAll(Arrays.asList(doc.select("h5").text().toLowerCase().split(" ")));
			            headings.addAll(Arrays.asList(doc.select("h6").text().toLowerCase().split(" ")));
			            titles.addAll(Arrays.asList(doc.select("title").text().toLowerCase().split(" ")));
			            normal.addAll(Arrays.asList(doc.select("pre").text().toLowerCase().split(" ")));
			            normal.addAll(Arrays.asList(doc.select("p").text().toLowerCase().split(" ")));
			            normal.addAll(Arrays.asList(doc.select("li").text().toLowerCase().split(" ")));
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} 
            
           
            //System.out.println(text);
           // arr=text.split(" ");
            for(int i=0; i<headings.size(); i++) {
            	if(!stopWordsList.contains(headings.get(i).toLowerCase())) {
            	tokens_headings.add(headings.get(i).toLowerCase());
            	}
	        }
            for(int i=0; i<titles.size(); i++) {
            	if(!stopWordsList.contains(titles.get(i).toLowerCase())) {
            	tokens_titles.add(titles.get(i).toLowerCase());
            	}
	        }
            for(int i=0; i<normal.size(); i++) {
            	if(!stopWordsList.contains(normal.get(i))) {
            	tokens_normal.add(normal.get(i).toLowerCase());
            	}
	        }
            it_tokens_headings=tokens_headings.iterator();
            it_tokens_titles=tokens_titles.iterator();
            it_tokens_normal=tokens_normal.iterator();
    		while (it_tokens_headings.hasNext() || it_tokens_titles.hasNext() ||it_tokens_normal.hasNext()) {   
    			//it_tokens_value=it_tokens.next();
    			if(it_tokens_headings.hasNext()) {
    				it_tokens_all=it_tokens_headings;
    				tag_choice="Heading";
    			}
    			else if(it_tokens_titles.hasNext()){
    				it_tokens_all=it_tokens_titles;
    				tag_choice="Title";
    			}
    			else if(it_tokens_normal.hasNext()){
    				it_tokens_all=it_tokens_normal;
    				tag_choice="Normal";
    			}
    			it_tokens_value=stemmer.stem(it_tokens_all.next().toLowerCase());
    			iterable = db.getCollection("InvertedFile").find(new org.bson.Document ("word",it_tokens_value));
    			//if(iterable.first==null) {
    			if(iterable.first()==null) {
					//URLs_DB.add(it_SeedURLs_value);
					//document_url_tf= new org.bson.Document("URL",it_SeedURLs_value) .append("TF", 1);
					document0 = new org.bson.Document("word",it_tokens_value).append("DF",1);
					TFs_URLs=new ArrayList<org.bson.Document>();
					List<String> tags= new ArrayList<String>();
					tags.add(tag_choice);
					org.bson.Document d= new org.bson.Document("URL",it_SeedURLs_value) .append("TF", 1);
					d.append("Tags", tags);
					//TFs_URLs.add(new org.bson.Document("URL",it_SeedURLs_value) .append("TF", 1));
					TFs_URLs.add(d);
					InvertedFile.insertOne(document0.append("TF/URL", TFs_URLs));
					//db.getCollection("InvertedFile0").insertOne([{word:it_tokens_value,DF:1}]);
				}
				else {
					
					criteria = new BasicDBObject();
					criteria.append("word",it_tokens_value);
					criteria.append("TF/URL.URL",it_SeedURLs_value);
					
				  
				    
				    FindIterable<org.bson.Document> ft;
				    cursor = InvertedFile.find(criteria).cursor();  
				    if(cursor.hasNext()) {
					    try{
					        while(cursor.hasNext()){
					        	
					        	oldDoc = cursor.next();
				   // try{
				    	//cursor = db.getCollection("InvertedFile").find(criteria).cursor();  
						//oldDoc =  db.getCollection("InvertedFile").find(criteria).first();
				       // while(cursor.hasNext()){
				    	//cursor = (db.getCollection("InvertedFile").find(criteria)).iterator();  
				        	//oldDoc = ( db.getCollection("InvertedFile").find(criteria)).cursor().next();
				    	    
				            upsert = new UpdateOptions().upsert(true);
				            idFilter = Filters.eq("_id", oldDoc.getObjectId("_id"));
				            TF_Prev_Arr=new ArrayList<org.bson.Document>((ArrayList) (oldDoc.get("TF/URL")));
				            //Set<String> tags= new HashSet<String>();	
				            
				            for(int y=0;y<TF_Prev_Arr.size();y++) {
				            	dummy=(String) ((org.bson.Document)(TF_Prev_Arr.get(y))).get("URL");
		
				            	if(dummy.equals(it_SeedURLs_value)) {
				            		TF_Prev=(Integer) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("TF"));	
				            		List<String> tags= new ArrayList<String>();
				            		tags=(List<String>) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("Tags"));
				            		if(tags==null) {
				            			tags=new ArrayList<String>();
				            			tags.add(tag_choice);
				            		}
				            		else if(!tags.contains(tag_choice) ) {
				            		tags.add(tag_choice);
				            		}
				            		TF_Prev_Arr.remove(y);	
				            		org.bson.Document  d= new org.bson.Document("URL",it_SeedURLs_value);
				            		d.append("TF", TF_Prev+1);
				            		d.append("Tags",tags);
						            TF_Prev_Arr.add(d);
						            updates=Updates.combine(Updates.set("TF/URL",TF_Prev_Arr));	
						            db.getCollection("InvertedFile").updateOne(idFilter, updates, upsert);
						            break;
}
				            	
				            }
				            
				            	
				            }
				        
				    } finally {
				        cursor.close();
				    }
				    }
				    else {
				    	criteria = new BasicDBObject();
						criteria.append("word",it_tokens_value);
						cursor = InvertedFile.find(criteria).cursor();  
						oldDoc = cursor.next();
			            //System.out.println(oldDoc.toJson());
			            upsert = new UpdateOptions().upsert(true);
			            idFilter = Filters.eq("_id", oldDoc.getObjectId("_id"));
			            TF_Prev_Arr=(ArrayList) (oldDoc.get("TF/URL"));
			            DF_Prev=(int) oldDoc.get("DF");
			            List<String> tags= new ArrayList<String>();	
			            tags.add(tag_choice);
			            org.bson.Document d= new org.bson.Document("URL",it_SeedURLs_value);
			            d.append("TF", 1);
			            d.append("Tags", tags);
			            TF_Prev_Arr.add(d);
			            updates=Updates.combine(Updates.set("TF/URL",TF_Prev_Arr),Updates.set("DF",DF_Prev+1));	
			            db.getCollection("InvertedFile").updateOne(idFilter, updates, upsert); 
				    }
				    
				}

			
		   }
		}
		//result=InvertedFile.find();
		System.out.println(stemmer.stem("frequently"));
		result=InvertedFile.find(new org.bson.Document("word", "frequent"));
		for(org.bson.Document document: result) {
			System.out.println(document.toJson());
		}
			

		
	}
}

