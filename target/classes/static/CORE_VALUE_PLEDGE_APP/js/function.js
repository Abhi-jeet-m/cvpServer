//------------------------------------------------------------

//HOME PAGE REDIRECTS


$(function() {
	$('#employee').on("click", function(e) {
		window.location = "/CORE_VALUE_PLEDGE_APP/EMPLOYEE";
	});
});
$(function() {
	$('#hrd').on("click", function(e) {
		window.location = "/CORE_VALUE_PLEDGE_APP/HRD";
	});
});
// -------------------------------------------------------
// EMP LOGIN PAGE
// VALIDATION

function onlyNumbers(event) {
	var charCode = (event.which) ? event.which : event.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57))
		return false;

	return true;
}

$.generateOTP=function(e) {
	
	var empid = document.getElementById("empid").value;
	if (empid != "") {
		var jsonobj = {};
		jsonobj["usertype"] = "emp";
		jsonobj["empid"] = document.getElementById('empid').value;
		jsonobj["password"] = "";
		jsonobj["username"] = "";

		var jsonstring = JSON.stringify(jsonobj);
		var vallink = "/CORE_VALUE_PLEDGE_APP/validatelogin";
		$.ajax({
			type : 'POST',
			data : jsonstring,
			async : false,
			contentType : "application/json",
			url : vallink,
			success : function(resp) {
				var statusreport = JSON.parse(resp);
				if (statusreport.statuscode == 00) {
					$('#empid').prop('readonly', true);
					$("#otp").show();
					$("#otpsubmit").show();
					$("#empidsubmit").remove();
					$("#1").remove();
					$("#2").remove();
					$("#3").remove();
					$("#4").remove();
				} else {
					alert(statusreport.statusmsg);
				}
			}
		});
	} else {
		alert("Enter all the fields");
	}
}

$(function() {
	$('#empidsubmit').on("click",function(e) {
				$.generateOTP();
			});
});

function generateOtp(event) {
	  if( event.which == 13 || event.keyCode == 13)
		  {
		  $.generateOTP();
		  }
	}

$.otpSubmit=function(e) {
	var empid = document.getElementById("empid").value;
	var otp = document.getElementById('otp').value;
	if (empid != "" && otp != "") {
		var jsonobj = {};
		jsonobj["empid"] = empid;
		jsonobj["otp"] = otp;
		var jsonstring = JSON.stringify(jsonobj);
		var vallink = "/CORE_VALUE_PLEDGE_APP/validateotp";
		$.ajax({
			type : 'POST',
			data : jsonstring,
			async : false,
			contentType : "application/json",
			url : vallink,
			success : function(resp) {
				var statusreport = JSON.parse(resp);
				if (statusreport.statuscode == 00) {
					window.location = "/CORE_VALUE_PLEDGE_APP/PLEDGEINFO/?"
							+ btoa("empid=" + statusreport.empid
									+ "name=" + statusreport.name
									+ "company="
									+ statusreport.company);
				} else {
					alert(statusreport.statusmsg);
				}
			}
		});
	} else {
		alert("Enter all the fields");
	}
}

$(function() {
	$('#otpsubmit').on("click",function(e) {
				 $.otpSubmit();
			});
});

function myFunction(event) {
	  if( event.which == 13 || event.keyCode == 13)
		  {
		  $.otpSubmit();
		  }
	}
