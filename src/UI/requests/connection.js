const mongoose = require('mongoose');

const connectionString = "mongodb+srv://Sandra:fmCs6CAZx0phSrjs@cluster0.l6yha.mongodb.net/SearchEngine?retryWrites=true&w=majority";


const connectDB = async() => {
    await mongoose.connect(connectionString, 
                        {useUnifiedTopology: true, 
                        useNewUrlParser: true}).then((result) =>{    
                            console.log('MongoDB Connected yay...');
                        })
}

module.exports = connectDB;