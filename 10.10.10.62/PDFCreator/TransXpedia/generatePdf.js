/* *************************************************************************** */ 
const timestamp  		= require('../Utils/timestamp').getTimeStamp;
const utils				= require('./utils');
/* *************************************************************************** */ 

const mTXNINFO = {
	"HTMLTEMPLATE"   : {"FIXED" : false,   "LENGTH" : 15000, "ISNULL" : false, "MANDATORY"    : true},
	"INPUTJSON" 	 : {"FIXED" : false,   "LENGTH" : 5000, "ISNULL" : false,  "MANDATORY"    : true}
 }

/* *************************************************************************** */ 
module.exports.extractClientMessage = function extractClientMessage (TransXpedia) {
	return new Promise((resolved, rejected) => {
		try {
				TransXpedia.CLIENT_JSON=JSON.parse(TransXpedia.ClientBuffer);

				utils.validateClientFields(TransXpedia, mTXNINFO)
				.then((TransXpedia)=>{
						logmessage('FROM CLIENT', TransXpedia);
					   return resolved(TransXpedia);
				}).catch((TransXpedia) => {
						return rejected(TransXpedia);
				});
		} catch (e) {
			TransXpedia.systemErr      = e;                                                                         		
			TransXpedia.ErrorCode      = 301;
			TransXpedia.errDescription = `UNABLE TO EXTRACT CLIENT DETAILS `;
			return rejected(TransXpedia);
		}
	});
}

module.exports.frameClientMessage = function frameClientMessage (TransXpedia) {
	return new Promise ((resolved, rejected) => {
		try {
			
			TransXpedia.ClientMessage = JSON.stringify({
				"ERRORCODE" 	: TransXpedia.CLIENT_JSON.ERRORCODE,
				"ERRORMSG"		: TransXpedia.errDescription,
			    "PDFDATA"	    : TransXpedia.CLIENT_JSON.PDFDATA, 
			});
			logmessage('TO CLIENT', TransXpedia);
			return resolved(TransXpedia);

		} catch(e) {
			TransXpedia.systemErr      = e;       
			TransXpedia.ErrorCode      = 302;
			TransXpedia.errDescription = `UNABLE TO FRAME MESSAGE TO CLIENT`;
			return rejected(TransXpedia);
		}
	});
}

function logmessage(CONTEXT, TransXpedia) {
   try {
		let Message = {
			'CONTEXT'   : CONTEXT,
         	'UUID'      : TransXpedia.UUID,
			'SERVICE'   : TransXpedia.SERVICE,
			// 'DATA' : {
			// 	'INCOMING' : {
			// 		'HTMLTEMPLATE'   : TransXpedia.CLIENT_JSON.HTMLTEMPLATE,
			// 		'INPUTJSON'      : TransXpedia.CLIENT_JSON.INPUTJSON
			//  	},
			// 	'OUTGOING' : {
			// 		'ERRORCODE' : TransXpedia.CLIENT_JSON.ERRORCODE,
			// 		'ERRORMSG'  : TransXpedia.errDescription,
			// 		'PDFDATA'   : TransXpedia.CLIENT_JSON.PDFDATA,
			//  	}
			//  },
			'TIMESTAMP' : timestamp(),
		}
		// Message.DATA = (Message.CONTEXT == 'FROM CLIENT' ? Message.DATA.INCOMING : Message.DATA.OUTGOING);
		console.log('---------------------------------------------------------');
		console.log(Message);
		console.log('---------------------------------------------------------\n');
	}catch(e) {
		console.log(e);
	}
}
/* *************************************************************************** */ 
