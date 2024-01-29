/* *************************************************************************** */ 
const timeStamp = require('../Utils/timestamp').getTimeStamp;
/* *************************************************************************** */ 

/* *************************************************************************** */ 
module.exports = function serverErrorMessage (TransXpedia) {
	return new Promise ((resolved, rejected) => {
		try {

			TransXpedia.PLAIN_BUFFER = JSON.stringify({
				"ERRORCODE" 	: TransXpedia.ErrorCode ? TransXpedia.ErrorCode : 'NA',
				"ERRORMSG"		: TransXpedia.errDescription || "Internal Server Error",
			});
			TransXpedia.ClientMessage = TransXpedia.PLAIN_BUFFER;
		  console.log('---------------------------------------------------------');
		  console.log ({
				SERVICE        : TransXpedia.SERVICE,
            UUID           : TransXpedia.UUID,
				ERRORCODE      : TransXpedia.ErrorCode ? TransXpedia.ErrorCode : 'NA',
				ERRORMSG 	   : TransXpedia.errDescription ? TransXpedia.errDescription : 'NA',
            ERRORSTACK     : TransXpedia.systemErr,
				CLIENTREQUEST 	: TransXpedia.CLIENT_JSON ? TransXpedia.CLIENT_JSON : 'NA',
				TIMESTAMP      : timeStamp(),
			});
		   console.log('---------------------------------------------------------\n');
			return resolved(TransXpedia);
		} catch(e) {
		  console.log('---------------------------------------------------------');
		  console.log ({
				SERVICE        : TransXpedia.SERVICE,
            UUID           : TransXpedia.UUID,
				ERRORMSG 		: "UNBLE TO FRAME RESPONCE TO CLIENT",
				CLIENTREQUEST 	: TransXpedia.CLIENT_JSON ? TransXpedia.CLIENT_JSON : 'NA',
            ERRORSTACK     : TransXpedia.systemErr,
				TIMESTAMP      : timeStamp(),
			});
		  console.log('---------------------------------------------------------\n');
		  return rejected(TransXpedia);
		}
	});
};

/* *************************************************************************** */ 
