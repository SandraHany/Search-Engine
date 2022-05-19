package net.codejava;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
//gets urls in each url to be used for popularity calculation
public class IndexedLinks {
	static MongoDBClass mongodb=new MongoDBClass();
	static Set<String> linkIndexedBeforeSet= new HashSet<> ();
	  static MongoCollection indexedLinksCollection=mongodb.getIndexedLinksCollection(); 
	  static List<org.bson.Document> linkIndexedBeforeDocument= new ArrayList<org.bson.Document> ();
	  public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		  JSONParser parser = new JSONParser();
		   JSONArray a =(JSONArray) parser.parse(new FileReader("concats/Concatenated0.json")); //read crawler files
			List<String>urls_list = new ArrayList<String>();
	       for(int fileId=0;fileId<6;fileId++) {
		   a =(JSONArray) parser.parse(new FileReader("concats/Concatenated"+Integer.toString(fileId) + ".json"));
		   for (Object o : a)
		   {
			   
		     JSONObject obj = (JSONObject) o;
		     String link = (String) obj.get("url");
		     if(!linkIndexedBeforeSet.contains(link)) {
		    	 linkIndexedBeforeSet.add(link);
		     org.jsoup.nodes.Document doc=Jsoup.parse ((String) obj.get("document"));		    
		     org.bson.Document document= new org.bson.Document("URL",link);
		     urls_list=Arrays.asList(doc.select("a").attr("abs:href")); //to get links in this url
			 document.append("embedded URLs", urls_list);
			 linkIndexedBeforeDocument.add(document);

		    
		    }
		     
		  }
	       indexedLinksCollection.drop();
	       indexedLinksCollection.insertMany(linkIndexedBeforeDocument);
	     }	
	}
}
