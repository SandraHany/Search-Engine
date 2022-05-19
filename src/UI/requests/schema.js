const mongoose = require('mongoose');
//const Int32 = require("mongoose-int32").loadType(mongoose);
const schema = mongoose.Schema;

// const arwaSchema = new schema({
//     word: String
// });

const siteSchema = new schema({
    word: String
});



const Site = mongoose.model('indexer', siteSchema);

module.exports = Site;