// PLEDGEINFO PAGE
// APPENDING NAME EMPID AND COMPANY
var empid;
$(function() {
	$('#pledgeinfo').ready(
			function(e) {
				var todecode = location.search;
				var decode = todecode.split("?")[1];
				var url = atob(decode);
				empid = url.split("empid=")[1].replace("name="
						+ url.split("name=")[1], '');
				var empname = url.split("name=")[1].replace("company="
						+ url.split("company=")[1], '');
				var empcompany = url.split("company=")[1];
				// alert(empid+empname+empcompany);
				var empcompanyfullname;
				if(empcompany == "i25RMCS"){
					empcompanyfullname = "i25 Rural Mobile Commerce Services (i25RMCS)";
				}else if(empcompany == "IMSPL"){
					empcompanyfullname = "Integra Micro Systems (P) Ltd. (IMSPL)";
				}else if(empcompany == "IMSS"){
					empcompanyfullname = "Integra Micro Software Services (P) Ltd. (IMSS)";
				}else if(empcompany == "BOT AI ML"){
					empcompanyfullname = "BOT AI ML Private Limited (BOT AI ML)";
				}else if(empcompany == "JTPL"){
					empcompanyfullname = "Jakkur Technoparks Private Limited (JTPL)";
				}else if(empcompany == "IDPL"){
					empcompanyfullname = "Integra Datatech Private Limited (IDPL)";
				}
				$("#empName").html(empname);
				$("#empCompanyFullName").html(empcompanyfullname);
				$("[name='empCompany']").html(empcompany);
				$("#empId").html(empid);
			});
});
// FOR ESIGN REDIRECT TO INSTRUCTIONS PAGE
// WITH AADHAR
$(function() {
	$("#withesign").on("click", function() {
		var x = $("[name=terms1]");
		var count = x.length;
		for (i in x) {
			if (x[i].checked == true) {
				if (count > 0) {
					count = count - 1;
				}
			}
		}
		if (count != 0) {
			alert("Please agree to all Core Values to proceed !!");
		} else {
			window.location = "/CORE_VALUE_PLEDGE_APP/INSTRUCTIONS?" + btoa("empid=" + empid + "");
		}

	});
});
// WITHOUT AADHAR
$(function() {
	$('#withoutaadhar').on("click", function(e) {
		// alert("Inside withoutaadhar btn");
		window.location = "/CORE_VALUE_PLEDGE_APP/TERMS/?" + btoa("empid=" + empid + "");
	});
});
// TO SHOW UPLOAD BUTTTON
$(function() {
	$('#offlinecopy').on("click", function(e) {
		// alert("Inside offlinecopy btn");
		$("#fileupload").show();
	});
});
// UPLOADING THE FILE
$(function() {
	$('#uploadpledge').on(
			"click",
			function(e) {
				var uploadlink = "/CORE_VALUE_PLEDGE_APP/uploadSignedPledge";
				var jsonobj = {};
				jsonobj["empid"] = empid;
				var jsonstring = JSON.stringify(jsonobj);
				var form1 = $('#fileupload')[0];
				document.getElementById('inputs').value = jsonstring;
				var data1 = new FormData(form1);
				$
						.ajax({
							type : "POST",
							enctype : 'multipart/form-data',
							contentType : false,
							processData : false,
							cache : false,
							data : data1,
							url : uploadlink,
							success : function(resp) {
								var statusreport = JSON.parse(resp);
								if (statusreport.statuscode == 00) {
									alert(statusreport.statusmsg);
								} else {
									alert(statusreport.statusmsg);
								}
							},
							error : function(ts) {
								document.write(ts.responseText);
							}
						});
				e.preventDefault();

			});
});
// ------------------------------------------------------------------------------
// SIGN IN PAGE
// SIGN IN PAGE REDIRECTS

$(function() {
	$('#signinpage').ready(function(e) {
		var todecode = location.search;
		var decode = todecode.split("?")[1];
		var name = atob(decode);
		var name1 = name.split("name=")[1];
		document.getElementById("dspyempdet").innerHTML = "Welcome " + name1;
	});
});
$(function() {
	$('#onsignin').on(
			"click",
			function(e) {
				var todecode = location.search;
				var decode = todecode.split("?")[1];
				var empid = atob(decode);
				var empid1 = empid.split("empid=")[1].replace("name="
						+ empid.split("name=")[1], '');
				window.location = "/CORE_VALUE_PLEDGE_APP/TERMS/?" + btoa("empid=" + empid1 + "");
			});
});
$(function() {
	$('#ondownload').on("click", function(e) {
		window.location = "/CORE_VALUE_PLEDGE_APP/DOWNLOAD";
	});
});
// -------------------------------------------------------------------------------
// FUNCTION FOR TERMS & CONDITION PAGE
// DISABLE SUBMIT AND LOAD TERMS $ CONDITION FROM SERVER

