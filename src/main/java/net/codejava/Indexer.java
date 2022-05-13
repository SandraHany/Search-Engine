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
import java.util.function.Consumer;

import org.bson.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;

import crawlercommons.filters.basic.BasicURLNormalizer;
import opennlp.tools.stemmer.PorterStemmer;


public class Indexer {

	public static  class MongoDBClass {
		private MongoClient mongoClient;
	    private MongoDatabase db; 
	    public MongoDBClass() {
			
		
	    	this. mongoClient = MongoClients.create("mongodb+srv://Sandra:fmCs6CAZx0phSrjs@cluster0.real4.mongodb.net/SearchEngine?retryWrites=true&w=majority");
	    	this. db = mongoClient.getDatabase("SearchEngine");		
			
	    }
	    public MongoCollection<Document> DBgetCollection(String CollectionName) {
	    	return db.getCollection(CollectionName);
	    }
	}
	//static HashMap<String, docContainer> indexerDocuments = new HashMap<String, Boolean>();
	public static PorterStemmer stemmer =new PorterStemmer();
	static MongoDBClass db_connection=new MongoDBClass();
	//static HashMap<String, Boolean> CrawlerLinks = new HashMap<String, Boolean>();
	static HashMap<String, org.jsoup.nodes.Document> URL_doc = new HashMap<String, org.jsoup.nodes.Document>();
	static List<String>headings = new ArrayList<String>();
	static List<String>titles = new ArrayList<String>();
	static List<String>plainText = new ArrayList<String>();
	static List<String>stopWordsList = new ArrayList<String>();
	static List<String>dummyList = new ArrayList<String>();
	static List<String> crawlerLinks= new ArrayList<String>();
	static HashMap<String, org.bson.Document> indexerDocuments = new HashMap<String, org.bson.Document>();
	static List<String>URL_Indexed_before_set = new ArrayList<String>();
	static List<org.bson.Document>URL_Indexed_before_documents = new ArrayList<org.bson.Document>();
	static List<String>URL_SET = new ArrayList<String>();
	static int  num_documents;
	

