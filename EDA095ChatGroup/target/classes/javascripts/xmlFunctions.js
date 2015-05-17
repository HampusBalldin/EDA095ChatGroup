
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
	xhttp.withCredentials = true;
	xhttp.setRequestHeader('Access-Control-Allow-Credentials', 'true');
	xhttp.overrideMimeType("text/xml");
	xhttp.setRequestHeader('Cookie', 'uid=Hampus;');
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