$(function() {
	$('#offlinepledgedoc').ready(
			function(e) {
				
				var todecode = location.search;
				var decode = todecode.split("?")[1];
				var empid1 = atob(decode);
				var empid2 = empid1.split("empid=")[1];
				// alert(empid2);
				var getpledgedoc = "/CORE_VALUE_PLEDGE_APP/downloadPledgeInfo/"
						+ empid2 + "";
				var Source = document.getElementById('pledgeinfodoc');
				var Clone = Source.cloneNode(true);
				Clone.setAttribute('src', getpledgedoc);
				Source.parentNode.replaceChild(Clone, Source);
			});
});
// ENABLE SUBMIT ON Checkbox CHECK
function activateButton(element) {
	if (element.checked) {
		document.getElementById("toinstructions").disabled = false;
	} else {
		document.getElementById("toinstructions").disabled = true;
	}
}
// TO DOWNLOAD UNSIGNED PLEDGE
$(function() {
	$('#downloaddoc').on("click", function(e) {
		// alert("download pledge");
		var dwnpaglink = "/CORE_VALUE_PLEDGE_APP/downloadPledgeInfo";
		var form = $('#downloadpledgeinfo')[0];
		form.action = dwnpaglink;
		form.submit();
	});
});

// REDIRECT TO INSTRUCTIONS PAGE
$(function() {
	$('#toinstructions').on("click", function(e) {
		var todecode = location.search;
		var decode = todecode.split("?")[1];
		var empid1 = atob(decode);
		var empid2 = empid1.split("empid=")[1];
		window.location = "/CORE_VALUE_PLEDGE_APP/INSTRUCTIONS?" + btoa("empid=" + empid2 + "");
	});
});
// ------------------------------------------------------------------------------------------------------------------------
// DOWNLOAD PAGE
// TO DOWNLOAD SIGNED PLEDGE

$(function() {
	$('#downloadbtn')
			.on(
					"click",
					function(e) {
						var refno = document.getElementById("refnumber").value;
						if (refno != "") {
							var dwnpaglink = "/CORE_VALUE_PLEDGE_APP/downloadSignedPledge/emp-"
									+ refno + "";
							var form = $('#downloadpledge')[0];
							form.action = dwnpaglink;
							form.submit();
							e.preventDefault();
						} else {
							alert("Enter Ref No.....");
						}
					});
});
// -----------------------------------
// TO EMAIL PLEDGE DOCUMENT
$(function() {
	$('#emailcall').on(
			"click",
			function(e) {
				var refno = document.getElementById("refnumber").value;
				if (refno != "") {
					var jsonobj = {};
					jsonobj["usertype"] = "emp";
					jsonobj["value"] = refno;
					var jsonstring = JSON.stringify(jsonobj);
					var emaillink = "/CORE_VALUE_PLEDGE_APP/emailSignedPledge";
					$.ajax({
						type : 'POST',
						data : jsonstring,
						async : false,
						contentType : "application/json",
						url : emaillink,
						success : function(resp) {
							var statusreport = JSON.parse(resp);
							// alert(statusreport.statuscode);
							if (statusreport.statuscode == 00) {
								alert("Successfully Emailed");
							} else {
								alert(statusreport.statusmsg);
							}
						}
					});
					return false;
				} else {
					alert("Enter Ref No.....");
				}
			});
});
// -------------------------------------------
// RESPONSE PAGE
$(function() {
	$('#todwnpge').on("click", function(e) {
		window.location = "/CORE_VALUE_PLEDGE_APP/DOWNLOAD";
	});
});

// ---------------------------------------------------------------------------------------------------------
// HDR PAGE

// HR LOGIN VALIDATION FUNCTION
$.hrLogin=function(e) {
	var username = document.getElementById("username").value;
	var hrpassword = document.getElementById("hrpassword").value;
	if (username != "" && hrpassword != "") {
		var jsonobj = {};
		jsonobj["usertype"] = "hr";
		jsonobj["empid"] = "";
		jsonobj["empdob"] = "";
		jsonobj["empphone"] = "";
		jsonobj["password"] = hrpassword;
		jsonobj["username"] = username;

		var jsonstring = JSON.stringify(jsonobj);
		var vallink = "/CORE_VALUE_PLEDGE_APP/validatelogin";
		$.ajax({
			type : 'POST',
			data : jsonstring,
			async : false,
			contentType : "application/json",
			url : vallink,
			success : function(resp) {
				var statusreport = JSON.parse(resp);
				if (statusreport.statuscode == 00) {
					window.location = "/CORE_VALUE_PLEDGE_APP/LIST?" + btoa("token=" + statusreport.authToken + "");
				} else {
					alert(statusreport.statusmsg);
				}
			}
		});
	} else {
		alert("Enter all the fields");
	}
}
$(function() {
	$('#hrsubmit').on(
			"click",
			function(e) {
				$.hrLogin();
			});
});
function hrSubmit(event) {
	  if( event.which == 13 || event.keyCode == 13)
		  {
		  $.hrLogin();
		  }
	}
