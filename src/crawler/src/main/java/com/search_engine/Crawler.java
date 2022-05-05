package com.search_engine;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.text.Normalizer;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;
import org.bson.conversions.Bson;

import crawlercommons.filters.basic.BasicURLNormalizer;

public class Crawler implements Runnable {

    /*
     * A hashmap to store the links of the crawled sites loaded from the database
     * to check against when inserting new links
     */
    static HashMap<String, Integer> CrawledLinksMap = new HashMap<String, Integer>();

    /*
     * The number of max links to be crawled
     */
    static int maxLinks = 5000;

    /*
     * A queue to store the links to be crawled
     * A queue is used to perform a breadth first search
     */
    static Queue<String> links = new LinkedList<String>();

    /*
     * A variable to state the number of links crawled
     */
    static int level = 0;

    /*
     * An instance of the seed manager class
     */
    static SeedManager seedManager = new SeedManager();

    /*
     * Class RobobtChecker is responsible for the following:
     * -extracting the host url,path and protocol from the given url
     * -checking if the host has a robobt.txt file
     * -if it does
     * - it checks the crawler autherized routes and if applicable add the url to
     * the list of urls to be crawled
     * -if it does not
     * -then it add it to the list of urls to be crawled
     */
    public static class RobotChecker {
        private String host;
        private String url;
        private String protocol;
        private String path;

        public RobotChecker(String url) {
            this.url = url;
            this.host = extractHost();
            this.protocol = extractProtocol();
            this.path = extractPath();
        }

        String extractHost() {
            String host = "";
            try {
                host = new URL(url).getHost();
            } catch (MalformedURLException ignoUrlException) {
            }
            return host;
        }

