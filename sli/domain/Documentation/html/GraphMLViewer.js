/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


//
// yWorks GraphMLViewer 1.0
//
//
var isIE = (navigator.appVersion.indexOf("MSIE") != -1);
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1);
var isOpera = (navigator.userAgent.indexOf("Opera") != -1);

// Get the URL of the HTML document. Needed to convert relative URLs into absolute URLs.
function GetUrl() {
  return location.href;
}

var _factories = [
    function() { return new XMLHttpRequest();},
    function() { return new ActiveXObject("Msxml2.XMLHTTP"); },
    function() { return new ActiveXObject("Microsoft.XMLHTTP"); }];

var _factory = null;

// Creates a HttpRequest from one of the factories
function newRequest() {
  if (_factory != null) return _factory;
  for (var i=0; i<_factories.length; i++) {
    try {
      var factory = _factories[i];
      var request = factory();
      if (request != null) {
        _factory = factory;
        return request;
      }
    }catch(e) {
      continue;
    }
  }
  return null;
}

// loads a GraphML
function LoadGraphML(url) {
  var request = newRequest();
  var func = null;	//getCompleteCallback();	// async loading doesn't work currently
  if (request != null) {
    if (func) {
      request.open("GET", url, true);
      request.onreadystatechange = function() {
        if (request.readyState == 4) {
          onLoadComplete(request.responseText);
        }
      }
      request.send(null);
      return "async";
    } else {
      request.open("GET", url, false);
      request.send(null);
      //      var response = request.responseXML;
      var text = request.responseText;
      if (text) return text;

    }

  }
  return null;
}

function getCompleteCallback() {
  var swfFile;
  if (navigator.appName.indexOf("Microsoft") != -1) {
    swfFile = window["GraphMLViewer"];
  } else {
    swfFile = document["GraphMLViewer"];
  }
  return swfFile.loadCompleteCallback;
}

function onLoadComplete(value) {
  var func = getCompleteCallback();
  func(value);
}


// Gets the parameters and creates and starts the viewer.
// Returns false if the Flash Player is too old or not installed.
function RunPlayer() {

  var hasRequestedVersion = DetectFlashVer(9, 0, 38);

  var params = CreateParams(arguments);

  if (!hasRequestedVersion) {
    return false;
  }

  initMouseWheel();

  var url = "GraphMLViewer.swf";
  if (location.href.substr(0, 5) != "file:") {
    url = "http://www.yworks.com/products/graphmlviewer/1.1/GraphMLViewer.swf";
  }

  var str = '<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" ' +
            'id="viewer'+ params["id"] + '" width="' + params["width"] + '" height="' + params["height"] + '"' +
            ' codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">' +
            ' <param name="movie" value="' + url + '" />' +
            ' <param name="quality" value="high" />' +
            ' <param name="bgcolor" value="#ffffff" />' +
            ' <param name="allowScriptAccess" value="always" />' +
            ' <param name="FlashVars" value="' + params["flashvars"] + '" />' +
            ' <embed src="' + url + '" quality="high" bgcolor="#ffffff"' +
            '  width="' + params["width"] + '" height="' + params["height"] + '" name="viewer'+ params["id"] + '" align="middle"' +
            '  play="true"' +
            '  loop="false"' +
            '  allowScriptAccess="always"' +
            '  type="application/x-shockwave-flash"' +
            '  pluginspage="http://www.adobe.com/go/getflashplayer"' +
            '  FlashVars="' + params["flashvars"] + '">' +
            '</embed>' +
            '</object>';

  document.write(str);

  return true;
}

function CreateParams(args) {
  var ret = new Object();
  var flashVars = new Object();
  for (var i = 0; i < args.length; i += 2) {
    var param = String(args[i]).toLowerCase();
    switch (param) {
      case "width":
      case "height":
      case "flashvars":
      case "id":
        ret[param] = args[i + 1];
        break;
      default:
        flashVars[param] = args[i + 1];
        break;
    }
  }
  if (!ret["flashvars"]) {
    var flashVarString = "";
    if (flashVars["graphurl"] != null) {
      flashVarString = flashVarString + "&graphUrl=" + flashVars["graphurl"];
    }
    if (flashVars["overview"] != null) {
      flashVarString = flashVarString + "&overview=" + flashVars["overview"];
    }
    if (flashVars["toolbar"] != null) {
      flashVarString = flashVarString + "&toolbar=" + flashVars["toolbar"];
    }
    if (flashVars["tooltips"] != null) {
      flashVarString = flashVarString + "&tooltips=" + flashVars["tooltips"];
    }
    if (flashVars["movable"] != null) {
      flashVarString = flashVarString + "&movable=" + flashVars["movable"];
    }
    if (flashVars["links"] != null) {
      flashVarString = flashVarString + "&links=" + flashVars["links"];
    }
    if (flashVars["linksinnewwindow"] != null) {
      flashVarString = flashVarString + "&linksInNewWindow=" + flashVars["linksinnewwindow"];
    }
    if (flashVars["viewport"] != null) {
      flashVarString = flashVarString + "&viewport=" + flashVars["viewport"];
    }
    if (flashVars["scrollbars"] != null) {
      flashVarString = flashVarString + "&scrollbars=" + flashVars["scrollbars"];
    }
    if (flashVarString.length > 0) {
      ret["flashvars"] = flashVarString.substring(1, flashVarString.length);
    }
  }
  if (!ret["width"]) {
    ret["width"] = "100%";
  }
  if (!ret["height"]) {
    ret["height"] = "100%";
  }
  if (!ret["id"]) {
    ret["id"] = "1";
  }
  return ret;
}


