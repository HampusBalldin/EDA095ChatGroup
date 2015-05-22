var HXML = HXML || {};

/**
 * Sends a GET Request.
 * @param filename, the file being requested.
 * @param callback, the function being called when request successfully ACKed.
 */
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
/**
 * @param filename, the file being posted to.
 * @param callback, the function being called when request successfully ACKed.
 * @param data, the data being posted.
 */
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
/**
 * @type {{Request_Receive_Data: string, Request_Send_Data: string, Login: string, Logout: string}}
 */
HXML.MESSAGE_TYPE = {
    Request_Receive_Data: "Request_Receive_Data",
    Request_Send_Data: "Request_Send_Data",
    Login: "Login",
    Logout: "Logout",
    GetFriends: "GetFriends",
    CheckConnectionStatus: "CheckConnectionStatus"
}

/**
 * @type {{Online: string, Offline: string, Away: string}}
 */
HXML.STATUS = {
    ONLINE: "ONLINE",
    OFFLINE: "OFFLINE",
    AWAY: "AWAY"
}

/**
 *
 * @param type
 * @param origin
 * @param data
 * @param destinations
 * @returns {{Message: {type: *, origin, destinations, data: *}}}
 */
HXML.createMessage = function (type, origin, data, destinations) {
    var msg =
    {
        Message: {
            "type": type,
            origin,
            "data": data,
            destinations
        }
    };
    return msg;
}

/**
 *
 * @param uid
 * @param pwd
 * @param status
 * @param includeName
 * @returns {*}
 */
HXML.createUser = function (uid, pwd, status) {
    return {
        "uid": uid,
        "pwd": pwd,
        "status": status
    };
}

/**
 *
 * @param uid_1
 * @param uid_2
 * @returns {{uid_1: *, uid_2: *}}
 */
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
    for (var i = 0; i < arguments.length; i++) {
        json.User[i] = arguments[i];
    }
    return json;
}
/**
 *
 * @param xml
 * @returns {*}
 */
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

HXML.establishConnection = function () {
    console.log("establishConnection");
    var usr = HXML.getUser();
    uid = usr.uid;
    pwd = usr.pwd;
    console.log("UID = " + uid);
    var usr = HXML.getUser();
    var msg =
        HXML.createMessage(
            HXML.MESSAGE_TYPE.Login,
            HXML.createUser(uid, pwd, HXML.STATUS.ONLINE),
            "Test Data",
            HXML.createUsers());

    $.post("../loginhandler", JSON.stringify(msg)).done(function (arg) {
    });
}

/**
 *
 * @returns {string}
 */
HXML.checkConnectionStatus = function () {
    console.log("checkConnectionStatus");
    var usr = HXML.getUser();
    var msg =
        HXML.createMessage(
            HXML.MESSAGE_TYPE.CheckConnectionStatus,
            HXML.createUser(usr.uid, usr.pwd, HXML.STATUS.ONLINE),
            "Test Data",
            HXML.createUsers());

    return $.ajax({
        type: "POST",
        url: "../dynamichandler",
        data: JSON.stringify(msg),
        async: false
    }).responseText;
}

HXML.getUser = function () {
    uid = HXML.getCookie("uid");
    pwd = HXML.getCookie("pwd");
    return {"uid": uid, "pwd": pwd};
}

HXML.setCookie = function (cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

HXML.getCookie = function (cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var s = 0; s < ca.length; s++) {
        var c = ca[s];
        while (c.charAt(0) == ' ')
            c = c.substring(1);
        if (c.indexOf(name) == 0)
            return c.substring(name.length, c.length);
    }
    return "";
}