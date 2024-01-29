/* ****************************************************************** */
const initServer = require('./InitialiseApp/ServerInit').initServer;
/* ****************************************************************** */

/* ****************************************************************** */
initServer()
.then(()=> {
	const appConfiguration  = require('./InitialiseApp/ServerInit.js').appConfigurations;
	require(`./InitialiseApp/ServerCommType/${appConfiguration.ServerCommType.toUpperCase()}`);
}).catch((e)=> {
		console.log('############## INITIALIASATION ERROR ################');
		console.log(e);
});
/* ****************************************************************** */
	