// -----------------------------------------------------------------------------------------------
// HR DOWNLOAD PAGE
// VIEW LIST PAGE
$(function() {
	$('#viewlist').on("click", function(e) {
		window.location = "/CORE_VALUE_PLEDGE_APP/LIST";
	});
});
// HR DOWNLOAD BY EMPID
$(function() {
	$('#hrdownload')
			.on(
					"click",
					function(e) {
						var empid = document.getElementById("empid").value;
						if (empid != "") {
							var dwnpaglink = "/CORE_VALUE_PLEDGE_APP/downloadSignedPledge/hr-"
									+ empid + "";
							var form = $('#downloadpledge')[0];
							form.action = dwnpaglink;
							form.submit();
							e.preventDefault();
						} else {
							alert("Enter EmpID");
						}
					});
});
// ------------------------------------------------------------------------------------------------------

// ------------------------------------------------------------------------------------------------------
// LIST PAGE
// ON LOAD DISPLAYUN SIGNED LIST
//$("report")
//.ready(
//		function() {
//			var todecode = location.search;
//			var decode = todecode.split("?")[1];
//			var token = atob(decode);
//			var token1 = token.split("token=")[1];
//			
//			
//			var value = $('#list :selected').val();
//			var company = $('#company :selected').val();
//			var selected = $('#list :selected').text();
//			//alert(selected);
//			//alert(token1);
//			$('#selectedtype').html(selected);
//			
//			var validatetoken = "/CORE_VALUE_PLEDGE_APP/validateToken/"+token1+"";
//			$.ajax({
//				type : 'GET',
//				async : false,
//				contentType : "application/json",
//				url : validatetoken,
//				success : function(resp) {
//					var statusreport = JSON.parse(resp);
//					//alert(statusreport.statuscode);
//					if (statusreport.statuscode == 00) {
//						var jsonobj = {};
//						jsonobj["value"] = value;
//						jsonobj["company"] = company;
//						var listlink = "/CORE_VALUE_PLEDGE_APP/getEmployeeList";
//						var jsonstring = JSON.stringify(jsonobj);
//						$
//								.ajax({
//									type : 'POST',
//									async : false,
//									data : jsonstring,
//									contentType : "application/json",
//									url : listlink,
//									success : function(resp) {
//										var tablelist = JSON.parse(resp);
//										$('#listtable')
//												.append(
//														'<tr ><th colspan="6" style="display:none;">'+selected+'</th></tr><tr><th>Name</th><th>Company</th><th>Emp ID</th><th>Contact No</th><th>Sign Status</th></tr>');
//										for (var i = 0; i < tablelist.length; i++) {
//											$('#listtable')
//													.append(
//															'<tr ><td align="center">'
//																	+ tablelist[i]['empname']
//																	+ '</td><td align="center">'
//																	+ tablelist[i]['empcompany']
//																	+ '</td><td align="center">'
//																	+ tablelist[i]['empid']
//																	+ '</td><td align="center">'
//																	+ tablelist[i]['empphone']
//																	+ '</td><td align="center">'
//																	+ tablelist[i]['empsignstatus']
//																	+ '</td></tr>');
//										}
//										$('#totalcount').html("Total Count = "+tablelist.length+"/"+tablelist[0]['emptotal']);
//										$('#listtable').append('<tr style="display:none;"><th>Total Count</th><td align="center">'+tablelist.length+'/'+tablelist[0]['emptotal']+'</td></tr>');
//									}
//								});
//					}
//					else{
//						alert("Session expried!! Try again!!");
//						window.location = "/HRD";
//					}
//				}
//		});
//});

