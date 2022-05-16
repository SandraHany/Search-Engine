const connectDB = require('./connection.js');
const Site = require('./schema.js');
const express = require('express');
const natural = require('natural');
const stemmer = natural.LancasterStemmer;
const app = express();
app.use(express.json());

const PORT = 3000;
const cors = require('cors');

var host, port;


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

app.get('/sites/:word', (req, res) => {
    var query = req.params.word.toLowerCase();
    const queryArray = query.split(" ");
    wordList = [];
    loop1:
        for (let i = 0; i < queryArray.length; i++) {
            Site.find({
                word : stemmer.stem(queryArray[i])
            }).then((result) =>{
                if(result[0] != null){
                    wordList.push(result[0].word);              
                }
                if(i == queryArray.length - 1){
                    res.send(result);
                    console.log(...wordList);
                }

            }).catch((err)=>{
                console.log(err);
            });
        }
});



app.get('/app', (req, res) => {
    res.send(JSON.stringify([1,2,3,4,5,6,7,8,9,10,12,13]));
    console.log(typeof(JSON.stringify([1,2,3,4,5,6,7,8,9,10,12,13])));
});










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