////////////////// Mouse wheel stuff //////////////////////


// create unique namespace
if(typeof eb == "undefined" || !eb)	eb = {};

var userAgent = navigator.userAgent.toLowerCase();
eb.platform = {
    win:/win/.test(userAgent),
    mac:/mac/.test(userAgent)
};
eb.browser = {
    version: (userAgent.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1],
    safari: /webkit/.test(userAgent),
    opera: /opera/.test(userAgent),
    msie: /msie/.test(userAgent) && !/opera/.test(userAgent),
    mozilla: /mozilla/.test(userAgent) && !/(compatible|webkit)/.test(userAgent),
    chrome: /chrome/.test(userAgent)
};

function isHovering(evt) {
	var r = false;
    var elem = evt.srcElement ? evt.srcElement : evt.target;
	if( null != elem ) {
		var nodeName = elem.nodeName.toLowerCase();
	    if (( nodeName == "object" &&
	          elem.getAttribute("classid").toLowerCase() == "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000") ||
	        (nodeName == "embed" &&
	          elem.getAttribute("type") == "application/x-shockwave-flash")) {
	        r = true;
	    }
    }
    return r;
}

function onMouseWheel(evt) {
    evt = evt? evt : window.event;
    if (isHovering(evt)) { return handleMouseWheel(evt); }
    return true;
}

function handleMouseWheel(evt) {
    evt = evt ? evt : window.event;
    if (evt.stopPropagation) {
        evt.stopPropagation();
    }
    if (evt.preventDefault) {
        evt.preventDefault();
    }
    evt.cancelBubble = true;
    evt.cancel = true;
    evt.returnValue = false;
    var elem = evt.srcElement ? evt.srcElement : evt.target;
    if (evt.wheelDelta)        delta = evt.wheelDelta / (eb.browser.opera ? 12 : 120);
    else if (evt.detail)        delta = -evt.detail;
    if (eb.platform.mac && elem.externalMouseEvent)  elem.externalMouseEvent(delta);
    return false;
}
var  initialized = false;

function initMouseWheel() {
  if (!initialized) {
    if (window.addEventListener) {
        window.addEventListener('DOMMouseScroll', onMouseWheel, false);
    }
    window.onmousewheel = document.onmousewheel = onMouseWheel;
    initialized = true;
    }
}



// JavaScript helper required to detect Flash Player PlugIn version information
function GetSwfVer() {
  // NS/Opera version >= 3 check for Flash plugin in plugin array
  var flashVer = -1;

  if (navigator.plugins != null && navigator.plugins.length > 0) {
    if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
      var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
      var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
      var descArray = flashDescription.split(" ");
      var tempArrayMajor = descArray[2].split(".");
      var versionMajor = tempArrayMajor[0];
      var versionMinor = tempArrayMajor[1];
      var versionRevision = descArray[3];
      if (versionRevision == "") {
        versionRevision = descArray[4];
      }
      if (versionRevision[0] == "d") {
        versionRevision = versionRevision.substring(1);
      } else if (versionRevision[0] == "r") {
        versionRevision = versionRevision.substring(1);
        if (versionRevision.indexOf("d") > 0) {
          versionRevision = versionRevision.substring(0, versionRevision.indexOf("d"));
        }
      }
      flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
    }
  }
  // MSN/WebTV 2.6 supports Flash 4
  else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
  // WebTV 2.5 supports Flash 3
  else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
  // older WebTV supports Flash 2
  else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
  else if (isIE && isWin && !isOpera) {
    flashVer = ControlVersion();
  }
  return flashVer;
}

