package net.codejava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import opennlp.tools.stemmer.PorterStemmer;

class multiThreadingInsertion implements Runnable {
	//threading for faster insertion into database
	static MongoDBClass mongodb=new MongoDBClass();//class that contains database info
	static  MongoCollection indexerCollection=mongodb.getIndexerCollection(); 
	List<org.bson.Document> insertThread ; //the slice of documents to be inserted in indexer in this thread
	
	
	public multiThreadingInsertion(List<org.bson.Document> insertThreadInput) {		
		insertThread=insertThreadInput;
	}
	
    public void run()
    {	
    	indexerCollection.insertMany(insertThread);
    }
 }

public class Indexer  {
	static MongoDBClass mongodb=new MongoDBClass();  
	static  MongoCollection indexerCollection=mongodb.getIndexerCollection(); 
	static  MongoCollection indexedLinksCollection=mongodb.getIndexedLinksCollection(); 
	static Set<String> linkIndexedBeforeSet= new HashSet<> (); //use set to avoid duplicates and set.contains is o(1)
	static List<org.bson.Document> linkIndexedBeforeDocument= new ArrayList<org.bson.Document> ();	 
	static HashMap<String, org.jsoup.nodes.Document> LinkDocument = new HashMap<String, org.jsoup.nodes.Document>();//use hashmap because searching for word is o(1)
	public static PorterStemmer stemmer =new PorterStemmer(); //applies stemming                                          
	static Set<String> stopWordsList = new HashSet<> ();//use set to avoid duplicates and set.contains is o(1)
	static Set<String> dummySet;
	static HashMap<String, org.bson.Document> indexerDocuments = new HashMap<String, org.bson.Document>();//documents to inserted in db
	
	private static void ReadCrawlerFiles() throws FileNotFoundException, IOException, ParseException { // reads json crawler files to get downloaded html document of each url
	   JSONParser parser = new JSONParser();
	   JSONArray a =(JSONArray) parser.parse(new FileReader("concats/Concatenated0.json"));

       for(int fileId=0;fileId<6;fileId++) {
	   a =(JSONArray) parser.parse(new FileReader("concats/Concatenated"+Integer.toString(fileId) + ".json"));
	   for (Object o : a)
	   {
	     JSONObject obj = (JSONObject) o;
	     String link = (String) obj.get("url");
	      org.jsoup.nodes.Document doc=Jsoup.parse ((String) obj.get("document"));
	     
			
			
	      if(!linkIndexedBeforeSet.contains(link)){ // to avoid indexing a previously indexed link
	      URLTags_Preprocessed(doc,link);   //indexing in this for loop to avoid multiple for loops for faster execution
	      org.bson.Document d= new org.bson.Document("URL",link);
	      linkIndexedBeforeSet.add(link);  //could make a hashmap with link as key and document as value and then check if link exists in the keyset of this hashmap
	      linkIndexedBeforeDocument.add(d); //to add to mongodb the indexed urls to avoid indexing a previously indexed link afterwards
	      }
	    
	   }
	    
       }			   
   }

