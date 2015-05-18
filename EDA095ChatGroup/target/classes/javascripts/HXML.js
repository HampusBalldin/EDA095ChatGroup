var HXML = HXML || {};
HXML.getXMLDoc = function (filename, callback) {
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4) {
                callback(xhttp);
            }
        };
    } else { // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.overrideMimeType("text/xml");
    xhttp.open("GET", filename, true);
    xhttp.send();
}

HXML.postXMLDoc = function (filename, callback, data) {
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4) {
                callback(xhttp);
            }
        };
    } else { // code for IE5 and IE6
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.overrideMimeType("text/xml");
    xhttp.open("POST", filename, true);
    xhttp.send(data);
}

HXML.MESSAGE_TYPE = {
    Request_Receive_Data: "Request_Receive_Data",
    Request_Send_Data: "Request_Send_Data",
    Login: "Login",
    Logout: "Logout"
}

HXML.STATUS = {
    Online: "Online",
    Offline: "Offline",
    Away: "Away",
}

HXML.createMessage = function (type, origin, data, destinations) {
    var msg =
    {
        Message: {
            "type": type,
            origin,
            destinations,
            "data": data
        }
    };
    return msg;
}

HXML.createUser = function (uid, pwd, status, includeName) {
    if (includeName) {
        return {
            "User": {
                "uid": uid,
                "pwd": pwd,
                "status": status
            }
        };
    } else {
        return {
            "uid": uid,
            "pwd": pwd,
            "status": status
        };
    }
}

HXML.createFriend = function (uid_1, uid_2) {
    var friend =
    {
        "uid_1": uid_1,
        "uid_2": uid_2
    };
    return friend;
}

/**
 * Pass in as many users as wanted...
 * Returns a well-formed array of users.
 * @returns {{User: Array}}
 */
HXML.createUsers = function () {
    var json = {"User": []};
    for(var i = 0; i < arguments.length; i++){
        json.User[i] = arguments[i];
    }
    return json;
}

HXML.parseXml = function (xml) {
    var dom = null;
    if (window.DOMParser) {
        try {
            dom = (new DOMParser()).parseFromString(xml, "text/xml");
        }
        catch (e) {
            dom = null;
        }
    }
    else if (window.ActiveXObject) {
        try {
            dom = new ActiveXObject("Microsoft.XMLDOM");
            dom.async = false;
            if (!dom.loadXML(xml)) // parse error ..
                window.alert(dom.parseError.reason + dom.parseError.srcText);
        }
        catch (e) {
            dom = null;
        }
    }
    else
        alert("cannot parse xml string!");
    return dom;
}