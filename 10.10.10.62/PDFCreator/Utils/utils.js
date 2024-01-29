const uuid4 = require('../node_modules/uuid/v4');

module.exports.generateUUID = function generateUUID()
{
	return uuid4();
}