//EMPLOYEE LIST FUNCTION BY FILTERS
$(function() {
	$('#list,#company')
		.on(
				"change",
				function(e) {
					getpiedashboarddata();
					var value = $('#list :selected').val();
					var company = $('#company :selected').val();
					var selected = $('#list :selected').text();
					$('#selectedtype').html(selected);
					// alert(value);
					var jsonobj = {};
					if (value != "") {
						$("#listtable tr").remove();
						jsonobj["value"] = value;
						jsonobj["company"] = company;
					}
					var listlink = "/CORE_VALUE_PLEDGE_APP/getEmployeeList";
					var jsonstring = JSON.stringify(jsonobj);
					// alert(jsonstring);
					$
							.ajax({
								type : 'POST',
								async : false,
								data : jsonstring,
								contentType : "application/json",
								url : listlink,
								success : function(resp) {
									if(resp == "" || resp == "[]" || resp == "null"){
										$('#totalcount').html("Total Count = 0/0");
									} else {
										
									var tablelist = JSON.parse(resp);
									var tablelength = tablelist.length;
									if(value=="unSigEmpList"){
									$('#listtable')
											.append(
													'<tr ><th colspan="6" style="display:none;">'+selected+'</th></tr><tr><th>Name</th><th>Company</th><th>Emp ID</th><th>Sign Status</th></tr>');
									for (var i = 0; i < tablelength; i++) {
										$('#listtable')
												.append(
														'<tr class="data"><td align="center">'
																+ tablelist[i]['empname']
																+ '</td><td align="center">'
																+ tablelist[i]['empcompany']
																+ '</td><td align="center">'
																+ tablelist[i]['empid']
																+ '</td><td align="center">'
																+ tablelist[i]['empsignstatus']
																+ '</td></tr>');
									}
									$('#totalcount').html("Total Count = "+tablelist.length+"/"+tablelist[0]['emptotal']);
									$('#listtable').append('<tr style="display:none;"><th>Total Count</th><td align="center">'+tablelist.length+'/'+tablelist[0]['emptotal']+'</td></tr>');
								}else
									{
									$('#listtable')
									.append(
											'<tr ><th colspan="7" style="display:none;">'+selected+'</th></tr><tr><th>Name</th><th>Company</th><th>Emp ID</th><th>Sign Status</th><th>Signed On</th><th>Sign Type</th></tr>');
							for (var i = 0; i < tablelength; i++) {
								$('#listtable')
										.append(
												'<tr class="data"><td align="center">'
														+ tablelist[i]['empname']
														+ '</td><td align="center">'
														+ tablelist[i]['empcompany']
														+ '</td><td align="center">'
														+ tablelist[i]['empid']
														+ '</td><td align="center">'
														+ tablelist[i]['empsignstatus']
														+ '</td><td align="center">'
														+ tablelist[i]['empsignedon']
														+ '</td><td align="center">'
														+ tablelist[i]['empsigntype']
														+ '</td></tr>');
							}
							$('#totalcount').html("Total Count = "+tablelist.length+"/"+tablelist[0]['emptotal']);
							$('#listtable').append('<tr style="display:none;"><th >Total Count</th><td align="center">'+tablelist.length+'/'+tablelist[0]['emptotal']+'</td></tr>');
									}
								}
							}
							});
				});
	});

$(function() {
	$('#signout').on("click", function(e) {
	window.location = "/CORE_VALUE_PLEDGE_APP/EMPLOYEE";
	});
});

$(function() {
	$('#backfromofflinepledgedoc').on("click", function(e) {
	window.location = "/CORE_VALUE_PLEDGE_APP/PLEDGEINFO";
	});
});

// -------------------------------------------------------------------------------------------------------------------------
// INSTRUCTIONS PAGE

var referenceno;
$(function() {
	$('#instructions').ready(function(e) {
		var todecode = location.search;
		var decode = todecode.split("?")[1];
		var empid1 = atob(decode);
		var empid2 = empid1.split("empid=")[1];
		// alert(empid2);
		var jsonobj = {};
		jsonobj["empid"] = empid2;
		var jsonstring = JSON.stringify(jsonobj);
		// alert(jsonstring);
		var getstringlink = "/CORE_VALUE_PLEDGE_APP/getPledgeInfo";
		$.ajax({
			type : "POST",
			data : jsonstring,
			async : false,
			contentType : "application/json",
			url : getstringlink,
			success : function(resp) {

				var docdata = JSON.parse(resp);
				referenceno = docdata.referenceno;
			}
		});
	});
});
$(function() {
	$('#submitpledge').on("click", function(e) {
		var todecode = location.search;
		var decode = todecode.split("?")[1];
		var empid1 = atob(decode);
		var empid2 = empid1.split("empid=")[1];
		var jsonobj = {};
		jsonobj["empid"] = empid2;
		jsonobj["referenceno"] = referenceno;
		var jsonstring = JSON.stringify(jsonobj);
		var submitpledgedoc = "/CORE_VALUE_PLEDGE_APP/submitConsent";
		$.ajax({
			type : "POST",
			data : jsonstring,
			async : false,
			contentType : "application/json",
			url : submitpledgedoc,
			success : function(response) {
				var obj2 = JSON.parse(response);
				if (obj2.statuscode == '00') {
					var form = $('#URL')[0];
					document.getElementById('msg').value = obj2.espXML;
					form.action = obj2.aspUrl;
					form.submit();
				} else {
					alert(obj2.statusmsg);
				}
			}
		});
	});
});

