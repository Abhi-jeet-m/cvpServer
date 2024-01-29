/* ************************************************************************** */
const txnModule    		 = require('../TransXpedia/txnModule');
const httpResponse 		 = require('../Utils/httpResponse');
const serverErrorMessage = require('../Utils/serverErrorMessage');
const utils              = require('../Utils/utils');
const pdfModule          = require('../Utils/PDFCreator/pdfCreator');
/* ************************************************************************** */

/* ************************************************************************** */
module.exports = function (_Clientreq, _Clientresp, ClientBuffer) {
	const TransXpedia 			  = {};
   	TransXpedia.SERVICE          = 'GENERATEPDF';
	TransXpedia.UUID             = utils.generateUUID();
	TransXpedia.ClientBuffer 	  = ClientBuffer;

	txnModule.generatePdf.extractClientMessage(TransXpedia)
	.then(pdfModule.generatePdf)
	.then(txnModule.generatePdf.frameClientMessage)
	.then((TransXpedia) => {
			httpResponse(_Clientresp, 200, TransXpedia.ClientMessage);
			return TransXpedia;
	})
	.catch((TransXpedia) => {
		try {
		serverErrorMessage(TransXpedia)
		.then((TransXpedia) => {
			httpResponse(_Clientresp, TransXpedia.ErrorCode, TransXpedia.ClientMessage);
			return TransXpedia;
		})
		.catch((e) => {
			httpResponse(_Clientresp, 500, {
				ERRORCODE : TransXpedia.ErrCode,
				ERRORMSG  : TransXpedia.errDescription,
			});
			console.log(e)
		});
		} catch(e) {
			console.log(e);
		}
	});
}
/* ************************************************************************** */
