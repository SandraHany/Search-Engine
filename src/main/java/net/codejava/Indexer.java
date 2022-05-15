package net.codejava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bson.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;

import crawlercommons.filters.basic.BasicURLNormalizer;
import opennlp.tools.stemmer.PorterStemmer;


public class Indexer {

    InsertManyOptions options= new InsertManyOptions();
   
	//static HashMap<String, docContainer> indexerDocuments = new HashMap<String, Boolean>();
	public static PorterStemmer stemmer =new PorterStemmer();
	static MongoDBClass db_connection=new MongoDBClass();
	//static HashMap<String, Boolean> CrawlerLinks = new HashMap<String, Boolean>();
	//static HashMap<String, org.jsoup.nodes.Document> URL_doc = new HashMap<String, org.jsoup.nodes.Document>();
	static ConcurrentHashMap<String, org.jsoup.nodes.Document> URL_doc = new ConcurrentHashMap<String, org.jsoup.nodes.Document>();
	static List<String>headings = new ArrayList<String>();
	static List<String>titles = new ArrayList<String>();
	static List<String>plainText = new ArrayList<String>();
	static List<String>stopWordsList = new ArrayList<String>();
	static List<String>dummyList = new ArrayList<String>();
	
	static List<String> crawlerLinks= new ArrayList<String>();
	static HashMap<String, org.bson.Document> indexerDocuments = new HashMap<String, org.bson.Document>();
	static HashMap<String, org.bson.Document> indexedBeforeDocuments = new HashMap<String, org.bson.Document>();
	static List<String>URL_Indexed_before_set = new ArrayList<String>();
	static List<org.bson.Document>URL_Indexed_before_documents = new ArrayList<org.bson.Document>();
	static List<String>URL_SET = new ArrayList<String>();
	static List<org.bson.Document>url_description = new ArrayList<org.bson.Document>();
	static int  num_documents;
	

