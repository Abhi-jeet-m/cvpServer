/* ********************************************************************************** */
const fs = require('fs');
/* ********************************************************************************** */

/* ********************************************************************************** */
let CONFIG_FILE = './InitialiseApp/SQConfig.json';
let appConfigurations = {};
/* ********************************************************************************** */

function initServer() {
	return new Promise((resolved, rejected) => {

		try {
			 if (process.argv.length != 4) throw 'USAGE: node <startFile.js> <IP> <PORT>';
		}catch(e) {
			return rejected(e);
		}

		readAppConfigurations()
		.then(() => {
				return resolved();
		}).catch((e) => {
			return rejected(e);
		});
	});

}

function readAppConfigurations() {
 return new Promise((resolved, rejected)=>{
	fs.readFile(CONFIG_FILE, (e,content) =>{
		if(e) {
			return rejected(e);
		}
		else {
			appConfig = JSON.parse(content);
			appConfigurations.LISTENING_IP          = process.argv[2];
			appConfigurations.LISTENING_PORT        = process.argv[3];
			appConfigurations.ServerCommType       = appConfig.ServerConfig.ServerCommType;
         	appConfigurations.ServerTimedoutTime   = appConfig.ServerConfig.ServerTimedoutTime;
         	appConfigurations.ServerMaxConnAllowed = appConfig.ServerConfig.ServerMaxConnAllowed;
			return resolved(appConfig);
		}
	});
 });
}

module.exports.appConfigurations = appConfigurations;
module.exports.initServer = initServer;
		
