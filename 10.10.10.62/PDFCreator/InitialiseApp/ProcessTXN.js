/* *************************************************************************** */ 
const httpResponse  	= require ('../Utils/httpResponse');
const webServices 	= require ('../WebServices/webServices');
/* *************************************************************************** */ 

/* *************************************************************************** */
const WEBSERVICES = {
	'/HTML2PDF/integra/generatePdf' : webServices.generatePdf,
};
/* *************************************************************************** */

/* *************************************************************************** */ 
module.exports = function (_Clientreq, _Clientresp, Buffer) {
	let webServiceFunction = undefined;
	switch (_Clientreq.method)
	{
		case 'POST':
			webServiceFunction = WEBSERVICES[_Clientreq.url];
			if (undefined == webServiceFunction) {
				httpResponse (_Clientresp, 404, {Error : `Web Service Not Found : ${_Clientreq.url}`});
			} else {
				webServiceFunction(_Clientreq, _Clientresp, Buffer);
			}
			break;
		case 'GET':                                                                                               
			httpResponse (_Clientresp, 404, {Error : `Web Service Not Found : ${_Clientreq.url}`});            			break;                                                                                                 
		default:
			httpResponse (_Clientresp, 405, {Error : `HTTP Method Not Supported ${_Clientreq.method}`});
	}
}
/* *************************************************************************** */ 
