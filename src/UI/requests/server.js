const { MongoClient } = require("mongodb");
const connectDB = require('./connection.js');
const Site = require('./schema.js');
const express = require('express');
const natural = require('natural');
const https = require('https');
const {parser}  = require('html-metadata-parser');
const stemmer = natural.PorterStemmer;
const app = express();
app.use(express.json());

const uri =
  "mongodb+srv://Sandra:fmCs6CAZx0phSrjs@cluster0.real4.mongodb.net/SearchEngine?retryWrites=true&w=majority";

const client = new MongoClient(uri);
const database = client.db("SearchEngine");
const indexer = database.collection("indexers");
const IndexedURLs = database.collection("indexedlinks");





const PORT = 3000;
const cors = require('cors');

var host, port;

var tempLinksList = [];
var linksList = [];

var found_documents = [];
var ranked_URLs;


var titleList = [];
var descriptionList = [];

               

connectDB();

app.use(cors({
    origin: 'http://127.0.0.1:5501'
}));


const server = app.listen(PORT, () => {
    host = server.address().address;
    port = server.address().port; 
    console.log('listening at http://localhost:%s', port);
});


var wordList;

app.get('/sites/:word', async (req, res) => {
    var query = req.params.word.toLowerCase();
    const queryArray = query.split(" ");
    wordList = [];
    loop1:
        for (let i = 0; i < queryArray.length; i++) {
            if(queryArray[i] == ""){
                continue loop1;
            }
            let currentWord = queryArray[i];
            if(!skipStemming(currentWord)){
                currentWord = stemmer.stem(queryArray[i]);
            }
            await Site.find({
                word : currentWord
            }).then(async (result) =>{
                if(result[0] != null){
                    wordList.push(result[0].word);              
                }
                if(i == queryArray.length - 1){
                    if(wordList.length > 0){
                        console.log(...wordList);
                        await callRanker(wordList, tempLinksList);
                        linksList = ranked_URLs;
                        var jsonResponse = {
                            "links": linksList,
                            "header": titleList,
                            "description": descriptionList
                        }; 
                        //console.log(...linksList);
                        //console.log(linksList);
                        console.log(jsonResponse);
                        res.send(jsonResponse);
                    }
                    else{
                        res.send(false);
                    }
                }

            }).catch((err)=>{
                console.log(err);
            });
        }
});


async function callRanker(words, found_documents) {
    try {
        await client.connect();
        // await PhraseSearching();
        await ranker(words, found_documents);
      } catch (e) {
        console.error(e);
      }
}



async function getHtmlFromURL(url, title ,description) {
    var result = await parser(url);
    title.push({title: result.meta.title});
    description.push({description: result.meta.description});
    return (result.meta.title);
    // descriptionList.push(result.meta.description);
    // console.log(result.meta.title);
    // console.log(result.meta.description);
    // console.log(typeof(result.meta.title));
    // console.log(typeof(result.meta.description));
}

async function getHtmlData(){
    var result = await parser('https://www.youtube.com/watch?v=eSzNNYk7nVU');
    console.log(result.meta.title);
    console.log(result.meta.description);
}


function populateJsonResponse(linksList) {
    var title;

    getHtmlData();

    console.log("hi");
    console.log(...titleList);
    console.log("hi");
}


function skipStemming(word){
    var skip = false;
    skip = numberString(word);
    return skip;
}

function numberString(string){
    var number = Number(string);
    if(number){
        return true;
    }
    return false;
}



