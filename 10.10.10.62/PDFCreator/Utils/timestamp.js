module.exports.getTimeStamp = function getTimeStamp (time) {
	time = time || Date.now();
	var date = new Date(parseInt(time));
	var time 	= 	date.toLocaleTimeString();
	var year 	= 	date.getFullYear();
	var month = 	date.getMonth()+1; //Note: 0=January, 1=February etc.
	var day 	= 	date.getDate();

	var hour = date.getHours();
	var minute = date.getMinutes();
	var seconds = date.getSeconds();

	month = '00'+month;
	month = month.slice(-2);
	day = '00'+day;
	day = day.slice(-2);

	hour = '00'+hour; hour = hour.slice(-2);
	minute = '00'+minute; minute = minute.slice(-2);
	seconds = '00'+seconds; seconds = seconds.slice(-2);
	var millisec = date.getMilliseconds();
	millisec = '000'+millisec; millisec = millisec.slice(-3);
	
	var timeStamp = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds+"."+millisec;
	return timeStamp;
}
module.exports.getTimeStamp_yymmdd = function getTimeStamp_yymmdd () {
	var date = new Date();
	var year 	= 	date.getFullYear();
	var month = 	date.getMonth()+1; //Note: 0=January, 1=February etc.
	var day 	= 	date.getDate();


	month = '00'+month;
	month = month.slice(-2);
	day = '00'+day;
	day = day.slice(-2);

	
	var timeStamp = year.toString().slice(-2)+ month + day;
	return timeStamp;
}

module.exports.reversalgetTimeStamp = function getTimeStamp (time) {
	time = time || Date.now();
	var date = new Date(parseInt(time));
	var time 	= 	date.toLocaleTimeString();
	var year 	= 	date.getFullYear();
	var month = 	date.getMonth()+1; //Note: 0=January, 1=February etc.
	var day 	= 	date.getDate();

	var hour = date.getHours();
	var minute = date.getMinutes();
	var seconds = date.getSeconds();

	month = '00'+month;
	month = month.slice(-2);
	day = '00'+day;
	day = day.slice(-2);

	hour = '00'+hour; hour = hour.slice(-2);
	minute = '00'+minute; minute = minute.slice(-2);
	seconds = '00'+seconds; seconds = seconds.slice(-2);
	var millisec = date.getMilliseconds();
	millisec = '000'+millisec; millisec = millisec.slice(-3);
	
	var timeStamp = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds+"."+millisec;
	return timeStamp;
}
module.exports.FormatTimestamp = function FormatTimestamp (UnformatedTimeStamp) {
	var today = new Date();
	var year  = UnformatedTimeStamp.slice(0,4);
	var month = UnformatedTimeStamp.slice(4,6);
	var day 	= UnformatedTimeStamp.slice(6,8);
	var Hour  = UnformatedTimeStamp.slice(8,10);
	var min 	= UnformatedTimeStamp.slice(10,12);
	var sec 	= UnformatedTimeStamp.slice(12,14);

	var formatedTimeStamp = year+"-"+month+"-"+day+" "+Hour+":"+min+":"+sec;
	return formatedTimeStamp;
}

module.exports.deFormatTime = function (FormatTimestamp) {
	var year = FormatTimestamp.slice(0,4);
	var month = FormatTimestamp.slice(5,7);
	var day = FormatTimestamp.slice(8,10);

	var hour = FormatTimestamp.slice(11,13);
	var minute = FormatTimestamp.slice(14,16);
	var sec = FormatTimestamp.slice(17,19);

	return year+month+day+hour+minute+sec;

}

module.exports.getTimeStamp_init = function getTimeStamp (time) {
	var date = new Date(time);
	var time 	= 	date.toLocaleTimeString();
	var year 	= 	date.getFullYear();
	var month = 	date.getMonth()+1; //Note: 0=January, 1=February etc.
	var day 	= 	date.getDate();
	var millisec = date.getMilliseconds();

	var hour = date.getHours();
	var minute = date.getMinutes();
	var seconds = date.getSeconds();

	month = '00'+month;
	month = month.slice(-2);
	day = '00'+day;
	day = day.slice(-2);

	hour = '00'+hour; hour = hour.slice(-2);
	minute = '00'+minute; minute = minute.slice(-2);
	seconds = '00'+seconds; seconds = seconds.slice(-2);
	millisec = '000'+millisec; millisec = millisec.slice(-3);


	var timeStamp = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds+ '.' + millisec;
	return timeStamp;
}