function ControlVersion()
{
	var version;
	var axo;
	var e;

	// NOTE : new ActiveXObject(strFoo) throws an exception if strFoo isn't in the registry

	try {
		// version will be set for 7.X or greater players
		axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
		version = axo.GetVariable("$version");
	} catch (e) {
	}

	if (!version)
	{
		try {
			// version will be set for 6.X players only
			axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");

			// installed player is some revision of 6.0
			// GetVariable("$version") crashes for versions 6.0.22 through 6.0.29,
			// so we have to be careful.

			// default to the first public version
			version = "WIN 6,0,21,0";

			// throws if AllowScripAccess does not exist (introduced in 6.0r47)
			axo.AllowScriptAccess = "always";

			// safe to call for 6.0r47 or greater
			version = axo.GetVariable("$version");

		} catch (e) {
		}
	}

	if (!version)
	{
		try {
			// version will be set for 4.X or 5.X player
			axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
			version = axo.GetVariable("$version");
		} catch (e) {
		}
	}

	if (!version)
	{
		try {
			// version will be set for 3.X player
			axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
			version = "WIN 3,0,18,0";
		} catch (e) {
		}
	}

	if (!version)
	{
		try {
			// version will be set for 2.X player
			axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
			version = "WIN 2,0,0,11";
		} catch (e) {
			version = -1;
		}
	}

	return version;
}

// When called with reqMajorVer, reqMinorVer, reqRevision returns true if that version or greater is available
function DetectFlashVer(reqMajorVer, reqMinorVer, reqRevision)
{
  var versionStr = GetSwfVer();
  if (versionStr == -1) {
    return false;
  } else if (versionStr != 0) {
    if (isIE && isWin && !isOpera) {
      // Given "WIN 2,0,0,11"
      var tempArray = versionStr.split(" ");
      // ["WIN", "2,0,0,11"]
      var tempString = tempArray[1];
      // "2,0,0,11"
      var versionArray = tempString.split(",");
      // ['2', '0', '0', '11']
    } else {
      var versionArray = versionStr.split(".");
    }
    var versionMajor = versionArray[0];
    var versionMinor = versionArray[1];
    var versionRevision = versionArray[2];

    // is the major.revision >= requested major.revision AND the minor version >= requested minor
    if (versionMajor > parseFloat(reqMajorVer)) {
      return true;
    } else if (versionMajor == parseFloat(reqMajorVer)) {
      if (versionMinor > parseFloat(reqMinorVer))
        return true;
      else if (versionMinor == parseFloat(reqMinorVer)) {
        if (versionRevision >= parseFloat(reqRevision))
          return true;
      }
    }
  }
  return false;
}

// Installs the latest Flash player.
// Needs Flash Player 6.0.65 or later.
// Returns false if the Flash player is too old or not installed.
function InstallFlashUpdate() {

  var hasProductInstall = DetectFlashVer(6, 0, 65);
  if (hasProductInstall) {
    var params = CreateParams(arguments);
    var MMPlayerType = isIE ? "ActiveX" : "PlugIn";
    var MMredirectURL = window.location;
    document.title = document.title.slice(0, 47) + " - Flash Player Installation";
    var MMdoctitle = document.title;
    var fv = "MMredirectURL=" + MMredirectURL + '&MMplayerType=' + MMPlayerType + '&MMdoctitle=' + MMdoctitle;
    document.write('<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" ' +
                   'id="GraphMLViewer" width="' + params["width"] + '" height="' + params["height"] + '"' +
                   ' codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">' +
                   ' <param name="movie" value="http://www.yworks.com/products/graphmlviewer/1.0.1/playerProductInstall.swf" />' +
                   ' <param name="quality" value="high" />' +
                   ' <param name="bgcolor" value="#ffffff" />' +
                   ' <param name="allowScriptAccess" value="sameDomain" />' +
                   ' <param name="FlashVars" value="' + fv + '" />' +
                   ' <embed src="http://www.yworks.com/products/graphmlviewer/1.0.1/playerProductInstall.swf" quality="high" bgcolor="#ffffff"' +
                   '  width="' + params["width"] + '" height="' + params["height"] + '" name="yEdViewer.graphml" align="middle"' +
                   '  play="true"' +
                   '  loop="false"' +
                   '  quality="high"' +
                   '  allowScriptAccess="sameDomain"' +
                   '  type="application/x-shockwave-flash"' +
                   '  pluginspage="http://www.adobe.com/go/getflashplayer"' +
                   '  FlashVars="' + fv + '">' +
                   '</embed>' +
                   '</object>');
    return true;
  } else {
    return false;
  }
}
