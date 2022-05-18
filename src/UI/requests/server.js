const connectDB = require('./connection.js');
const Site = require('./schema.js');
const express = require('express');
const natural = require('natural');
const https = require('https');
const stemmer = natural.PorterStemmer;
const app = express();
app.use(express.json());

const PORT = 3000;
const cors = require('cors');

var host, port;

var linksList = ["https://www.google.com"
                ,"https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"
                ,"https://www.computerhope.com/jargon/u/url.htm"
                ,"https://stackoverflow.com/questions/21293456/scroll-horizontally-starting-from-right-to-left-with-css-overflowscroll"
                ,"https://www.reddit.com"
                ,"https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/Creating_hyperlinks"
                ,"https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"
                ,"https://css-tricks.com/scroll-fix-content/"
                ,"https://www.computerhope.com/jargon/u/url.htm"
                ,"https://www.producthunt.com/posts/copy-all-urls"];

var headerList = [];
var descriptionList = [];

var jsonResponse = {
    "links": linksList,
    "header": headerList,
    "description": descriptionList
};                

connectDB();

app.use(cors({
    origin: 'http://127.0.0.1:5501'
}));


const server = app.listen(PORT, () => {
    host = server.address().address;
    port = server.address().port; 
    console.log('listening at http://localhost:%s', port);
});


app.get('/', (req, res) => {
  res.send('Hello World!');
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
            }).then((result) =>{
                if(result[0] != null){
                    wordList.push(result[0].word);              
                }
                if(i == queryArray.length - 1){
                    if(wordList.length > 0){
                        populateJsonResponse(linksList);
                        res.send(jsonResponse);
                    }
                    else{
                        res.send(false);
                    }
                    console.log(...wordList);
                }

            }).catch((err)=>{
                console.log(err);
            });
        }
});


function getHtmlFromURL(url) {
    return new Promise(function(resolve, reject) {
        https.get(url, function(res) {
            var html = '';
            res.on('data', function(data) {
                html = document.getElementById("h1");
            });
            res.on('end', function() {
                resolve(html);
            });
        }).on('error', function(e) {
            reject(e);
        });
    });
}

function getHeaderFromURL(url){

    
}


async function populateJsonResponse(linksList) {
    for (let i = 0; i<linksList.length; i++ ){
        var html = await getHtmlFromURL(linksList[i]);
        console.log(html);

    }
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


