/**************************************************************************** */
function validateClientFields(TransXpedia,mTXNFIELD_INFO) {
	return new Promise ((resolved, rejected) => {
		let MissingKey = [];
		let KeyLengthIssue= [];
      let NullIssue = [];

		for (key in mTXNFIELD_INFO) {
			if (mTXNFIELD_INFO[key].MANDATORY == true) {
				if ((TransXpedia.CLIENT_JSON.hasOwnProperty(key) == false) || 
						(undefined == TransXpedia.CLIENT_JSON[`${key}`]))  {
					MissingKey.push(key);
				}
				else {
					if(mTXNFIELD_INFO[key].FIXED == true) {
						if(mTXNFIELD_INFO[key].LENGTH != TransXpedia.CLIENT_JSON[`${key}`].length){
							KeyLengthIssue.push(key);
						}
					}
					else {
						if(mTXNFIELD_INFO[key].ISNULL == false) {
							if(TransXpedia.CLIENT_JSON[`${key}`].length == 0)
									NullIssue.push(key);
							}
					}
				}
			}
		}
		if (MissingKey.length != 0) {
			TransXpedia.systemErr      = null;
			TransXpedia.ErrorCode      = 303;
			TransXpedia.errDescription = `KEY/KEYS NOT FOUND IN CLIENT REQUEST`;
			return rejected(TransXpedia);
		} else if(KeyLengthIssue.length != 0){
			TransXpedia.systemErr      = null;
			TransXpedia.ErrorCode      = 305;
			TransXpedia.errDescription = `FIELD/FIELDS DATA LENGTHS NOT PROPER IN CLIENT REQUEST`;
			return rejected(TransXpedia);
		} else if(NullIssue.length != 0){
			TransXpedia.systemErr      = null;
			TransXpedia.ErrorCode      = 305;
			TransXpedia.errDescription = `FIELD/FIELDS DATA NULL IN CLIENT REQUEST`;
			return rejected(TransXpedia);
		} else { return resolved(TransXpedia); }
	});
}

/* *************************************************************************** */ 
module.exports={
	validateClientFields    : validateClientFields,
}
/* *************************************************************************** */ 
