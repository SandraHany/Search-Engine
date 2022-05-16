const connectDB = require('./connection.js');
const Site = require('./schema.js');
const express = require('express');
const app = express();
app.use(express.json());

var host, port;

connectDB();
app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.get('/sites', (req, res) => {
    Site.find({}).then((result) =>{
    res.send(result);
    console.log(typeof(result));
    console.log(result[0].url);
  }).catch((err)=>{
    console.log(err);
  });
});



app.get('/app', (req, res) => {
    res.send(JSON.stringify([1,2,3,4,5,6,7,8,9,10,12,13]));
    console.log(typeof(JSON.stringify([1,2,3,4,5,6,7,8,9,10,12,13])));
});


  

const server = app.listen(3000, () => {
    host = server.address().address;
    port = server.address().port; 
    console.log('listening at http://localhost:%s', port);
});