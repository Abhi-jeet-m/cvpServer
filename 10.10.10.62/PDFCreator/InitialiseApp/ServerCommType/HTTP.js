/* *************************************************************************** */ 
const clientRequestHandler = require ('../clientRequestHandler');
const appConfig 				= require('../ServerInit').appConfigurations;
const httpServer    			= require('http').createServer();
const timeStamp 				= require('../../Utils/timestamp').getTimeStamp;
/* *************************************************************************** */ 

/* *************************************************************************** */ 
const SECONDS = 1000;
httpServer.maxConnections 	= appConfig.ServerMaxConnAllowed;
httpServer.timeout 			= appConfig.ServerTimedoutTime*SECONDS;
/* *************************************************************************** */ 


/* ******************** REGISTERING LISTENER **************** */
httpServer.on ('error', (e) => {
	console.log ({
		ERROR 		: e,
		TIMESTAMP 	: timeStamp(),
	});
})

httpServer.on ('request', clientRequestHandler);

httpServer.on ('listening', () => {
  console.log ('Server Is Listening On IP : %s And PORT : %d :-)', appConfig.LISTENING_IP, 
  appConfig.LISTENING_PORT);
});


httpServer.on ('connection', () => {
});

httpServer.on ('timeout', (clientSocket) => {
	console.log ({
		ERROR 		: "Timedout Occured Between Client And Server",
		TIMESTAMP 	: timeStamp(),
	});
	clientSocket.destroy();
});

httpServer.on ('clientError', (err, socket) => {
	console.log ({
		ClientError : err,
		TIMESTAMP 	: timeStamp(),
	});
  socket.end ('HTTP/1.1 400 Bad Request\r\n\r\n');
});

process.on('SIGINT', () => {                                                      
	console.log('Received Signal To Shutdown Server...Wait For Sometime.... [%s]', timeStamp());   
	httpServer.close((e) => {                                                     
		if (e) {
			console.log(e);
			process.exit(0);
		}
/*				database.shutdown()
				.then(()=> {
					console.log('SERVER SHUTDOWN SUCCESSFULL....:-) [%s]', timeStamp());
					process.exit(0);
				}).catch((e) => {
					console.log(e);
					process.exit(0);
				}); */
		});
	});                                                                          
/* ********************************************************** */

/* ***************************************************************************** */
httpServer.listen (appConfig.LISTENING_PORT, appConfig.LISTENING_IP, 300);
/* ***************************************************************************** */
