
var HXML = HXML || {};
HXML.getXMLDoc = function (filename, callback){
	if (window.XMLHttpRequest){
		xhttp=new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			if (xhttp.readyState == 4) {
				callback(xhttp);
			}
		};
	} else { // code for IE5 and IE6
		xhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhttp.overrideMimeType("text/xml");
	xhttp.open("GET", filename, true);
	xhttp.send();
}

HXML.postXMLDoc = function (filename, callback, data){
	if (window.XMLHttpRequest){
		xhttp=new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			if (xhttp.readyState == 4) {
				callback(xhttp);
			}
		};
	} else { // code for IE5 and IE6
		xhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhttp.overrideMimeType("text/xml");
	xhttp.open("POST", filename, true);
	xhttp.send(data);
}

HXML.messageType = {
	Request_Receive_Data:"Request_Receive_Data",
	Request_Send_Data:"Request_Send_Data",
	Login:"Login",
	Logout:"Logout"
}

HXML.createMessage = function (type, origin, data){

	var msg = 
	{Message: 
		{'type': type,
		origin,
		'data': data}};
	return msg;
}
