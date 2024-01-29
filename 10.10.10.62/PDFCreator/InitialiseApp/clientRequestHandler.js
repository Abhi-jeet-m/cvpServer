/* *************************************************************************** */
const ProcessTXN 			= require ('./ProcessTXN');
const httpResponse 		= require('../Utils/httpResponse');
//const serverVariables 	= require('./serverStatus').serverVariables;
/* *************************************************************************** */

/* *************************************************************************** */
const KB = 1024;
/* *************************************************************************** */

/* *************************************************************************** */
module.exports = function clientRequestHandler (_ClientRequest_, _ClientResponse_) {

	var chunk ='';
	_ClientRequest_.on ('data', (ClientData) => {readingClientData(ClientData)}).on ('end', () => {
		ProcessTXN (_ClientRequest_, _ClientResponse_, chunk);
	}).on ('error', (e) => {
		console.log (`ERROR ON READING STREAM [FUNCTION : clientRequestHandler] ERROR : ${e}`);
		_ClientRequest_.destroy ();
	});

	_ClientResponse_.on ('finish', () => {
		/* * console.log ('FINISHED SENDING DATA [FUNCTION : %s]', clientRequestHandler.name); */
	}).on ('close', () => {
		/* * console.log ('CONNETION CLOSED [FUNCTION : %s]', clientRequestHandler.name); */
	})

	function readingClientData (ClientData) {
		chunk += ClientData;
			return;                                                           
	}
}
/* *************************************************************************** */