	private static void getCrawlerLinks() {
		/*FindIterable<Document> iterDoc=db_connection.DBgetCollection("CrawlerLinks").find();
		@SuppressWarnings("deprecation")
		int num_documents=(int) db_connection.DBgetCollection("CrawlerLinks").count();
		Iterator it = iterDoc.iterator();
		while(it.hasNext()) {
			 Document document = (Document) it.next();
			 String url=document.getString("url");
             org.jsoup.nodes.Document doc=null;
			 URL_doc.put(url, doc);
		}
		num_documents=URL_doc.size();
		System.out.println(URL_doc.size());*/
			 MultithreadingCrawlerLinks crawler_links_1= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_2= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_3= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_4= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_5= new MultithreadingCrawlerLinks(URL_doc,num_documents);
				
			 Thread t1=new Thread(crawler_links_1);
			 t1.setName("1");
			 Thread t2=new Thread(crawler_links_2);
			 t2.setName("1");
			 Thread t3=new Thread(crawler_links_3);
			 t3.setName("3");
			 Thread t4=new Thread(crawler_links_4);
			 t4.setName("4");
			 Thread t5=new Thread(crawler_links_5);
			 t5.setName("5");
				t1.start();
				t2.start();
				t3.start();
				t4.start();
				t5.start();
		
				URL_doc=MultithreadingCrawlerLinks.URL_doc;
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
		if(doc !=null) {
		dummyList=Arrays.asList(doc.select("title").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"title");
			 }
		 }
		// dummy.clear();
		 dummyList=Arrays.asList(doc.select("h1").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("h2").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("h3").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		// dummy.clear();
		 dummyList=Arrays.asList(doc.select("h4").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("h5").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("h6").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Heading");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("pre").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Plain Text");
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("li").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
				 addToHashmap(s,URL,"Plain Text");;
			 }
		 }
		 //dummy.clear();
		 dummyList=Arrays.asList(doc.select("p").text().toLowerCase().split(" "));
		 for(String s:dummyList) {
			 s=preprocess(s);
			 if(s!=null ) {
		  			
			    addToHashmap(s,URL,"Plain Text");
				 
			 }
		 }
		 //dummy.clear();
		System.out.println("Indexed----->"+ URL);
		}
	}
	private static void addToHashmap(String word , String URL, String tag){
		if(!indexerDocuments.containsKey(word)) {
			//URLs_DB.add(it_SeedURLs_value);
			//document_url_tf= new org.bson.Document("URL",it_SeedURLs_value) .append("TF", 1);
			Document document0 = new org.bson.Document("word",word).append("DF",1);
			ArrayList<Document> TFs_URLs = new ArrayList<org.bson.Document>();
			List<String> tags= new ArrayList<String>();
			tags.add(tag);
			org.bson.Document d= new org.bson.Document("URL",URL) .append("TF", 1);
			d.append("Tags", tags);
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
	            doc.append("Tags", tags);
	            TF_Prev_Arr.add(doc);
	            document.replace("DF", DF_Prev+1);
	            document.replace("TF/URL", TF_Prev_Arr);
	            indexerDocuments.put(word, document);
		         }
	            
		    }
		
	}
	private static String preprocess(String str) {
	  
		  str=str.replaceAll("[^a-zA-Z0-9]", " ");
		  str=str.replaceAll("\\s", "");
		  if (! str.isBlank()  && !stopWordsList.contains(str ) ){
			
			return stemmer.stem(str);
		  }
		  else {
			  return null; 
		  }
	  }
	
	 private static void getPeviouslyIndexed() {
		 
		 FindIterable<Document> iterDoc=db_connection.DBgetCollection("IndexedURLS").find();
			@SuppressWarnings("deprecation")
			
			Iterator it = iterDoc.iterator();
			while(it.hasNext()) {
				 Document document = (Document) it.next();
				 String url=document.getString("url");
	     
	             URL_Indexed_before_set.add(url);
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
		
		//System.out.println(db_connection.DBgetCollection("CrawlerLinks").find());
		getPeviouslyIndexed();
		//getCrawlerLinks();
		FindIterable<Document> iterDoc=db_connection.DBgetCollection("CrawlerLinks").find();
		@SuppressWarnings("deprecation")
		int num_documents=(int) db_connection.DBgetCollection("CrawlerLinks").count();
		Iterator it = iterDoc.iterator();
		while(it.hasNext()) {
			 Document document = (Document) it.next();
			 String url=document.getString("url");
			 if(!URL_Indexed_before_set.contains(url)) {
             org.jsoup.nodes.Document doc=null;
			 URL_doc.put(url, doc);
			 }
            // URL_SET.add(url);
         
		}
		num_documents=URL_doc.size();
		System.out.println(URL_doc.size());
			 MultithreadingCrawlerLinks crawler_links_1= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_2= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_3= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_4= new MultithreadingCrawlerLinks(URL_doc,num_documents);
			 MultithreadingCrawlerLinks crawler_links_5= new MultithreadingCrawlerLinks(URL_doc,num_documents);
				
			 Thread t1=new Thread(crawler_links_1);
			 t1.setName("1");
			 Thread t2=new Thread(crawler_links_2);
			 t2.setName("1");
			 Thread t3=new Thread(crawler_links_3);
			 t3.setName("3");
			 Thread t4=new Thread(crawler_links_4);
			 t4.setName("4");
			 Thread t5=new Thread(crawler_links_5);
			 t5.setName("5");
				t1.start();
				t2.start();
				t3.start();
				t4.start();
				t5.start();
				try {
					t1.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					t2.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					t3.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					t4.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					t5.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        stopWords();
       
        
        //shuld be added to getCrawlerLinks();

        
        //System.out.println(IndexedLinks);
		/*URL_doc.entrySet().forEach(entry->{
			URLTags_Preprocessed(entry.getValue(),entry.getKey());
			Index();
			IndexedLinks.put(entry.getKey(), true);
		});*/
        
        System.out.println("got crawler links");
        URL_doc=MultithreadingCrawlerLinks.URL_doc;
        for (String url : URL_doc.keySet()) {
        	URLTags_Preprocessed(URL_doc.get(url),url);
        	org.bson.Document d= new org.bson.Document("URL",url);
        	URL_Indexed_before_documents.add(d);
        	//System.out.println(url + "--------> indexed");
        }
        db_connection.DBgetCollection("IndexedURLS").insertMany(URL_Indexed_before_documents);
		
		//db_connection.DBgetCollection("Indexer2").deleteMany(new org.bson.Document());
        	//for(String word: indexerDocuments.keySet()) {
        	List<org.bson.Document> list = new ArrayList<org.bson.Document>(indexerDocuments.values());
        	long start = System.currentTimeMillis();
        	db_connection.DBgetCollection("Indexer").insertMany(list);
        	//}
        	
        	
        	
        	System.out.println("Indexed Successfully");
        	long end = System.currentTimeMillis();
        	long elapsedTime = end - start;
        	System.out.println("Insert Time"+ elapsedTime);
		//db_connection.DBgetCollection("Indexer2").insertMany((List<? extends org.bson.Document>) Arrays.asList(indexerDocuments.values()));

	}
}

class MultithreadingCrawlerLinks implements Runnable {
	public static HashMap<String, org.jsoup.nodes.Document> URL_doc ;
	int numDocs;
	public MultithreadingCrawlerLinks(HashMap<String, org.jsoup.nodes.Document> url_doc, int num_documents) {
		URL_doc=url_doc;
		numDocs=num_documents;
	}
	
    public void run()
    {
    	if (Thread.currentThread().getName().equals("1")) {
			do_work1(); 
		}

		else if (Thread.currentThread().getName().equals("2")) {
			do_work2();
		}
		else if (Thread.currentThread().getName().equals("3")) {
			do_work3();
		}
		else if (Thread.currentThread().getName().equals("4")) {
			do_work4();
		}
		else if (Thread.currentThread().getName().equals("5")) {
			do_work5();
		}
    }
    void do_work1 () {
		synchronized (this) {
			
			 for (Map.Entry<String,org.jsoup.nodes.Document> entry : new ArrayList<Map.Entry<String,org.jsoup.nodes.Document>>(URL_doc.entrySet()).subList(0, numDocs/5)) {
			        String url = entry.getKey();
			        org.jsoup.nodes.Document doc;
					try {
						doc = Jsoup.connect(url).get();
						URL_doc.put(url, doc);
						System.out.println(url+ "Thread 1  downloaded");
					} catch (IOException e) {
						URL_doc.remove(url);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			    }
		
		}
		
    }
    void do_work2 () {
		
			synchronized (this) {
				 for (Map.Entry<String,org.jsoup.nodes.Document> entry : new ArrayList<Map.Entry<String,org.jsoup.nodes.Document>>(URL_doc.entrySet()).subList(numDocs/5, 2*numDocs/5)) {
				        String url = entry.getKey();
				        org.jsoup.nodes.Document doc;
						try {
							doc = Jsoup.connect(url).get();
							URL_doc.put(url, doc);
							System.out.println(url+ "Thread 2  downloaded");
						} catch (IOException e) {
							URL_doc.remove(url);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        
				    }
			
			}
			
    }

void do_work3 () {
		
			synchronized (this) {
				 for (Map.Entry<String,org.jsoup.nodes.Document> entry : new ArrayList<Map.Entry<String,org.jsoup.nodes.Document>>(URL_doc.entrySet()).subList(2*numDocs/5, 3*numDocs/5)) {
				        String url = entry.getKey();
				        org.jsoup.nodes.Document doc;
						try {
							doc = Jsoup.connect(url).get();
							URL_doc.put(url, doc);
							System.out.println(url + "Thread 3  downloaded");
						} catch (IOException e) {
							URL_doc.remove(url);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        
				    }
			
			}
			
 }
void do_work4 () {
	
		synchronized (this) {
			 for (Map.Entry<String,org.jsoup.nodes.Document> entry : new ArrayList<Map.Entry<String,org.jsoup.nodes.Document>>(URL_doc.entrySet()).subList(3*numDocs/5, 4*numDocs/5)) {
			        String url = entry.getKey();
			        org.jsoup.nodes.Document doc;
					try {
						doc = Jsoup.connect(url).get();
						URL_doc.put(url, doc);
						System.out.println(url+ "Thread 4  downloaded");
					} catch (IOException e) {
						URL_doc.remove(url);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			    }
		
		}
		
}
void do_work5 () {
	
		synchronized (this) {
			 for (Map.Entry<String,org.jsoup.nodes.Document> entry : new ArrayList<Map.Entry<String,org.jsoup.nodes.Document>>(URL_doc.entrySet()).subList(4*numDocs/5,numDocs)) {
			        String url = entry.getKey();
			        org.jsoup.nodes.Document doc;
					try {
						doc = Jsoup.connect(url).get();
						URL_doc.put(url, doc);
						System.out.println(url+ "Thread 5  downloaded");
					} catch (IOException e) {
						URL_doc.remove(url);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			    }
		
		}
		
}
}
