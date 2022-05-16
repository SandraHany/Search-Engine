const mongoose = require('mongoose');
const schema = mongoose.Schema;

const siteSchema = new schema({
    url: String
});

const Site = mongoose.model('arwa', siteSchema);

module.exports = Site;