// ------------------------------ Page ranker function ------------------------------
async function ranker(words, found_documents) {
    const totalDocuments = await IndexedURLs.countDocuments();
  
    // loop over each word, calculate rank and add its document
    for (var i = 0; i < words.length; i++) {
      const name = words[i];
      const query = { word: name };
      const result = await indexer.findOne(query);
  
      // if word found => save its document + calculate rank and score
      if (result) {
        console.log(`${name} -> found`);
        const document_frequency = result.DF;
        const docs = result.Details;
  
        docs.forEach(async (document) => {
          const url = document.URL;
          const normalizedTF = document.NormalisedTF;
          const index = found_documents.findIndex((doc) => doc.URL === url);
          const importance_score =
            0.5 * document.headingFrequency +
            0.3 * document.titleFrequency +
            0.1 * document.normalFrequency;
  
          // document not found before
          if (index === -1) {
            var popularity_score = 1 / totalDocuments;
  
            found_documents.push({
              URL: url,
              IDF: totalDocuments / document_frequency,
              Sum_TF: normalizedTF,
              score: popularity_score,
              importance_score: importance_score,
              countWords: 1,
            });
            // console.log(found_documents);
          } else {
            found_documents[index].Sum_TF =
              found_documents[index].Sum_TF + normalizedTF;
  
            found_documents[index].importance_score =
              found_documents[index].importance_score + importance_score;
  
            found_documents[index].countWords =
              found_documents[index].countWords + 1;
          }
        });
      } else {
        console.log(`${name} -> not found`);
      }
    }
  
    found_documents.forEach(async (doc) => {
      const query_score = { URL: doc.URL };
      const result_score = await IndexedURLs.findOne(query_score);
  
      var popularity_score;
      if (result_score) {
        if (result_score.new_score > 0) {
          popularity_score = result_score.new_score;
        } else {
          popularity_score = result_score.old_score;
        }
      } else {
        popularity_score = 1 / totalDocuments;
      }
  
      doc.score = popularity_score;
    });
  
    found_documents.sort((doc1, doc2) =>
      doc1.IDF *
        doc1.Sum_TF *
        doc1.score *
        doc1.importance_score *
        doc1.countWords >
      doc2.IDF *
        doc2.Sum_TF *
        doc2.score *
        doc2.importance_score *
        doc2.countWords
        ? -1
        : 1
    );
  
    ranked_URLs = found_documents.map((doc) => doc.URL);
  }


// ---------------------------- Phrase Searching Function ---------------------------
async function PhraseSearching() {
    const statement = '"Hello millania Java"';
    const phrase_words = statement.slice(1, -1).split(" ");
    console.log(phrase_words);
    const documents = [];

    await ranker(phrase_words, documents);
    console.log(documents);

    const result_docs = documents.map((doc) => doc.URL);
    result_docs.forEach(async (doc) => {
        const query = { word: name };
        const result = await indexer.findOne(query);
    });
}















// function removeSpecialCharacters(string){
//     var specialCharacters = /[ ]/|/[^a-zA-Z0-9]/g;
//     return string.replace(specialCharacters, "");
// }

// TO BE USED INSTEAD OF NATURAL
// function processWord(word){
//     var newWord = removeConstantAddition(word);
//     if (newWord!=word){
//         return newWord;
//     }
//     return newWord;
// }

// function removeConstantAddition(word){
//     var newWord = "";
//         if(word.endsWith("ing")){
//             newWord += word.slice(0, -3);
//             newWord = removeDoublingUp(newWord);
//         }
//         else if(word.endsWith("ed")){
//             newWord += word.slice(0, -2);
//             newWord = removeDoublingUp(newWord);
//         }
//         else if(word.endsWith("s")){
//             newWord += word.slice(0, -1);
//         }
//         else{
//             newWord += word;
//         }
//     return newWord;
// }

// function removeDoublingUp(word){
//     var newWord = "";
//     var len = word.length;
//     if(len >= 3){
//         if((word[len-3] == "a" || word[len-3] == "e" || word[len-3] == "i" || word[len-3] == "o" || word[len-3] == "u") 
//             && (word[len-2] != "a" || word[len-2] != "e" || word[len-2] != "i" || word[len-2] != "o" || word[len-2] != "u")
//             && (word[word.length-2] === word[word.length-1]) ){
//             newWord += word.slice(0, -1);
//         }
//         else
//           newWord+= word;
//     }
//     else{
//         newWord += word;
//     }
//     return newWord;
// }