        String extractProtocol() {
            String protocol = "";
            try {
                protocol = new URL(url).getProtocol();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return protocol;
        }

        String extractPath() {
            String path = "";
            try {
                path = new URL(url).getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (path.equals("")) {
                path = " /";
            } else if (!path.endsWith("/")) {
                path += "/";
            }
            return path;
        }

        boolean hasRobotFile() {
            try {
                URL url = new URL(protocol + "://" + host + "/robots.txt");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        boolean CrawlingAllowed() {
            if (!hasRobotFile()) {
                return true;
            } else {
                try {
                    URL url = new URL(protocol + "://" + host + "/robots.txt");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        boolean GeneralBotauthoritiesFound = false;
                        while ((inputLine = in.readLine()) != null) {
                            if (GeneralBotauthoritiesFound && inputLine.contains("User-agent:")) {
                                break;
                            } else if (GeneralBotauthoritiesFound && inputLine.contains("Disallow: " + path)) {
                                return false;
                            } else if (inputLine.contains("User-agent: *")) {
                                GeneralBotauthoritiesFound = true;
                            }
                        }
                        in.close();
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    /*
     * class SeedManager is responsible for the following:
     * reading the intial Seed Set form the given file
     * normailizing the url
     * add the url to the list of urls to be crawled if the URL is not already in
     * the list
     * maintain the State of the crawler by updating the database
     */

    public static class SeedManager {
        private MongoClient mongoClient;
        private MongoDatabase db;
        private MongoCollection crawlerLinks;
        private MongoCollection crawlerState;
        private MongoCollection crawledLinks;
        private MongoCollection linkUnderCrawl;

        public SeedManager() {
            this.mongoClient = new MongoClient(new MongoClientURI(
                    "mongodb://Sandra:fmCs6CAZx0phSrjs@cluster0-shard-00-00.real4.mongodb.net:27017,cluster0-shard-00-01.real4.mongodb.net:27017,cluster0-shard-00-02.real4.mongodb.net:27017/SearchEngine?ssl=true&replicaSet=atlas-bhl30t-shard-0&authSource=admin&retryWrites=true&w=majority"));
            this.db = mongoClient.getDatabase("SearchEngine");
            this.crawlerLinks = db.getCollection("CrawlerLinks"); // A collection of links to be crawled
            this.crawledLinks = db.getCollection("CrawledLinks"); // A collection of links that have been crawled
            this.crawlerState = db.getCollection("CrawlerState"); // A collection to store the current state of the
                                                                  // crawler
            this.linkUnderCrawl = db.getCollection("LinkUnderCrawl"); // A collection to store the links that are
                                                                      // currently being crawled

            level = getCrawlerState();
            if (level == 0) {
                intializeDBSeedSet();
            }
            fillLinksToBeCrawled();
            fillCrawledLinks();
        }

        /*
         * gets the crawler state from the database
         * if now state document is found then it creates a new document with level 0
         */
        private int getCrawlerState() {
            Iterator iterator = crawlerState.find().iterator();
            if (iterator.hasNext()) {
                Document document = (Document) iterator.next();
                return document.getInteger("level");
            }
            crawlerState.insertOne(new Document("level", 0));
            return 0;
        }

        private void setCrawlerState() {
            crawlerState.updateOne(new Document("level", getCrawlerState()), Updates.set("level", level));
        }

        /*
         * Insert the intial seed set into the CrawlerLinks collection
         */
        private void intializeDBSeedSet() {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            crawlerLinks.insertOne(new Document("url", normalizer
                    .filter("https://www.arduino.cc")));
            crawlerLinks.insertOne(
                    new Document("url", normalizer.filter("https://www.javatpoint.com")));
            crawlerLinks.insertOne(
                    new Document("url", normalizer.filter(
                            "https://circuitdigest.com/microcontroller-projects/arduino-spi-communication-tutorial")));
            crawlerLinks.insertOne(new Document("url", normalizer.filter("https://stackoverflow.com")));
            crawlerLinks.insertOne(new Document("url", normalizer
                    .filter("https://www.zagrosrobotics.com/shop/custom.aspx?recid=69")));
        }

        private boolean isPresent(String url) {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();

            if (url.isEmpty() || CrawledLinksMap.containsKey(normalizer.filter(url))
                    || links.contains(normalizer.filter(url))) {
                return true;
            }
            return false;
        }

        private void addToCrawledLinks(String url) {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            String normalizedUrl = normalizer.filter(url);
            CrawledLinksMap.put(normalizer.filter(url), 1);
            crawledLinks.insertOne(new Document("url", normalizedUrl));
            System.out.println("adding to Crawled links: " + normalizedUrl);
            DeleteResult res = linkUnderCrawl.deleteOne(new Document("url", normalizedUrl));
            System.out.println(res.getDeletedCount());
        }

        /*
         * add the url to the list of urls to be crawled if the URL is not already in
         * the list and it is allow to be crawled
         */
        private void addtToLinksToBeCrawled(String url) {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            String normalizedUrl = normalizer.filter(url);
            if (!url.contains("void") && !isPresent(normalizedUrl) && url.contains("http") && (new RobotChecker(url)).CrawlingAllowed()) {
                links.add(normalizedUrl);
                crawlerLinks.insertOne(new Document("url", normalizedUrl));
            }
        }

        private String getNextLink() {
            String resultString = links.poll();
            if (resultString != null) {
                removeFromLinksToBeCrawled(resultString);
                linkUnderCrawl.insertOne(new Document("url", resultString));
                level++;
                setCrawlerState();
            }
            return resultString;
        }

        private void removeFromLinksToBeCrawled(String url) {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            String normalizedUrl = normalizer.filter(url);
            if (links.contains(normalizedUrl)) {
                links.remove(normalizedUrl);
                System.out.println("removing from links to be crawled: " + normalizedUrl);
                DeleteResult res = crawlerLinks.deleteOne(new Document("url", normalizedUrl));
                System.out.println(res.getDeletedCount());
            }
        }

        /*
         * fills the list of links to be crawled from the database
         */
        private void fillLinksToBeCrawled() {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            Iterator iterator = crawlerLinks.find().iterator();
            while (iterator.hasNext()) {
                Document document = (Document) iterator.next();
                links.add(normalizer.filter(document.getString("url")));
            }
        }

        private void fillCrawledLinks() {
            BasicURLNormalizer normalizer = new BasicURLNormalizer();
            Iterator iterator = crawledLinks.find().iterator();
            while (iterator.hasNext()) {
                Document document = (Document) iterator.next();
                CrawledLinksMap.put(normalizer.filter(document.getString("url")), 1);
            }
        }
    }

        /*
         * function Crawl is responsible for the following:
         * 1. get the next url from the list of links to be crawled
         * 2. crawl the url
         * 3. add the extracted links to the list of links to be crawled
         * 4. add the url to the list of crawled links
         */
    private void crawl(SeedManager seedManager) {
        while (!links.isEmpty()) {
            String url;
            synchronized (seedManager) {
                if (CrawledLinksMap.size()+links.size() > maxLinks)
                    break;
                url = seedManager.getNextLink();
            }
            org.jsoup.nodes.Document doc;
            try {
                doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");
                System.out.println(url);
                for (Element link : links) {
                    if ((new BasicURLNormalizer()).filter(url)
                            .equals((new BasicURLNormalizer()).filter(link.attr("abs:href")))) {
                        continue;
                    }
                    System.out.printf("crawled-- %s\n", link.attr("abs:href"));
                    synchronized (seedManager) {
                        seedManager.addtToLinksToBeCrawled(link.attr("abs:href"));
                    }
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            synchronized (seedManager) {
                seedManager.addToCrawledLinks(url);
            }
        }
    }

        @Override
        public void run() {
            crawl(seedManager);
        }    

    public static void main(String[] args) throws URISyntaxException, IOException {
        int crawlerCount = 5;
        Thread [] crawlers = new Thread[crawlerCount];
        for (int i = 0; i < crawlerCount; i++) {
            crawlers[i] = new Thread(new Crawler());
            crawlers[i].start();
        }
        //join threads
        for (int i = 0; i < crawlerCount; i++) {
            try {
                crawlers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}