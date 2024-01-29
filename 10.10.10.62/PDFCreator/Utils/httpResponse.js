/* ************************************************************************** */
const timeStamp = require('./timestamp').getTimeStamp;
/* ************************************************************************** */

module.exports = function httpResponse (response, statusCode, Message) {
	try {
		statusCode = statusCode || 200;
		response.writeHead(statusCode, {
			'Content-Type': 'text/JSON;charset=utf-8',
			'Connection'  : 'Close',
			'X-Frame-Options':'deny',
			'X-Content-Type-Options':'nosniff'
		});
		if (typeof Message == 'object') {
			Message = JSON.stringify(Message);
		}

		response.write (Message,(e)=>{
			if(e){
				console.log ({
					SERVER_ERROR 	: e,
					TIMESTAMP 		: timeStamp(),
				});
			}
			response.end();
		});
	/*	response.write(Message);
		response.end();*/
	} catch(e) {
		console.log ({
			SERVER_ERROR 	: e,
			TIMESTAMP 		: timeStamp(),
		});
		response.writeHead(500, {
			'Connection'  : 'Close',
			'Content-Type': 'text/JSON;charset=utf-8',
			'X-Frame-Options':'deny',
			'X-Content-Type-Options':'nosniff'
		});
		response.write (JSON.stringify({
			"ERRORCODE": '500',
			"ERRORMSG" : "UNABLE TO SEND MESSAGE TO CLIENT",
		}),(e)=>{
			if(e){
				console.log ({
					SERVER_ERROR 	: e,
					TIMESTAMP 		: timeStamp(),
				});
			}
			response.end();
		});
	}
}