	private static List<String> preprocess(String str) {
		 List<String> list_preprocess= new ArrayList<String>();
	     String[] str_arr;
		 str=str.replaceAll("[^a-zA-Z0-9]", " "); //remove symbols
		  str=str.replaceAll("\\s", " "); //remove spaces greater than "n"
		  str_arr=str.split(" "); //get tokens as emp.id will be considered 2 words 
	
		  for(String str1:str_arr) {
		  if (! str1.isBlank()  && !stopWordsList.contains(str1 ) ){//check for blank because symbols will be removed
			                										//check for stop words after splitting 	
			list_preprocess.add( stemmer.stem(str1));
		  }
		  else {
			  list_preprocess.add(null); //flag word to be null so as not to be added to index
		  }
		  }
		  return list_preprocess;
		  
	  }
	private static float  getWordCount(org.jsoup.nodes.Document doc) {   //to calculate number of words in document for normalised tf
		Set<String>count_words ;
		
		count_words=new HashSet<>(Arrays.asList(doc.text().toLowerCase().split(" ")));
		count_words.removeAll(stopWordsList);
		return  count_words.size();
	}
	private static void stopWords() { //initialise stop words list
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
	private static void URLTags_Preprocessed(org.jsoup.nodes.Document doc, String URL) { //function that gets headings, titles, normal in given link then preprocess them
		if(doc!=null) {	//to check if jsoup could connect already done in crawler so could be removed																
			float url_word_count=getWordCount(doc);
			doc.select("a[href]").remove();
			List<String>s_arr;
			dummySet = new HashSet<>(Arrays.asList(doc.select("title").text().toLowerCase().split(" ")));
			dummySet.removeAll(stopWordsList);  //remove all stop word to decrease length of for loop for faster execution
			 for(String str:dummySet) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null && !stopWordsList.contains(s ) ) {
					 addToHashmap(s,URL,"Title",url_word_count); 
				 }
				 }
			 }
			 dummySet = new HashSet<>(Arrays.asList(doc.select("h1, h2, h3, h4, h5, h6").text().toLowerCase().split(" ")));
			 dummySet.removeAll(stopWordsList);
			 for(String str:dummySet) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null && !stopWordsList.contains(s )) {
					 addToHashmap(s,URL,"Heading",url_word_count);
				 }
				 }
			 }
			
			 doc.select("h1, h2, h3, h4, h5, h6").remove();
			 doc.select("title").remove();
			 dummySet=new HashSet<>(Arrays.asList(doc.text().toLowerCase().split(" ")));
			 //remove all is o(n) and contains is o(1) 
			 dummySet.removeAll(stopWordsList);
			 for(String str:dummySet) {
				 s_arr=preprocess(str);
				 for(String s:s_arr ) {
				 if(s!=null && !stopWordsList.contains(s )) {
					 addToHashmap(s,URL,"Plain Text",url_word_count);
				 }
				 }
			 }
			}
		System.out.println("Indexed----->"+ URL);
		
	}
	private static void addToHashmap(String word , String URL, String tag,float wordCount){// the function that gets tf,df and links of word and heading,title,normal freuqncies
	
		if(!indexerDocuments.containsKey(word)) {
						
			
			Document document0 = new org.bson.Document("word",word).append("DF",1);//initially if word isn't in index then its df is one
			ArrayList<Document> TFs_URLs = new ArrayList<org.bson.Document>();
			org.bson.Document d= new org.bson.Document("URL",URL) .append("TF", 1);//initially if word isn't in index then its tf is one
			d.append("NormalisedTF", Math.ceil(1.0/wordCount));
			int headingFrequency=0;
			int titleFrequency=0;
			int normalFrequency=0;
			if(tag=="Plain Text") {
				normalFrequency++;
			}
			else if(tag=="Heading") {
				headingFrequency++;
			}
			else {
				titleFrequency++;
			}
			d.append("headingFrequency", headingFrequency);
			d.append("titleFrequency", titleFrequency);
			d.append("normalFrequency", normalFrequency);			
			TFs_URLs.add(d);
			document0.append("Details", TFs_URLs);
			indexerDocuments.put(word, document0);
		
		}
		else {
       
				org.bson.Document oldDoc = indexerDocuments.get(word);
				Boolean found=false;
				
				ArrayList<org.bson.Document> TF_Prev_Arr=new ArrayList<org.bson.Document>((ArrayList) (oldDoc.get("Details")));
				ArrayList<String> url_list=new ArrayList<String>();
				String dummy=null;
				 for(int y=0;y<TF_Prev_Arr.size();y++) {
					 dummy=(String) ((org.bson.Document)(TF_Prev_Arr.get(y))).get("URL");
					 url_list.add(dummy);
				 }

            	if(url_list.contains(URL)) {
            		int y=url_list.indexOf(URL);
            		Integer TF_Prev=(Integer) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("TF"));	
            		int headingFrequency;
        			int titleFrequency;
        			int normalFrequency;
        			headingFrequency=(int) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("headingFrequency")); //word exists themget its frequencies to update the, 
        			titleFrequency=(int) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("titleFrequency"));
        			normalFrequency=(int) (((org.bson.Document)(TF_Prev_Arr.get(y))).get("normalFrequency"));
            		/*if(tags==null) {
            			tags=new ArrayList<String>();
            			tags.add(tag);
            		}
            		else {
            		tags.add(tag);
            		}*/
            		TF_Prev_Arr.remove(y);	//update details array 
            		org.bson.Document  d= new org.bson.Document("URL",URL);
            		d.append("TF", TF_Prev+1);
            		d.append("NormalisedTF", Math.ceil(((float)(TF_Prev+1))/wordCount));
        			if(tag=="Plain Text") {
        				normalFrequency++;
        			}
        			else if(tag=="Heading") {
        				headingFrequency++;
        			}
        			else {
        				titleFrequency++;
        			}
        			d.append("headingFrequency", headingFrequency);
        			d.append("titleFrequency", titleFrequency);
        			d.append("normalFrequency", normalFrequency);
            	    //d.append("wordCount",wordCount);
		            TF_Prev_Arr.add(d);
		            oldDoc.replace("Details",TF_Prev_Arr);	
		            indexerDocuments.put(word, oldDoc);     	
              }
             else ///word exists however not as a word in the current link
			
             {   
		    	org.bson.Document document = indexerDocuments.get(word);	    
		    	 TF_Prev_Arr=(ArrayList<Document>) (document.get("Details"));
		        int DF_Prev=(Integer) document.get("DF");
		        org.bson.Document doc= new org.bson.Document("URL",URL);
		        doc.append("TF", 1);
		        doc.append("NormalisedTF",Math.ceil( 1.0/wordCount));
				int headingFrequency=0;
				int titleFrequency=0;
				int normalFrequency=0;
				if(tag=="Plain Text") {
					normalFrequency++;
				}
				else if(tag=="Heading") {
					headingFrequency++;
				}
				else {
					titleFrequency++;
				}
				doc.append("headingFrequency", headingFrequency);
				doc.append("titleFrequency", titleFrequency);
				doc.append("normalFrequency", normalFrequency);
		        TF_Prev_Arr.add(doc);
		        document.replace("DF", DF_Prev+1);
		        document.replace("Details", TF_Prev_Arr);
		        indexerDocuments.put(word, document);
	         }
	            
		  }
		
		
	}
	private static void getPeviouslyIndexedLinks() {
		FindIterable<Document> iterDoc=indexedLinksCollection.find();
			@SuppressWarnings("deprecation")
			
			Iterator it = iterDoc.iterator();
			while(it.hasNext()) {
				 Document document = (Document) it.next();
				 String url=document.getString("URL");
	            
	             linkIndexedBeforeSet.add(url);
	             System.out.println("added "+ url +" to indexed before");
			}
	 }	
	private static void getPreviouslyIndexedWords() {
		FindIterable<Document> iterDoc=indexerCollection.find();
			Iterator it = iterDoc.iterator();
			while(it.hasNext()) {
				
				 Document document = (Document) it.next();
				 String word=document.getString("word");
				 
				 indexerDocuments.put(word,document);
	             System.out.println("added word---->"+ word +" to indexed before");
	         
			}
	}
	
   public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
	   //to initilaise set of links indexed before to avoid indexing a url that has already been indexed
	   getPeviouslyIndexedLinks();
	   getPreviouslyIndexedWords(); //to update indexer as function updateMany takes longer time than insert many
	   //to update index if words already exist there 
	   // get set of all stop words to avoid inserting stop word into index
	   stopWords();  
	   System.out.println("Start of Indexing");
   	   long start = System.currentTimeMillis();
	   ReadCrawlerFiles();//the function that reads the downloaded html documemts and starts indexing 
	  long end = System.currentTimeMillis();
	  long elapsedTime = end - start;
      System.out.println("indexing time in milliseconds: "+elapsedTime);
	  List<org.bson.Document> indexerInsertion = new ArrayList<org.bson.Document>(indexerDocuments.values());
	  int sliceSize=10000;//for faster insertMany()
	  int numThreads=(int) Math.ceil(indexerInsertion.size()/((float)sliceSize));
	  Thread[] insertionThreads = new Thread[numThreads];
	  List<org.bson.Document> insertThreadInputSlice;
	  if(!indexerDocuments.isEmpty()) {
		 indexerCollection.drop(); 
	  }
	  for (int i = 0; i < numThreads; i++) {
		  if((i+1)*10000>indexerInsertion.size()) {
			  insertThreadInputSlice=indexerInsertion.subList(i*sliceSize,indexerInsertion.size());
		  }
		  else {
		  insertThreadInputSlice=indexerInsertion.subList(i*sliceSize, (i+1)*sliceSize);
		  }
		  insertionThreads[i] = new Thread(new multiThreadingInsertion( insertThreadInputSlice));
		  insertionThreads[i].start();
     }
	  // join threads
	 for (int i = 0; i < numThreads; i++) {
	      try {
	    	  insertionThreads[i].join();
	      } catch (InterruptedException e) {
	          e.printStackTrace();
	      }
	 }
     System.out.println("finished inserting into indexer collection");
     if(!linkIndexedBeforeDocument.isEmpty()) {
    	indexedLinksCollection.drop();
    	indexedLinksCollection.insertMany( (List<? extends Document>) (linkIndexedBeforeDocument));
     }
     System.out.println("updated previously indexed links collection");   
     System.out.println("End of Indexer");  
   }
}
