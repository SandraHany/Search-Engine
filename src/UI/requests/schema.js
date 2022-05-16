const mongoose = require('mongoose');
const schema = mongoose.Schema;

const siteSchema = new schema({
    word: String
});

const Site = mongoose.model('arwa', siteSchema);

module.exports = Site;