	private static void getPreviouslyIndexedWords() {
		 FindIterable<Document> iterDoc=db_connection.DBgetCollection("Indexer").find();
	
			Iterator it = iterDoc.iterator();
			while(it.hasNext()) {
				
				 Document document = (Document) it.next();
				 String word=document.getString("word");
				 
				 indexerDocuments.put(word,document);
	             System.out.println("added word---->"+ word +" to indexed before");
	         
			}
	}
	private static int  getWordCount(org.jsoup.nodes.Document doc) {
		List<String>count_words = new ArrayList<String>();
		stopWords();  
		count_words=Arrays.asList(doc.text().toLowerCase().split(" "));
		//count_words.removeAll(stopWordsList);
		return  count_words.size();
	}
	private static void stopWords() {
		FileInputStream fis;;
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
	}
	private static void URLTags_Preprocessed(org.jsoup.nodes.Document doc, String URL) {
		if(doc!=null) {
			int url_word_count=getWordCount(doc);
			doc.select("a[href]").remove();
			//System.out.println(doc);
			List<String>s_arr;
			dummyList=Arrays.asList(doc.select("title").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"title",url_word_count);
				 }
				 }
			 }
			// dummy.clear();
			 dummyList=Arrays.asList(doc.select("h1").text().toLowerCase().split(" "));
			
				 for(String str:dummyList) {
					 s_arr=preprocess(str);
					 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
					 }
			 }
			 //dummy.clear();
			 dummyList=Arrays.asList(doc.select("h2").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			 //dummy.clear();
			 dummyList=Arrays.asList(doc.select("h3").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			// dummy.clear();
			 dummyList=Arrays.asList(doc.select("h4").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			 //dummy.clear();
			 dummyList=Arrays.asList(doc.select("h5").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			 //dummy.clear();
			 dummyList=Arrays.asList(doc.select("h6").text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			
			 doc.select("h1").remove();
			 doc.select("h2").remove();
			 doc.select("h3").remove();
			 doc.select("h4").remove();
			 doc.select("h5").remove();
			 doc.select("h6").remove();
			 doc.select("title").remove();
			 dummyList=Arrays.asList(doc.text().toLowerCase().split(" "));
			 for(String str:dummyList) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null ) {
					 addToHashmap(s,URL,"Plain Text",url_word_count);
				 }
				 }
			 }
			}
		 //dummy.clear();
		System.out.println("Indexed----->"+ URL);
		
	}
	private static void addToHashmap(String word , String URL, String tag,int wordCount){
		//System.out.println(word);
		if(!indexerDocuments.containsKey(word)) {
			//URLs_DB.add(it_SeedURLs_value);
			//document_url_tf= new org.bson.Document("URL",it_SeedURLs_value) .append("TF", 1);
			Document document0 = new org.bson.Document("word",word).append("DF",1);
			ArrayList<Document> TFs_URLs = new ArrayList<org.bson.Document>();
			List<String> tags= new ArrayList<String>();
			tags.add(tag);
			org.bson.Document d= new org.bson.Document("URL",URL) .append("TF", 1);
			d.append("Tags", tags);
			d.append("NormalisedTF", Math.ceil(1/wordCount));
			TFs_URLs.add(d);
			document0.append("TF/URL", TFs_URLs);
			indexerDocuments.put(word, document0);
			
			//db.getCollection("InvertedFile0").insertOne([{word:it_tokens_value,DF:1}]);
		}
		else {
       
			org.bson.Document oldDoc = indexerDocuments.get(word);
			Boolean found=false;
			
				
			ArrayList<org.bson.Document> TF_Prev_Arr=new ArrayList<org.bson.Document>((ArrayList) (oldDoc.get("TF/URL")));
            //Set<String> tags= new HashSet<String>();	
            String dummy=null;
            for(int y=0;y<TF_Prev_Arr.size();y++) {
            	dummy=(String) ((org.bson.Document)(TF_Prev_Arr.get(y))).get("URL");

            	if(dummy.equals(URL)) {
            		Integer TF_Prev=(Integer) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("TF"));	
            		List<String> tags= new ArrayList<String>();
            		tags=(List<String>) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("Tags"));
            		if(tags==null) {
            			tags=new ArrayList<String>();
            			tags.add(tag);
            		}
            		else if(!tags.contains(tag) ) {
            		tags.add(tag);
            		}
            		TF_Prev_Arr.remove(y);	
            		org.bson.Document  d= new org.bson.Document("URL",URL);
            		d.append("TF", TF_Prev+1);
            		d.append("Tags",tags);
            		d.append("NormalisedTF", Math.ceil((TF_Prev+1)/wordCount));
		            TF_Prev_Arr.add(d);
		            oldDoc.replace("TF/URL",TF_Prev_Arr);	
		            indexerDocuments.put(word, oldDoc);
		            found=true;
		            break;

            	
            }
		
		        
		   
            }
			
		         if(found==false) {   
		    	org.bson.Document document = indexerDocuments.get(word);	    
		    	 TF_Prev_Arr=(ArrayList) (document.get("TF/URL"));
	            int DF_Prev=(Integer) document.get("DF");
	            List<String> tags= new ArrayList<String>();	
	            tags.add(tag);
	            org.bson.Document doc= new org.bson.Document("URL",URL);
	            doc.append("TF", 1);
	            doc.append("NormalisedTF",Math.ceil( 1/wordCount));
	            doc.append("Tags", tags);
	            TF_Prev_Arr.add(doc);
	            document.replace("DF", DF_Prev+1);
	            document.replace("TF/URL", TF_Prev_Arr);
	            indexerDocuments.put(word, document);
		         }
	            
		    }
		
	}
	private static List<String> preprocess(String str) {
		 List<String> list_preprocess= new ArrayList<String>();
	     String[] str_arr;
		 str=str.replaceAll("[^a-zA-Z0-9]", " ");
	//	str=str.replaceAll("[^a-zA-Z]", " ");
		  str=str.replaceAll("\\s", " ");
		  str_arr=str.split(" ");
		  for(String str1:str_arr) {
		  if (! str1.isBlank()  && !stopWordsList.contains(str1 ) ){
			
			list_preprocess.add( stemmer.stem(str1));
		  }
		  else {
			  list_preprocess.add(null);
		  }
		  }
		  return list_preprocess;
		  
	  }
	
	 private static void getPeviouslyIndexed() {
		 
		 FindIterable<Document> iterDoc=db_connection.DBgetCollection("IndexedURLs").find();
			@SuppressWarnings("deprecation")
			
			Iterator it = iterDoc.iterator();
			while(it.hasNext()) {
				 Document document = (Document) it.next();
				 String url=document.getString("URL");
	            
	             URL_Indexed_before_set.add(url);
	             System.out.println("added "+ url +" to indexed before");
	            // URL_SET.add(url);
	         
			}
	 }	
	private static  org.jsoup.nodes.Document getDoc(String url) {
		 Connection connection = Jsoup.connect(url);   
		 connection.userAgent("Mozilla");
		 connection.timeout(10 * 1000);
		 try {
			org.jsoup.nodes.Document doc = connection.get();
		  return doc;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
		
		
    }
	public static void main(String[] args) {
		 MongoClient mongoClient1= MongoClients.create(
         		"mongodb://Sandra:fmCs6CAZx0phSrjs@cluster0-shard-00-00.l6yha.mongodb.net:27017,cluster0-shard-00-01.l6yha.mongodb.net:27017,cluster0-shard-00-02.l6yha.mongodb.net:27017/SearchEngine?ssl=true&replicaSet=atlas-3bz7rt-shard-0&authSource=admin&retryWrites=true&w=majority");
         MongoClient mongoClient2;
         MongoDatabase db1;
         MongoDatabase db2;
		 MongoCollection crawlerLinks1;
         MongoCollection crawlerLinks2;
  
            
            db1 = mongoClient1.getDatabase("SearchEngine");
            crawlerLinks1 = db1.getCollection("CrawlerLinks"); // A collection of links to be crawled
           

            mongoClient2 = MongoClients.create(
            		"mongodb://Sandra:fmCs6CAZx0phSrjs@cluster0-shard-00-00.real4.mongodb.net:27017,cluster0-shard-00-01.real4.mongodb.net:27017,cluster0-shard-00-02.real4.mongodb.net:27017/SearchEngine?ssl=true&replicaSet=atlas-bhl30t-shard-0&authSource=admin&retryWrites=true&w=majority");
            db2 = mongoClient2.getDatabase("SearchEngine");
            crawlerLinks2 = db2.getCollection("CrawlerLinks");
		
	
			
		//System.out.println(db_connection.DBgetCollection("CrawlerLinks").find());
				getPeviouslyIndexed();
				//getCrawlerLinks();
				FindIterable<Document> iterDoc=crawlerLinks1.find();
				@SuppressWarnings("deprecation")
				//int num_documents=(int) db_connection.DBgetCollection("CrawlerLinks").countDocuments();
				Iterator it = iterDoc.iterator();
				while(it.hasNext()) {
					 Document document = (Document) it.next();
					 String url=document.getString("url");
					 if(URL_Indexed_before_set.contains(url)) {
					 }
					 else {
		             org.jsoup.nodes.Document doc=Jsoup.parse(document.getString("document"));
					//	 org.jsoup.nodes.Document doc=document.getString("document");
					 URL_doc.put(url, doc);
		             URL_SET.add(url);
		             System.out.println("got url "+ url);
		             
					 }
		            // URL_SET.add(url);
		         
				}
				 iterDoc=crawlerLinks2.find();
				
				 it = iterDoc.iterator();
				while(it.hasNext()) {
					 Document document = (Document) it.next();
					 String url=document.getString("url");
					 if(URL_Indexed_before_set.contains(url)) {
					 }
					 else {
		             org.jsoup.nodes.Document doc=Jsoup.parse(document.getString("document"));
					//	 org.jsoup.nodes.Document doc=document.getString("document");
					 URL_doc.put(url, doc);
		             URL_SET.add(url);
		             System.out.println("got url "+ url);
		             
					 }
		            // URL_SET.add(url);
		         
				}

				
				//num_documents=URL_doc.size();
				num_documents=URL_SET.size();
				System.out.println(URL_SET.size());
		        stopWords();    
		        System.out.println("got crawler links");
		      
		        for (String url : URL_doc.keySet()) {
		        	URLTags_Preprocessed(URL_doc.get(url),url);
		        	org.bson.Document d= new org.bson.Document("URL",url);
		        	Element meta = (URL_doc.get(url)).select("meta[name=description]").first();
		        	d.append("description", meta);
		        	List<String>urls_list = new ArrayList<String>();
		        	urls_list=Arrays.asList((URL_doc.get(url)).select("a").attr("abs:href"));
		        	d.append("embedded URLs", urls_list);
		        	URL_Indexed_before_documents.add(d);
		        	
		        	//System.out.println(url + "--------> indexed");
		        }
		           db_connection.DBgetCollection("IndexedURLs").insertMany(URL_Indexed_before_documents);
			       if(indexerDocuments.isEmpty()) {
			    	   System.out.println("nothing to index");
			       }
			       else {
		        	List<org.bson.Document> insert_links = new ArrayList<org.bson.Document>(indexerDocuments.values());
		        	long start = System.currentTimeMillis();
		        	System.out.println("start inserting");
		        	db_connection.DBgetCollection("Indexer").insertMany(insert_links);

		        	
		        	System.out.println("Indexed Successfully");
		        	long end = System.currentTimeMillis();
		        	long elapsedTime = end - start;
		        	System.out.println("Insert Time"+ elapsedTime);
		    		
			       }
			}
}