function downloadcsv(csv, filename){
	var csvFile;
	var downloadLink;
	csvFile=new Blob([csv],{type:"text/csv"});
	//CSV file
	if (window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveBlob(csvFile, filename);
    }else{
    	
    	//download link
    	downloadLink=document.createElement("a");
    	//File name
    	downloadLink.download=filename;
    	//create a link to rhe file
    	downloadLink.href=window.URL.createObjectURL(csvFile);
    	//Hide download link
    	downloadLink.style.display="none";
    	//add the link to dom
    	document.body.appendChild(downloadLink);
    	//click download link
    	downloadLink.click();
    }
	
}
function exportTableToCSV(filename){
	var csv=[];
	var rows=document.querySelectorAll("#listtable tr");
	for(var i=0;i<rows.length;i++){
		var row=[],cols=rows[i].querySelectorAll("td,th");
		for(var j=0;j<cols.length;j++)
			row.push(cols[j].innerText);
		csv.push(row.join(","));
	}
	//download csv file
	downloadcsv(csv.join("\n"),filename);
}
//Dashboard Data-------------------------------------------------------------------------------
function getpiedashboarddata(){
  	var value = $('#list :selected').val();
	var company = $('#company :selected').val();
	var validatetoken = "/CORE_VALUE_PLEDGE_APP/getWidgets";
			var jsonobj = {};
			jsonobj["listType"] = value;
			jsonobj["company"] = company;
			var jsonstring = JSON.stringify(jsonobj);
			$.ajax({
				type : 'POST',
				async : false,
				contentType : "application/json",
				data : jsonstring,
				url : validatetoken,
				success : function(resp) {
					var listData = JSON.parse(resp);
					displaypiechart(listData);
				}
			});
}

function getbardashboarddata(){

  	var value = $('#list :selected').val();
	var company = $('#company :selected').val();
	var validatetoken = "/CORE_VALUE_PLEDGE_APP/getWidgets";
			var jsonobj = {};
			jsonobj["listType"] = value;
			jsonobj["company"] = company;
			var jsonstring = JSON.stringify(jsonobj);
			$.ajax({
				type : 'POST',
				async : false,
				contentType : "application/json",
				data : jsonstring,
				url : validatetoken,
				success : function(resp) {
					var listData = JSON.parse(resp);
					displaybarchart(listData);
				}
			});
}
var myChart;
function displaybarchart(y)
{
if (myChart) {
myChart.destroy();
}
	document.getElementById("pie").style.display='inline';
	document.getElementById("bar").style.display='none';
	document.getElementById("mypieChart").style.display='none';
	document.getElementById("mybarChart").style.display='inline';
	
	var ctx = document.getElementById("mybarChart");
    myChart = new Chart(ctx, {
      type: 'bar',
   	data: {
        labels: [ "Signed", "Pending"],
        datasets: [{
        	label: 'Pledge Status',
          data: [y.signedcount, y.unsignedcount],
          backgroundColor: [
			'#3e95cd',
            '#FF0000'
           ],
          borderColor: [
			'rgb(0,0,0)',
            'rgb(0,0,0)',
            
          ],
          borderWidth: 2
        }]
      },
      options: {
    	  responsive: true,
    	  maintainAspectRatio: false,
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero: true
            }
          }]
        }
      }
    });   	
}
function displaypiechart(x)
{ 
if (myChart) {
    myChart.destroy();
  }
document.getElementById("bar").style.display='inline';
document.getElementById("pie").style.display='none';
document.getElementById("mybarChart").style.display='none';
document.getElementById("mypieChart").style.display='inline';

var ctx = document.getElementById("mypieChart");
myChart = new Chart(ctx,  {
type: 'pie',
data: {
  labels: ["Signed", "Pending"],
  datasets: [{
    label: "Pledge Status",
    backgroundColor: ["#3e95cd","#FF0000"],
    data: [x.signedcount, x.unsignedcount]
  }]
},
options: {
	responsive: true,
	  maintainAspectRatio: false,
  title: {
    display: true
  }
}
});
}