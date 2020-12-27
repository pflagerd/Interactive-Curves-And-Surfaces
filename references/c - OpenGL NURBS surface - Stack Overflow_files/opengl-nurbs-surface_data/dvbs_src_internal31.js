if (window.dvPerf) window.dvPerf.addTime('dvp_in8');
function dv_rolloutManager(handlersDefsArray, baseHandler) {
    this.handle = function () {
        var errorsArr = [];

        var handler = chooseEvaluationHandler(handlersDefsArray);
        if (handler) {
            var errorObj = handleSpecificHandler(handler);
            if (errorObj === null) {
                return errorsArr;
            }
            else {
                var debugInfo = handler.onFailure();
                if (debugInfo) {
                    for (var key in debugInfo) {
                        if (debugInfo.hasOwnProperty(key)) {
                            if (debugInfo[key] !== undefined || debugInfo[key] !== null) {
                                errorObj[key] = encodeURIComponent(debugInfo[key]);
                            }
                        }
                    }
                }
                errorsArr.push(errorObj);
            }
        }

        var errorObjHandler = handleSpecificHandler(baseHandler);
        if (errorObjHandler) {
            errorObjHandler['dvp_isLostImp'] = 1;
            errorsArr.push(errorObjHandler);
        }
        return errorsArr;
    };

    function handleSpecificHandler(handler) {
        var request;
        var errorObj = null;

        try {
            request = handler.createRequest();
            if (request && !request.isSev1) {
                var url = request.url || request;
                if (url) {
                    if (!handler.sendRequest(url)) {
                        errorObj = createAndGetError('sendRequest failed.',
                            url,
                            handler.getVersion(),
                            handler.getVersionParamName(),
                            handler.dv_script);
                    }
                } else {
                    errorObj = createAndGetError('createRequest failed.',
                        url,
                        handler.getVersion(),
                        handler.getVersionParamName(),
                        handler.dv_script,
                        handler.dvScripts,
                        handler.dvStep,
                        handler.dvOther
                    );
                }
            }
        }
        catch (e) {
            errorObj = createAndGetError(e.name + ': ' + e.message, request ? (request.url || request) : null, handler.getVersion(), handler.getVersionParamName(), (handler ? handler.dv_script : null));
        }

        return errorObj;
    }

    function createAndGetError(error, url, ver, versionParamName, dv_script, dvScripts, dvStep, dvOther) {
        var errorObj = {};
        errorObj[versionParamName] = ver;
        errorObj['dvp_jsErrMsg'] = encodeURIComponent(error);
        if (dv_script && dv_script.parentElement && dv_script.parentElement.tagName && dv_script.parentElement.tagName == 'HEAD') {
            errorObj['dvp_isOnHead'] = '1';
        }
        if (url) {
            errorObj['dvp_jsErrUrl'] = url;
        }
        if (dvScripts) {
            var dvScriptsResult = '';
            for (var id in dvScripts) {
                if (dvScripts[id] && dvScripts[id].src) {
                    dvScriptsResult += encodeURIComponent(dvScripts[id].src) + ":" + dvScripts[id].isContain + ",";
                }
            }
            
            
            
        }
        return errorObj;
    }

    function chooseEvaluationHandler(handlersArray) {
        var config = window._dv_win.dv_config;
        var index = 0;
        var isEvaluationVersionChosen = false;
        if (config.handlerVersionSpecific) {
            for (var i = 0; i < handlersArray.length; i++) {
                if (handlersArray[i].handler.getVersion() == config.handlerVersionSpecific) {
                    isEvaluationVersionChosen = true;
                    index = i;
                    break;
                }
            }
        }
        else if (config.handlerVersionByTimeIntervalMinutes) {
            var date = config.handlerVersionByTimeInputDate || new Date();
            var hour = date.getUTCHours();
            var minutes = date.getUTCMinutes();
            index = Math.floor(((hour * 60) + minutes) / config.handlerVersionByTimeIntervalMinutes) % (handlersArray.length + 1);
            if (index != handlersArray.length) { 
                isEvaluationVersionChosen = true;
            }
        }
        else {
            var rand = config.handlerVersionRandom || (Math.random() * 100);
            for (var i = 0; i < handlersArray.length; i++) {
                if (rand >= handlersArray[i].minRate && rand < handlersArray[i].maxRate) {
                    isEvaluationVersionChosen = true;
                    index = i;
                    break;
                }
            }
        }

        if (isEvaluationVersionChosen == true && handlersArray[index].handler.isApplicable()) {
            return handlersArray[index].handler;
        }
        else {
            return null;
        }
    }
}

function doesBrowserSupportHTML5Push() {
    "use strict";
    return typeof window.parent.postMessage === 'function' && window.JSON;
}

function dv_GetParam(url, name, checkFromStart) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = (checkFromStart ? "(?:\\?|&|^)" : "[\\?&]") + name + "=([^&#]*)";
    var regex = new RegExp(regexS, 'i');
    var results = regex.exec(url);
    if (results == null)
        return null;
    else
        return results[1];
}

function dv_Contains(array, obj) {
    var i = array.length;
    while (i--) {
        if (array[i] === obj) {
            return true;
        }
    }
    return false;
}

function dv_GetDynamicParams(url) {
    try {
        var regex = new RegExp("[\\?&](dvp_[^&]*=[^&#]*)", "gi");
        var dvParams = regex.exec(url);

        var results = new Array();
        while (dvParams != null) {
            results.push(dvParams[1]);
            dvParams = regex.exec(url);
        }
        return results;
    }
    catch (e) {
        return [];
    }
}

function dv_createIframe() {
    var iframe;
    if (document.createElement && (iframe = document.createElement('iframe'))) {
        iframe.name = iframe.id = 'iframe_' + Math.floor((Math.random() + "") * 1000000000000);
        iframe.width = 0;
        iframe.height = 0;
        iframe.style.display = 'none';
        iframe.src = 'about:blank';
    }

    return iframe;
}

function dv_GetRnd() {
    return ((new Date()).getTime() + "" + Math.floor(Math.random() * 1000000)).substr(0, 16);
}

function dv_SendErrorImp(serverUrl, errorsArr) {

    for (var j = 0; j < errorsArr.length; j++) {
        var errorObj = errorsArr[j];
        var errorImp = dv_CreateAndGetErrorImp(serverUrl, errorObj);
        dv_sendImgImp(errorImp);
    }
}

function dv_CreateAndGetErrorImp(serverUrl, errorObj) {
    var errorQueryString = '';
    for (key in errorObj) {
        if (errorObj.hasOwnProperty(key)) {
            if (key.indexOf('dvp_jsErrUrl') == -1) {
                errorQueryString += '&' + key + '=' + errorObj[key];
            }
            else {
                var params = ['ctx', 'cmp', 'plc', 'sid'];
                for (var i = 0; i < params.length; i++) {
                    var pvalue = dv_GetParam(errorObj[key], params[i]);
                    if (pvalue) {
                        errorQueryString += '&dvp_js' + params[i] + '=' + pvalue;
                    }
                }
            }
        }
    }

    var windowProtocol = 'https:';
    var sslFlag = '&ssl=1';

    var errorImp = windowProtocol + '//' + serverUrl + sslFlag + errorQueryString;
    return errorImp;
}

function dv_sendImgImp(url) {
    (new Image()).src = url;
}

function dv_sendScriptRequest(url) {
    document.write('<scr' + 'ipt type="text/javascript" src="' + url + '"></scr' + 'ipt>');
}

function dv_getPropSafe(obj, propName) {
    try {
        if (obj)
            return obj[propName];
    } catch (e) {
    }
}

function dvBsType() {
    var that = this;
    var eventsForDispatch = {};
    this.t2tEventDataZombie = {};

    this.processT2TEvent = function (data, tag) {
        try {
            if (tag.ServerPublicDns) {
                data.timeStampCollection.push({"beginProcessT2TEvent": getCurrentTime()});
                data.timeStampCollection.push({'beginVisitCallback': tag.beginVisitCallbackTS});
                var tpsServerUrl = tag.dv_protocol + '//' + tag.ServerPublicDns + '/event.gif?impid=' + tag.uid;

                if (!tag.uniquePageViewId) {
                    tag.uniquePageViewId = data.uniquePageViewId;
                }

                tpsServerUrl += '&dvp_upvid=' + tag.uniquePageViewId;
                tpsServerUrl += '&dvp_numFrames=' + data.totalIframeCount;
                tpsServerUrl += '&dvp_numt2t=' + data.totalT2TiframeCount;
                tpsServerUrl += '&dvp_frameScanDuration=' + data.scanAllFramesDuration;
                tpsServerUrl += '&dvp_scene=' + tag.adServingScenario;
                tpsServerUrl += '&dvp_ist2twin=' + (data.isWinner ? '1' : '0');
                tpsServerUrl += '&dvp_numTags=' + Object.keys($dvbs.tags).length;
                tpsServerUrl += '&dvp_isInSample=' + data.isInSample;
                tpsServerUrl += (data.wasZombie) ? '&dvp_wasZombie=1' : '&dvp_wasZombie=0';
                tpsServerUrl += '&dvp_ts_t2tCreatedOn=' + data.creationTime;
                if (data.timeStampCollection) {
                    if (window._dv_win.t2tTimestampData) {
                        for (var tsI = 0; tsI < window._dv_win.t2tTimestampData.length; tsI++) {
                            data.timeStampCollection.push(window._dv_win.t2tTimestampData[tsI]);
                        }
                    }

                    for (var i = 0; i < data.timeStampCollection.length; i++) {
                        var item = data.timeStampCollection[i];
                        for (var propName in item) {
                            if (item.hasOwnProperty(propName)) {
                                tpsServerUrl += '&dvp_ts_' + propName + '=' + item[propName];
                            }
                        }
                    }
                }
                $dvbs.domUtilities.addImage(tpsServerUrl, tag.tagElement.parentElement);
            }
        } catch (e) {
            try {
                dv_SendErrorImp(window._dv_win.dv_config.tpsErrAddress + '/visit.jpg?ctx=818052&cmp=1619415&dvtagver=6.1.src&jsver=0&dvp_ist2tProcess=1', {dvp_jsErrMsg: encodeURIComponent(e)});
            } catch (ex) {
            }
        }
    };

    this.processTagToTagCollision = function (collision, tag) {
        var i;
        var tpsServerUrl = tag.dv_protocol + '//' + tag.ServerPublicDns + '/event.gif?impid=' + tag.uid;
        var additions = [
            '&dvp_collisionReasons=' + collision.reasonBitFlag,
            '&dvp_ts_reporterDvTagCreated=' + collision.thisTag.dvTagCreatedTS,
            '&dvp_ts_reporterVisitJSMessagePosted=' + collision.thisTag.visitJSPostMessageTS,
            '&dvp_ts_reporterReceivedByT2T=' + collision.thisTag.receivedByT2TTS,
            '&dvp_ts_collisionPostedFromT2T=' + collision.postedFromT2TTS,
            '&dvp_ts_collisionReceivedByCommon=' + collision.commonRecievedTS,
            '&dvp_collisionTypeId=' + collision.allReasonsForTagBitFlag
        ];
        tpsServerUrl += additions.join("");

        for (i = 0; i < collision.reasons.length; i++) {
            var reason = collision.reasons[i];
            tpsServerUrl += '&dvp_' + reason + "MS=" + collision[reason + "MS"];
        }

        if (tag.uniquePageViewId) {
            tpsServerUrl += '&dvp_upvid=' + tag.uniquePageViewId;
        }
        $dvbs.domUtilities.addImage(tpsServerUrl, tag.tagElement.parentElement);
    };

    var messageEventListener = function (event) {
        try {
            var timeCalled = getCurrentTime();
            var data = window.JSON.parse(event.data);
            if (!data.action) {
                data = window.JSON.parse(data);
            }
            if (data.timeStampCollection) {
                data.timeStampCollection.push({messageEventListenerCalled: timeCalled});
            }
            var myUID;
            var visitJSHasBeenCalledForThisTag = false;
            if ($dvbs.tags) {
                for (var uid in $dvbs.tags) {
                    if ($dvbs.tags.hasOwnProperty(uid) && $dvbs.tags[uid] && $dvbs.tags[uid].t2tIframeId === data.iFrameId) {
                        myUID = uid;
                        visitJSHasBeenCalledForThisTag = true;
                        break;
                    }
                }
            }

            switch (data.action) {
                case 'uniquePageViewIdDetermination' :
                    if (visitJSHasBeenCalledForThisTag) {
                        $dvbs.processT2TEvent(data, $dvbs.tags[myUID]);
                        $dvbs.t2tEventDataZombie[data.iFrameId] = undefined;
                    }
                    else {
                        data.wasZombie = 1;
                        $dvbs.t2tEventDataZombie[data.iFrameId] = data;
                    }
                    break;
                case 'maColl':
                    var tag = $dvbs.tags[myUID];
                    
                    tag.AdCollisionMessageRecieved = true;
                    if (!tag.uniquePageViewId) {
                        tag.uniquePageViewId = data.uniquePageViewId;
                    }
                    data.collision.commonRecievedTS = timeCalled;
                    $dvbs.processTagToTagCollision(data.collision, tag);
                    break;
            }

        } catch (e) {
            try {
                dv_SendErrorImp(window._dv_win.dv_config.tpsErrAddress + '/visit.jpg?ctx=818052&cmp=1619415&dvtagver=6.1.src&jsver=0&dvp_ist2tListener=1', {dvp_jsErrMsg: encodeURIComponent(e)});
            } catch (ex) {
            }
        }
    };

    if (window.addEventListener)
        addEventListener("message", messageEventListener, false);
    else
        attachEvent("onmessage", messageEventListener);

    this.pubSub = new function () {

        var subscribers = [];

        this.subscribe = function (eventName, uid, actionName, func) {
            if (!subscribers[eventName + uid])
                subscribers[eventName + uid] = [];
            subscribers[eventName + uid].push({Func: func, ActionName: actionName});
        };

        this.publish = function (eventName, uid) {
            var actionsResults = [];
            if (eventName && uid && subscribers[eventName + uid] instanceof Array)
                for (var i = 0; i < subscribers[eventName + uid].length; i++) {
                    var funcObject = subscribers[eventName + uid][i];
                    if (funcObject && funcObject.Func && typeof funcObject.Func == "function" && funcObject.ActionName) {
                        var isSucceeded = runSafely(function () {
                            return funcObject.Func(uid);
                        });
                        actionsResults.push(encodeURIComponent(funcObject.ActionName) + '=' + (isSucceeded ? '1' : '0'));
                    }
                }
            return actionsResults.join('&');
        };
    };

    this.domUtilities = new function () {

        this.addImage = function (url, parentElement) {
            var image = parentElement.ownerDocument.createElement("img");
            image.width = 0;
            image.height = 0;
            image.style.display = 'none';
            image.src = appendCacheBuster(url);
            parentElement.insertBefore(image, parentElement.firstChild);
        };

        this.addScriptResource = function (url, parentElement) {
            if (parentElement) {
                var scriptElem = parentElement.ownerDocument.createElement("script");
                scriptElem.type = 'text/javascript';
                scriptElem.src = appendCacheBuster(url);
                parentElement.insertBefore(scriptElem, parentElement.firstChild);
            }
            else {
                addScriptResourceFallBack(url);
            }
        };

        function addScriptResourceFallBack(url) {
            var scriptElem = document.createElement('script');
            scriptElem.type = "text/javascript";
            scriptElem.src = appendCacheBuster(url);
            var firstScript = document.getElementsByTagName('script')[0];
            firstScript.parentNode.insertBefore(scriptElem, firstScript);
        }

        this.addScriptCode = function (srcCode, parentElement) {
            var scriptElem = parentElement.ownerDocument.createElement("script");
            scriptElem.type = 'text/javascript';
            scriptElem.innerHTML = srcCode;
            parentElement.insertBefore(scriptElem, parentElement.firstChild);
        };

        this.addHtml = function (srcHtml, parentElement) {
            var divElem = parentElement.ownerDocument.createElement("div");
            divElem.style = "display: inline";
            divElem.innerHTML = srcHtml;
            parentElement.insertBefore(divElem, parentElement.firstChild);
        };
    };

    this.resolveMacros = function (str, tag) {
        var viewabilityData = tag.getViewabilityData();
        var viewabilityBuckets = viewabilityData && viewabilityData.buckets ? viewabilityData.buckets : {};
        var upperCaseObj = objectsToUpperCase(tag, viewabilityData, viewabilityBuckets);
        var newStr = str.replace('[DV_PROTOCOL]', upperCaseObj.DV_PROTOCOL);
        newStr = newStr.replace('[PROTOCOL]', upperCaseObj.PROTOCOL);
        newStr = newStr.replace(/\[(.*?)\]/g, function (match, p1) {
            var value = upperCaseObj[p1];
            if (value === undefined || value === null)
                value = '[' + p1 + ']';
            return encodeURIComponent(value);
        });
        return newStr;
    };

    this.settings = new function () {
    };

    this.tagsType = function () {
    };

    this.tagsPrototype = function () {
        this.add = function (tagKey, obj) {
            if (!that.tags[tagKey])
                that.tags[tagKey] = new that.tag();
            for (var key in obj)
                that.tags[tagKey][key] = obj[key];
        };
    };

    this.tagsType.prototype = new this.tagsPrototype();
    this.tagsType.prototype.constructor = this.tags;
    this.tags = new this.tagsType();

    this.tag = function () {
    };
    this.tagPrototype = function () {
        this.set = function (obj) {
            for (var key in obj)
                this[key] = obj[key];
        };

        this.getViewabilityData = function () {
        };
    };

    this.tag.prototype = new this.tagPrototype();
    this.tag.prototype.constructor = this.tag;

    this.getTagObjectByService = function (serviceName) {

        for (var impressionId in this.tags) {
            if (typeof this.tags[impressionId] === 'object'
                && this.tags[impressionId].services
                && this.tags[impressionId].services[serviceName]
                && !this.tags[impressionId].services[serviceName].isProcessed) {
                this.tags[impressionId].services[serviceName].isProcessed = true;
                return this.tags[impressionId];
            }
        }


        return null;
    };

    this.addService = function (impressionId, serviceName, paramsObject) {

        if (!impressionId || !serviceName)
            return;

        if (!this.tags[impressionId])
            return;
        else {
            if (!this.tags[impressionId].services)
                this.tags[impressionId].services = {};

            this.tags[impressionId].services[serviceName] = {
                params: paramsObject,
                isProcessed: false
            };
        }
    };

    this.Enums = {
        BrowserId: {Others: 0, IE: 1, Firefox: 2, Chrome: 3, Opera: 4, Safari: 5},
        TrafficScenario: {OnPage: 1, SameDomain: 2, CrossDomain: 128}
    };

    this.CommonData = {};

    var runSafely = function (action) {
        try {
            var ret = action();
            return ret !== undefined ? ret : true;
        } catch (e) {
            return false;
        }
    };

    var objectsToUpperCase = function () {
        var upperCaseObj = {};
        for (var i = 0; i < arguments.length; i++) {
            var obj = arguments[i];
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    upperCaseObj[key.toUpperCase()] = obj[key];
                }
            }
        }
        return upperCaseObj;
    };

    var appendCacheBuster = function (url) {
        if (url !== undefined && url !== null && url.match("^http") == "http") {
            if (url.indexOf('?') !== -1) {
                if (url.slice(-1) == '&')
                    url += 'cbust=' + dv_GetRnd();
                else
                    url += '&cbust=' + dv_GetRnd();
            }
            else
                url += '?cbust=' + dv_GetRnd();
        }
        return url;
    };

    
    var messagesClass = function () {
        var waitingMessages = [];

        this.registerMsg = function(dvFrame, data) {
            if (!waitingMessages[dvFrame.$frmId]) {
                waitingMessages[dvFrame.$frmId] = [];
            }

            waitingMessages[dvFrame.$frmId].push(data);

            if (dvFrame.$uid) {
                sendWaitingEventsForFrame(dvFrame, dvFrame.$uid);
            }
        };

        this.startSendingEvents = function(dvFrame, impID) {
            sendWaitingEventsForFrame(dvFrame, impID);
            
        };

        function sendWaitingEventsForFrame(dvFrame, impID) {
            if (waitingMessages[dvFrame.$frmId]) {
                var eventObject = {};
                for (var i = 0; i < waitingMessages[dvFrame.$frmId].length; i++) {
                    var obj = waitingMessages[dvFrame.$frmId].pop();
                    for (var key in obj) {
                        if (typeof obj[key] !== 'function' && obj.hasOwnProperty(key)) {
                            eventObject[key] = obj[key];
                        }
                    }
                }
                that.registerEventCall(impID, eventObject);
            }
        }

        function startMessageManager() {
            for (var frm in waitingMessages) {
                if (frm && frm.$uid) {
                    sendWaitingEventsForFrame(frm, frm.$uid);
                }
            }
            setTimeout(startMessageManager, 10);
        }
    };
    this.messages = new messagesClass();

    this.dispatchRegisteredEventsFromAllTags = function () {
        for (var impressionId in this.tags) {
            if (typeof this.tags[impressionId] !== 'function' && typeof this.tags[impressionId] !== 'undefined')
                dispatchEventCalls(impressionId, this);
        }
    };

    var dispatchEventCalls = function (impressionId, dvObj) {
        var tag = dvObj.tags[impressionId];
        var eventObj = eventsForDispatch[impressionId];
        if (typeof eventObj !== 'undefined' && eventObj != null) {
            var url = tag.protocol + '//' + tag.ServerPublicDns + "/bsevent.gif?impid=" + impressionId + '&' + createQueryStringParams(eventObj);
            dvObj.domUtilities.addImage(url, tag.tagElement.parentElement);
            eventsForDispatch[impressionId] = null;
        }
    };

    this.registerEventCall = function (impressionId, eventObject, timeoutMs) {
        addEventCallForDispatch(impressionId, eventObject);

        if (typeof timeoutMs === 'undefined' || timeoutMs == 0 || isNaN(timeoutMs))
            dispatchEventCallsNow(this, impressionId, eventObject);
        else {
            if (timeoutMs > 2000)
                timeoutMs = 2000;

            var dvObj = this;
            setTimeout(function () {
                dispatchEventCalls(impressionId, dvObj);
            }, timeoutMs);
        }
    };

    var dispatchEventCallsNow = function (dvObj, impressionId, eventObject) {
        addEventCallForDispatch(impressionId, eventObject);
        dispatchEventCalls(impressionId, dvObj);
    };

    var addEventCallForDispatch = function (impressionId, eventObject) {
        for (var key in eventObject) {
            if (typeof eventObject[key] !== 'function' && eventObject.hasOwnProperty(key)) {
                if (!eventsForDispatch[impressionId])
                    eventsForDispatch[impressionId] = {};
                eventsForDispatch[impressionId][key] = eventObject[key];
            }
        }
    };

    if (window.addEventListener) {
        window.addEventListener('unload', function () {
            that.dispatchRegisteredEventsFromAllTags();
        }, false);
        window.addEventListener('beforeunload', function () {
            that.dispatchRegisteredEventsFromAllTags();
        }, false);
    }
    else if (window.attachEvent) {
        window.attachEvent('onunload', function () {
            that.dispatchRegisteredEventsFromAllTags();
        }, false);
        window.attachEvent('onbeforeunload', function () {
            that.dispatchRegisteredEventsFromAllTags();
        }, false);
    }
    else {
        window.document.body.onunload = function () {
            that.dispatchRegisteredEventsFromAllTags();
        };
        window.document.body.onbeforeunload = function () {
            that.dispatchRegisteredEventsFromAllTags();
        };
    }

    var createQueryStringParams = function (values) {
        var params = '';
        for (var key in values) {
            if (typeof values[key] !== 'function') {
                var value = encodeURIComponent(values[key]);
                if (params === '')
                    params += key + '=' + value;
                else
                    params += '&' + key + '=' + value;
            }
        }

        return params;
    };
}

function dv_baseHandler(){function K(e,a){var d=document.createElement("iframe");d.name=window._dv_win.dv_config.emptyIframeID||"iframe_"+L();d.width=0;d.height=0;d.id=a;d.style.display="none";d.src=e;return d}function F(e,a,d){var d=d||150,g=window._dv_win||window;if(g.document&&g.document.body)return a&&a.parentNode?a.parentNode.insertBefore(e,a):g.document.body.insertBefore(e,g.document.body.firstChild),!0;if(0<d)setTimeout(function(){F(e,a,--d)},20);else return!1}function M(e){var a=null;try{if(a=
e&&e.contentDocument)return a}catch(d){}try{if(a=e.contentWindow&&e.contentWindow.document)return a}catch(g){}try{if(a=window._dv_win.frames&&window._dv_win.frames[e.name]&&window._dv_win.frames[e.name].document)return a}catch(b){}return null}function R(e){var a=0,d;for(d in e)e.hasOwnProperty(d)&&++a;return a}function S(e,a){var d;a:{var g={};try{if(e&&e.performance&&e.performance.getEntries)for(var b=e.performance.getEntries(),c=0;c<b.length;c++){var w=b[c],k=w.name.match(/.*\/(.+?)\./);if(k&&k[1]){var q=
k[1].replace(/\d+$/,""),m=a[q];if(m){for(var h=0;h<m.stats.length;h++){var j=m.stats[h];g[m.prefix+j.prefix]=Math.round(w[j.name])}delete a[q];if(!R(a))break}}}d=g;break a}catch(y){}d=void 0}if(d&&R(d))return d}function N(e,a,d,g,b,c,w,k,q,m){var h,j;void 0==a.dvregion&&(a.dvregion=0);var y,i,l;try{j=g;for(h=0;10>h&&j!=window._dv_win.top;)h++,j=j.parent;g.depth=h;var f=X(g);y="&aUrl="+encodeURIComponent(f.url);i="&aUrlD="+f.depth;l=g.depth+b;c&&g.depth--}catch(L){i=y=l=g.depth=""}void 0!=a.aUrl&&
(y="&aUrl="+a.aUrl);var n;a:{try{if("object"==typeof window.$ovv||"object"==typeof window.parent.$ovv){n=1;break a}}catch(G){}n=0}b=a.script.src;n="&ctx="+(dv_GetParam(b,"ctx")||"")+"&cmp="+(dv_GetParam(b,"cmp")||"")+"&plc="+(dv_GetParam(b,"plc")||"")+"&sid="+(dv_GetParam(b,"sid")||"")+"&advid="+(dv_GetParam(b,"advid")||"")+"&adsrv="+(dv_GetParam(b,"adsrv")||"")+"&unit="+(dv_GetParam(b,"unit")||"")+"&isdvvid="+(dv_GetParam(b,"isdvvid")||"")+"&uid="+a.uid+"&tagtype="+(dv_GetParam(b,"tagtype")||"")+
"&adID="+(dv_GetParam(b,"adID")||"")+"&app="+(dv_GetParam(b,"app")||"")+"&sup="+(dv_GetParam(b,"sup")||"")+"&isovv="+n;(c=dv_GetParam(b,"xff"))&&(n+="&xff="+c);(c=dv_GetParam(b,"useragent"))&&(n+="&useragent="+c);n+="&dup="+dv_GetParam(b,"dup");if(void 0!=window._dv_win.$dvbs.CommonData.BrowserId&&void 0!=window._dv_win.$dvbs.CommonData.BrowserVersion&&void 0!=window._dv_win.$dvbs.CommonData.BrowserIdFromUserAgent)h=window._dv_win.$dvbs.CommonData.BrowserId,j=window._dv_win.$dvbs.CommonData.BrowserVersion,
c=window._dv_win.$dvbs.CommonData.BrowserIdFromUserAgent;else{var x=c?decodeURIComponent(c):navigator.userAgent;h=[{id:4,brRegex:"OPR|Opera",verRegex:"(OPR/|Version/)"},{id:1,brRegex:"MSIE|Trident/7.*rv:11|rv:11.*Trident/7|Edge/",verRegex:"(MSIE |rv:| Edge/)"},{id:2,brRegex:"Firefox",verRegex:"Firefox/"},{id:0,brRegex:"Mozilla.*Android.*AppleWebKit(?!.*Chrome.*)|Linux.*Android.*AppleWebKit.* Version/.*Chrome",verRegex:null},{id:0,brRegex:"AOL/.*AOLBuild/|AOLBuild/.*AOL/|Puffin|Maxthon|Valve|Silk|PLAYSTATION|PlayStation|Nintendo|wOSBrowser",
verRegex:null},{id:3,brRegex:"Chrome",verRegex:"Chrome/"},{id:5,brRegex:"Safari|(OS |OS X )[0-9].*AppleWebKit",verRegex:"Version/"}];c=0;j="";for(f=0;f<h.length;f++)if(null!=x.match(RegExp(h[f].brRegex))){c=h[f].id;if(null==h[f].verRegex)break;x=x.match(RegExp(h[f].verRegex+"[0-9]*"));null!=x&&(j=x[0].match(RegExp(h[f].verRegex)),j=x[0].replace(j[0],""));break}h=f=Y();j=f===c?j:"";window._dv_win.$dvbs.CommonData.BrowserId=h;window._dv_win.$dvbs.CommonData.BrowserVersion=j;window._dv_win.$dvbs.CommonData.BrowserIdFromUserAgent=
c}n+="&brid="+h+"&brver="+j+"&bridua="+c;(c=dv_GetParam(b,"turl"))&&(n+="&turl="+c);(c=dv_GetParam(b,"tagformat"))&&(n+="&tagformat="+c);c="";try{var r=window._dv_win.parent,c=c+("&chro="+(void 0===r.chrome?"0":"1")),c=c+("&hist="+(r.history?r.history.length:"")),c=c+("&winh="+r.innerHeight),c=c+("&winw="+r.innerWidth),c=c+("&wouh="+r.outerHeight),c=c+("&wouw="+r.outerWidth);r.screen&&(c+="&scah="+r.screen.availHeight,c+="&scaw="+r.screen.availWidth)}catch(J){}n+=c;var v;r=function(){try{return!!window.sessionStorage}catch(a){return!0}};
c=function(){try{return!!window.localStorage}catch(a){return!0}};j=function(){var a=document.createElement("canvas");if(a.getContext&&a.getContext("2d")){var b=a.getContext("2d");b.textBaseline="top";b.font="14px 'Arial'";b.textBaseline="alphabetic";b.fillStyle="#f60";b.fillRect(0,0,62,20);b.fillStyle="#069";b.fillText("!image!",2,15);b.fillStyle="rgba(102, 204, 0, 0.7)";b.fillText("!image!",4,17);return a.toDataURL()}return null};try{h=[];h.push(["lang",navigator.language||navigator.browserLanguage]);
h.push(["tz",(new Date).getTimezoneOffset()]);h.push(["hss",r()?"1":"0"]);h.push(["hls",c()?"1":"0"]);h.push(["odb",typeof window.openDatabase||""]);h.push(["cpu",navigator.cpuClass||""]);h.push(["pf",navigator.platform||""]);h.push(["dnt",navigator.doNotTrack||""]);h.push(["canv",j()]);var t=h.join("=!!!=");if(null==t||""==t)v="";else{var r=function(a){for(var b="",c,p=7;0<=p;p--)c=a>>>4*p&15,b+=c.toString(16);return b},c=[1518500249,1859775393,2400959708,3395469782],t=t+String.fromCharCode(128),
u=Math.ceil((t.length/4+2)/16),p=Array(u);for(j=0;j<u;j++){p[j]=Array(16);for(h=0;16>h;h++)p[j][h]=t.charCodeAt(64*j+4*h)<<24|t.charCodeAt(64*j+4*h+1)<<16|t.charCodeAt(64*j+4*h+2)<<8|t.charCodeAt(64*j+4*h+3)}p[u-1][14]=8*(t.length-1)/Math.pow(2,32);p[u-1][14]=Math.floor(p[u-1][14]);p[u-1][15]=8*(t.length-1)&4294967295;t=1732584193;h=4023233417;var f=2562383102,x=271733878,O=3285377520,z=Array(80),H,A,C,D,P;for(j=0;j<u;j++){for(var s=0;16>s;s++)z[s]=p[j][s];for(s=16;80>s;s++)z[s]=(z[s-3]^z[s-8]^z[s-
14]^z[s-16])<<1|(z[s-3]^z[s-8]^z[s-14]^z[s-16])>>>31;H=t;A=h;C=f;D=x;P=O;for(s=0;80>s;s++){var F=Math.floor(s/20),Z=H<<5|H>>>27,I;c:{switch(F){case 0:I=A&C^~A&D;break c;case 1:I=A^C^D;break c;case 2:I=A&C^A&D^C&D;break c;case 3:I=A^C^D;break c}I=void 0}var K=Z+I+P+c[F]+z[s]&4294967295;P=D;D=C;C=A<<30|A>>>2;A=H;H=K}t=t+H&4294967295;h=h+A&4294967295;f=f+C&4294967295;x=x+D&4294967295;O=O+P&4294967295}v=r(t)+r(h)+r(f)+r(x)+r(O)}}catch(R){v=null}var Q;a:{u=window._dv_win;p=0;try{for(;10>p;){if(u.maple&&
"object"===typeof u.maple){Q=!0;break a}if(u==u.parent){Q=!1;break a}p++;u=u.parent}}catch(S){}Q=!1}a=(window._dv_win.dv_config.verifyJSURL||a.protocol+"//"+(window._dv_win.dv_config.bsAddress||"rtb"+a.dvregion+".doubleverify.com")+"/verify.js")+"?jsCallback="+a.callbackName+"&jsTagObjCallback="+a.tagObjectCallbackName+"&num=6"+n+"&srcurlD="+g.depth+"&ssl="+a.ssl+(m?"&dvf=0":"")+(Q?"&dvf=1":"")+"&refD="+l+a.tagIntegrityFlag+a.tagHasPassbackFlag+"&htmlmsging="+(w?"1":"0")+(null!=v?"&aadid="+v:"");
(g=dv_GetDynamicParams(b).join("&"))&&(a+="&"+g);if(!1===k||q)a=a+("&dvp_isBodyExistOnLoad="+(k?"1":"0"))+("&dvp_isOnHead="+(q?"1":"0"));d="srcurl="+encodeURIComponent(d);if((k=window._dv_win[T("=@42E:@?")][T("2?46DE@C~C:8:?D")])&&0<k.length){q=[];q[0]=window._dv_win.location.protocol+"//"+window._dv_win.location.hostname;for(g=0;g<k.length;g++)q[g+1]=k[g];k=q.reverse().join(",")}else k=null;k&&(d+="&ancChain="+encodeURIComponent(k));k=4E3;/MSIE (\d+\.\d+);/.test(navigator.userAgent)&&7>=new Number(RegExp.$1)&&
(k=2E3);if(b=dv_GetParam(b,"referrer"))b="&referrer="+b,a.length+b.length<=k&&(a+=b);y.length+i.length+a.length<=k&&(a+=i,d+=y);var E;try{var U=0;y=function(a,b){b&&32>a&&(U=(U|1<<a)>>>0)};var N=function(a,b){return function(){return a.apply(b,arguments)}},M="svg"===document.documentElement.nodeName.toLowerCase(),W=function(){return"function"!==typeof document.createElement?document.createElement(arguments[0]):M?document.createElementNS.call(document,"http://www.w3.org/2000/svg",arguments[0]):document.createElement.apply(document,
arguments)},$=["Moz","O","ms","Webkit"],aa=["moz","o","ms","webkit"],B={style:W("modernizr").style};i=function(a,b,c,p,e){var d=a.charAt(0).toUpperCase()+a.slice(1),g=(a+" "+$.join(d+" ")+d).split(" ");if("string"===typeof b||"undefined"===typeof b){a:{c=g;a=function(){h&&(delete B.style,delete B.modElem)};e="undefined"===typeof e?!1:e;if("undefined"!==typeof p&&(d=nativeTestProps(c,p),"undefined"!==typeof d)){b=d;break a}for(var h,f,k,d=["modernizr","tspan","samp"];!B.style&&d.length;)h=!0,B.modElem=
W(d.shift()),B.style=B.modElem.style;g=c.length;for(d=0;d<g;d++)if(f=c[d],k=B.style[f],~(""+f).indexOf("-")&&(f=cssToDOM(f)),void 0!==B.style[f])if(!e&&"undefined"!==typeof p){try{B.style[f]=p}catch(i){}if(B.style[f]!=k){a();b="pfx"==b?f:!0;break a}}else{a();b="pfx"==b?f:!0;break a}a();b=!1}return b}g=(a+" "+aa.join(d+" ")+d).split(" ");for(f in g)if(g[f]in b){if(!1===c)return g[f];p=b[g[f]];return"function"===typeof p?N(p,c||b):p}return!1};y(0,!0);var V;b="requestFileSystem";k=window;0===b.indexOf("@")?
V=atRule(b):(-1!=b.indexOf("-")&&(b=cssToDOM(b)),V=k?i(b,k,void 0):i(b,"pfx"));y(1,!!V);y(2,window.CSS?"function"==typeof window.CSS.escape:!1);y(3,i("shapeOutside","content-box",!0));E=U}catch(ca){E=0}E&&(a+="&m1="+E);E=ba();a+="&vavbkt="+E.vdcd;a+="&lvvn="+E.vdcv;""!=E.err&&(a+="&dvp_idcerr="+encodeURIComponent(E.err));"prerender"===window._dv_win.document.visibilityState&&(a+="&prndr=1");a+="&eparams="+encodeURIComponent(T(d))+"&"+e.getVersionParamName()+"="+e.getVersion();return{isSev1:!1,url:a}}
function ba(){var e="";try{var a=eval(function(a,b,c,d,e,q){e=function(a){return(a<b?"":e(parseInt(a/b)))+(35<(a%=b)?String.fromCharCode(a+29):a.toString(36))};if(!"".replace(/^/,String)){for(;c--;)q[e(c)]=d[c]||e(c);d=[function(a){return q[a]}];e=function(){return"\\w+"};c=1}for(;c--;)d[c]&&(a=a.replace(RegExp("\\b"+e(c)+"\\b","g"),d[c]));return a}("(G(){1A{1A{36('1z?3o:3h')}1B(e){d{1x:\"-4m\"}}n 1h=[1z];1A{n V=1z;67(V!=V.3r&&V.1K.5F.5l){1h.1y(V.1K);V=V.1K}}1B(e){}G 1P(19){1A{1v(n i=0;i<1h.1d;i++){16(19(1h[i]))d 1h[i]==1z.3r?-1:1}d 0}1B(e){d e.5X||'5D'}}G 3m(1a){d 1P(G(O){d O[1a]!=56})}G 37(O,35,19){1v(n 1a 57 O){16(1a.3a(35)>-1&&(!19||19(O[1a])))d 3o}d 3h}G g(s){n h=\"\",t=\"3N.;j&4M}4N/0:51'4r=B(4z-4e!,4k)5r\\\\{ >4o+4l\\\"4A<\";1v(i=0;i<s.1d;i++)f=s.3b(i),e=t.3a(f),0<=e&&(f=t.3b((e+41)%82)),h+=f;d h}n c=['4G\"1m-4c\"3G\"22','p','l','60&p','p','{','\\\\<}4\\\\3M-3D<\"3O\\\\<}4\\\\3z<Z?\"6','e','6p','-5,!u<}\"66}\"','p','J','-5g}\"<53','p','=o','\\\\<}4\\\\31\"2f\"w\\\\<}4\\\\31\"2f\"5v}2\"<,u\"<5}?\"6','e','J=',':<5u}T}<\"','p','h','\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"2n<N\"[1s*1t\\\\\\\\2r-5K<2L\"2t\"4b]1c}C\"13','e','4L','\\\\<}4\\\\4v;5Q||\\\\<}4\\\\4t?\"6','e','+o','\"1f\\\\<}4\\\\1T\"I<-4s\"29\"5\"4w}26<}4O\"1f\\\\<}4\\\\1l}1E>1D-1C}2}\"29\"5\"46}26<}3Z','e','=J','W}U\"<5}3T\"7}F\\\\<}4\\\\[3R}3U:3W]m}b\\\\<}4\\\\[t:2b\"4I]m}b\\\\<}4\\\\[5W})5-u<}t]m}b\\\\<}4\\\\[5U]m}b\\\\<}4\\\\[5I}5P]m}64','e','65',':6g}<\"H-1Q/2M','p','6f','\\\\<}4\\\\17<U/1o}b\\\\<}4\\\\17<U/!m}9','e','=l','10\\\\<}4\\\\69}/68}U\"<5}5h\"7}59<2F}58\\\\4Z\"5E}/m}2z','e','=S=','\\\\<}4\\\\E-5p\\\\<}4\\\\E-5s\"5\\\\U?\"6','e','+J','\\\\<}4\\\\25!5t\\\\<}4\\\\25!5q)p?\"6','e','5m','-}\"5o','p','x{','\\\\<}4\\\\E<2q-5w}5C\\\\<}4\\\\5B\"5x-5y\\\\<}4\\\\5z.42-2}\"5A\\\\<}4\\\\5k<N\"H}5j?\"6','e','+S','W}U\"<5}K\"X\"7}F\\\\<}4\\\\y<1O\"1f\\\\<}4\\\\y<2j}U\"<5}1j\\\\<}4\\\\1n-2.42-2}\"w\\\\<}4\\\\1n-2.42-2}\"1p\"L\"\"M<30\"2Y\"2S<\"<5}2R\"2P\\\\<Z\"2T<Q\"2V{2X:3q\\\\2W<1k}38-39<}3k\"3j\"1q%3l<Q\"1q%3n?\"3i\"14\"7}3c','e','54','55:,','p','52','\\\\<}4\\\\4Y\\\\<}4\\\\23\"2O\\\\<}4\\\\23\"1Y,T}1Z+++++1j\\\\<}4\\\\50\\\\<}4\\\\21\"2O\\\\<}4\\\\21\"1Y,T}1Z+++++t','e','5i','\\\\<}4\\\\5f\"1Q\"5e}b\\\\<}4\\\\E\\\\5a<M?\"6','e','5b','W}U\"<5}K:5c\\\\<}4\\\\8-2}\"1p\".42-2}\"5d-5G<N\"5H<6c<6d}C\"3H<6e<6b[<]E\"27\"1m}\"2}\"6a[<]E\"27\"1m}\"2}\"E<}18&6n\"1\\\\<}4\\\\2A\\\\6o\\\\<}4\\\\2A\\\\1l}1E>1D-1C}2}\"z<6m-2}\"6l\"2.42-2}\"6h=6i\"7}6j\"7}P=6k','e','x','5R)','p','+','\\\\<}4\\\\2I:5O<5}5N\\\\<}4\\\\2I\"5J?\"6','e','5L','L!!5S.5T.H 61','p','x=','\\\\<}4\\\\62}63)u\"5Z\\\\<}4\\\\5Y-2?\"6','e','+=','\\\\<}4\\\\2x\"5V\\\\<}4\\\\2x\"4X--6q<\"2f?\"6','e','x+','\\\\<}4\\\\8-2}\"2p}\"2o<N\"w\\\\<}4\\\\8-2}\"2p}\"2o<N\"3X\")3Y\"<:3V\"3Q}9?\"6','e','+x','\\\\<}4\\\\2m)u\"3S\\\\<}4\\\\2m)u\"40?\"6','e','49','\\\\<}4\\\\2w}s<4a\\\\<}4\\\\2w}s<48\" 47-43?\"6','e','44','\\\\<}4\\\\E\"45-2}\"E(k\"3P<N\"[1s*3L\"3y<3A]3x?\"6','e','+e','\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"3C<:[\\\\3w}}2M][\\\\3t,5}2]3u}C\"13','e','3v','10\\\\<}4\\\\3B}3K\\\\<}4\\\\3J$3E','e','3F',':3I<Z','p','4W','\\\\<}4\\\\E-4d\\\\<}4\\\\E-4J}4K\\\\<}4\\\\E-4H<4C?\"6','e','4D','$H:4E}Z!4F','p','+h','\\\\<}4\\\\E\"1J\\\\<}4\\\\E\"1L-4T?\"6','e','4U','10\\\\<}4\\\\4V:,2H}U\"<5}1r\"7}4S<4R<2F}2z','e','4P','\\\\<}4\\\\17<U/4Q&1V\"E/1W\\\\<}4\\\\17<U/4B}C\"3d\\\\<}4\\\\17<U/f[&1V\"E/1W\\\\<}4\\\\17<U/4n[S]]1T\"4j}9?\"6','e','4g','4h}4i}4p>2s','p','4q','\\\\<}4\\\\1g:<1R}s<4x}b\\\\<}4\\\\1g:<1R}s<4y<}f\"u}2G\\\\<}4\\\\2K\\\\<}4\\\\1g:<1R}s<C[S]E:2b\"1o}9','e','l{','4u\\'<}4\\\\T}5n','p','==','\\\\<}4\\\\y<1O\\\\<}4\\\\y<2B\\\\<Z\"2C\\\\<}4\\\\y<2E<Q\"?\"6','e','6N','\\\\<}4\\\\2a}28-2c\"}2d<8k\\\\<}4\\\\2a}28-2c\"}2d/2Q?\"6','e','=8l','\\\\<}4\\\\E\"2f\"8m\\\\<}4\\\\8j<8e?\"6','e','o{','\\\\<}4\\\\8f-)2\"2U\"w\\\\<}4\\\\1g-8g\\\\1m}s<C?\"6','e','+l','\\\\<}4\\\\2g-2\"8h\\\\<}4\\\\2g-2\"8n<Z?\"6','e','+{','\\\\<}4\\\\E:8o}b\\\\<}4\\\\8v-8w}b\\\\<}4\\\\E:8x\"<8u\\\\}m}9?\"6','e','{S','\\\\<}4\\\\1i}\"11}8t\"-8p\"2f\"q\\\\<}4\\\\v\"<5}8q?\"6','e','o+',' &H)&8r','p','8s','\\\\<}4\\\\E.:2}\"c\"<8d}b\\\\<}4\\\\8c}b\\\\<}4\\\\7W<}f\"u}2G\\\\<}4\\\\2K\\\\<}4\\\\1l:}\"m}9','e','7X','7Y\"5-\\'2J:2M','p','J{','\\\\<}4\\\\7Z\"5-\\'2J:7V}7U=7Q:D|q=2y|7R-5|7S--1Q/2\"|2N-2y|80\"=81\"2f\"q\\\\<}4\\\\1M\"2h:2i<1k}D?\"6','e','=8a','\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"2n<N\"[1s*1t\\\\\\\\2r-2L\"2t/8b<6r]1c}C\"13','e','87',')8z!84}s<C','p','86','\\\\<}4\\\\2u<<8y\\\\<}4\\\\2u<<8D<}f\"u}94?\"6','e','{l','\\\\<}4\\\\2v.L>g;H\\'T)Y.8X\\\\<}4\\\\2v.L>g;8Y&&8Z>H\\'T)Y.I?\"6','e','l=','10\\\\<}4\\\\91\\\\8U>8V}U\"<5}1r\"7}F\"2l}U\"<5}90\\\\<}4\\\\93<2q-20\"u\"92}U\"<5}1r\"7}F\"2l}U\"<5}8S','e','{J','H:<Z<:5','p','8F','\\\\<}4\\\\m\\\\<}4\\\\E\"8G\\\\<}4\\\\v\"<5}3g\"3f}/1j\\\\<}4\\\\8-2}\"3e<}18&8H\\\\<}4\\\\v\"<5}1b\"}u-8E=?W}U\"<5}K\"X\"7}8T\\\\<}4\\\\1i}\"v\"<5}8A\"14\"7}F\"8B','e','8C','\\\\<}4\\\\1F-U\\\\w\\\\<}4\\\\1F-8I\\\\<}4\\\\1F-\\\\<}?\"6','e','8J','8P-N:8Q','p','8R','\\\\<}4\\\\1G\"8O\\\\<}4\\\\1G\"8N\"<5}8K\\\\<}4\\\\1G\"8L||\\\\<}4\\\\8M?\"6','e','h+','83<u-7O/','p','{=','\\\\<}4\\\\v\"<5}1b\"}u-6U\\\\<}4\\\\1l}1E>1D-1C}2}\"q\\\\<}4\\\\v\"<5}1b\"}u-2D','e','=S','\\\\<}4\\\\6W\"1f\\\\<}4\\\\6T}U\"<5}1j\\\\<}4\\\\6S?\"6','e','{o','\\\\<}4\\\\6O}<6P\\\\<}4\\\\6Q}?\"6','e','=6R','\\\\<}4\\\\y<1O\\\\<}4\\\\y<2B\\\\<Z\"2C\\\\<}4\\\\y<2E<Q\"w\"1f\\\\<}4\\\\y<2j}U\"<5}t?\"6','e','J+','c>A','p','=','W}U\"<5}K\"X\"7}F\\\\<}4\\\\E\"6Y\"74:75}76^[73,][72+]6Z\\'<}4\\\\70\"2f\"q\\\\<}4\\\\E}u-6M\"14\"7}6y=6z','e','6A','\\\\<}4\\\\1S:!34\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"1N<:[f\"22*6x<Q\"6w]6s<:[<Z*1t:Z,1I]1c}C\"13','e','=6t','\\\\<}4\\\\1X\"<24-1U-u}6u\\\\<}4\\\\1X\"<24-1U-u}6v?\"6','e','{x','6C}7K','p','6J','\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"1N<:[<Z*1t:Z,1I]F:<6K[<Z*6L]1c}C\"13','e','h=','6I-2}\"v\"<5}m}9','e','6H','\\\\<}4\\\\8-2}\"E(k\"12}9?\\\\<}4\\\\8-2}\"E(k\"1N<:[<Z*6D}1I]R<-C[1s*6E]1c}C\"13','e','6F','10\\\\<}4\\\\2e\"\\\\6G\\\\<}4\\\\2e\"\\\\77','e','78','\\\\<}4\\\\1M\"w\\\\<}4\\\\1M\"2h:2i<1k}?\"6','e','{e','\\\\<}4\\\\7A}Z<}7B}b\\\\<}4\\\\7C<f\"m}b\\\\<}4\\\\7y/<}C!!7u<\"42.42-2}\"1o}b\\\\<}4\\\\7v\"<5}m}9?\"6','e','7w','T>;7x\"<4f','p','h{','\\\\<}4\\\\7D<u-7E\\\\7L}b\\\\<}4\\\\1g<}7M}9?\"6','e','7N','\\\\<}4\\\\E\"1J\\\\<}4\\\\E\"1L-3s}U\"<5}K\"X\"7}F\\\\<}4\\\\1i}\"v\"<5}1b\"E<}18&3p}33=w\\\\<}4\\\\1i}\"8-2}\"1p\".42-2}\"7J}\"u<}7I}7F\"14\"7}F\"32?\"6','e','{h','\\\\<}4\\\\7H\\\\<}4\\\\7t}<(7s?\"6','e','7f','\\\\<}4\\\\7g<U-2Z<7h&p?10\\\\<}4\\\\7e<U-2Z<79/2H}U\"<5}1r\"7}F\"7a','e','=7b','7c\\'<7i\"','p','{{','\\\\<}4\\\\E\"1J\\\\<}4\\\\E\"1L-3s}U\"<5}K\"X\"7}F\\\\<}4\\\\1i}\"v\"<5}1b\"E<}18&3p}33=7j\"14\"7}F\"32?\"6','e','7p','W}U\"<5}K\"X\"7}F\\\\<}4\\\\1S:!34\\\\<}4\\\\1n-2.42-2}\"w\\\\<}4\\\\1n-2.42-2}\"1p\"L\"\"M<30\"2Y\"2S<\"<5}2R\"2P\\\\<Z\"2T<Q\"2V{2X:3q\\\\2W<1k}38-39<}3k\"3j\"1q%3l<Q\"1q%3n?\"3i\"14\"7}3c','e','{+','\\\\<}4\\\\7o<7n a}7l}b\\\\<}4\\\\E}7m\"7k 7r- 1o}9','e','7q','7d\\\\<}4\\\\v\"<5}1S}7G\"5M&M<C<}7z}C\"3d\\\\<}4\\\\v\"<5}3g\"3f}/1j\\\\<}4\\\\8-2}\"6B\\\\<}4\\\\8-2}\"3e<}18&71[S]6X=?\"6','e','l+'];n 1w=[];n 1e=[];1v(n j=0;j<c.1d;j+=3){n r=c[j+1]=='p'?3m(g(c[j])):1P(G(O){d O.36('(G(){'+37.7P()+';d '+g(c[j])+'})();')});n 1u=6V(g(c[j+2]));16(r>0||r<0){1w.1y(r*1u)}8W 16(85 r=='89'){1w.1y(-7T*1u);1e.1y(1u+\"=\"+r)}16(1e.1d>=15)d{1x:r}}n 1H={1x:1w.2k(\",\")};16(1e.1d>0)1H.8i=1e.2k(\"&\");d 1H}1B(e){d{1x:\"-88\"}}})();",
62,563,"    Z5  Ma2vsu4f2 aM EZ5Ua a44  a44OO  return       a2MQ0242U  P1 var        E45Uu OO  E3        function _   qD8    wnd  C3     tmpWnd qsa MQ8M2   U5q  5ML44P1 3RSvsu4f2 U3q2D8M2  if EBM Z27 func prop E35f WDE42 length errors QN25sF E_ wndz ENuM2 tOO ZZ2 E2 g5 EsMu fP1 EC2 vFoS q5D8M2 fMU  id for results res push window try catch N5 Tg5 U5Z2c Euf EuZ response _t UIuCTZOO parent UT EfaNN_uZf_35f 5ML44qWZ M5OO ch uM ZU5 Eu Ef2 fC_ BV2U 2Qfq Ea Q42E Z2711t  EuZ_lEf Q42 EuZ_hEf _7Z E_Y Z2s  5Mu ENM5 ENu uf _NuM 2M_ zt__  E__N _5 2MM M511tsa join QN25sF511tsa EufB 5ML44qWfUM 0UM E_UaMM2 sMu BuZfEU5  MuU E__ EcIT_0 ELZg5 EU uZf a44nD z5 M5E 3OO  M5E32 ZP1 U25sF tzsa E27 ALZ02M ELMMuQOO kN7   Q42OO 2HFB5MZ2MvFSN7HF  vFuBf54a Q42tD11tN5f 3vFJlSN7HF32  vFl vF3 SN7HF5 2qtf  Ba Ef35M Ma2HnnDqD uNfQftD11m 4uQ2MOO str eval co HF uMC indexOf charAt Fsu4f2HnnDqD 3RSOO EM2s2MM2ME vB4u E3M2sP1tuB5a false Ma2vsu4f2nUu vFmheSN7HF42s m42s HFM ex Ht true sqtfQ 2Ms45 top NTZOOqsa Um tDE42 eS UmBu WD kC5 ENaBf_uZ_faB UEVft zt__uZ_M 5ML44qtZ 5Zu4 _tD Jl 2Z0  u_faB zt_ f_tDOOU5q 1tk27 ENaBf_uZ_uZ Ue QOO 5MqWfUM 35ZP1 tf5a u_Z2U5Z2OO qD8M2 ZA2 2r2 24t EZ5p5 2s2MI5pu 2Zt ujuM   2cM4 JJ uCUuZ2OOZ5Ua QN2P1ta Mu CEC2 oo COO EVft Na 2MUaMQOO uic2EHVO  ox M2 5IMu aNP1 LnG lkSvfxWX 99 fD NhCZ fY45 hx Kt 25a E7GXLss0aBTZIuC UufUuZ2 E7LZfKrA QN211ta CP1 CF Q6T 1bqyJIma fDE42 NLZZM2ff Je V0 7A5C C2 2MUaMQE r5Z2t 2MUaMQEU5 sOO eo PzA YDoMw8FRp3gd94 2ZtOO lJ fOO f32M_faB F5ENaB4 NTZ oJ zt_M hJ 7__E2U EuZ_hOO IuMC2 EuZ_lOO s7 he u4f xx _M null in a44OO5BVEu445 F5BVEa 2BfM2Z xo uMF21 fbQIuCpu aM4P1 Ef fgM2Z2 q5BVD8M2 xl 5Z2f EfUM href lS s5 M__ UCMOO AEBuf2g  UCME AOO ZBu r5 2_M2s2M2 2TsMu 2OO EuZZ I5b5ZQOO EaNZu U2OO unknown b4u location 2qtfUM tDHs5Mq tB IQN2 kUM xJ  _V5V5OO 2Mf LMMt 24N2MTZ Ld0 _ALb A_pLr tUBt 7__OO tUZ message EuZZTsMu uOO  cAA_cg EA5Cba Z42 a44nDqD ee g5a while Mtzsa zt_4 OO2 sq2 1SH i2E42 99D ho u_a tDRm DM2 PSHM2 HnDqD EUM2u 1Z5Ua sqt E2fUuN2z21 xh MU0 fN4uQLZfEVft FZ So uC2MOO uC2MEUB vFSN7t 1t32 FP HnnDqD xe EM2s2MM2MOO B24 1tB2uU5 1tNk4CEN3Nt oe B__tDOOU5q eh Z5Ua xS Z25 1tfMmN4uQ2Mt 2DnUu Jh ELZ0 N2MOO EuZfBQuZf Sh E5U4U5qDEN4uQ E5U4U511tsa 2P1 parseInt E5U4U5OO D11m 5NENM5U2ff_ 8lzn kE squ Sm um uC_ uMfP1 a44OOk B_UB_tD lh ubuf2b45U Ma2nDvsu4f2 Sl LZZ035NN2Mf u1 ztIMuss ol EIMuss u60 ZC2 HnUu 5M2f UP1 _f 4Zf EUuU Jx lx M5 a2TZ E_NUCEYp_c gI Eu445Uu lo _c ENuM fzuOOuE42 E4u CcM4P1 Ef2A ENM bM5 a44HnUu U2f E_NUCOO 2MtD11 bQTZqtMffmU5  f2MP1 N4uU2_faUU2ffP1 Jo _uZB45U toString zlnuZf2M UUUN 2N5 1000 wZ8 2MOOkq ErF ll gaf Egaf uZf35f DkE  _NM 4Qg5 typeof oh eJ 999 string SS kZ ErP1 4P1 u4buf2Jl E_Vu fN uCOO err E0N2U ZOO Se fNNOO uCEa u_uZ_M2saf2_M2sM2f3P1 4kE E3M2sD rLTp hl a44OOkuZwkwZ8ezhn7wZ8ezhnwE3 2M_f35 ENuMu fC532M2P1 u_ ZfOO 2u4 E3M2szsu4f2nUu Ma2nnDqDvsu4f2 oS ZfF 2DRm hh 5NOO sq M2sOO JS OOq CfE35aMfUuN E35aMfUuND CfEf2U CfOO ___U _ZBf le tnD FN1 f2Mc A_tzsa else IOO _I AbL tnDOOU5q ztBM5 af_tzsa zt U25sFLMMuQ".split(" "),
0,{}));a.hasOwnProperty("err")&&(e=a.err);return{vdcv:23,vdcd:a.res,err:e}}catch(d){return{vdcv:23,vdcd:"0",err:e}}}function X(e){try{if(1>=e.depth)return{url:"",depth:""};var a,d=[];d.push({win:window._dv_win.top,depth:0});for(var g,b=1,c=0;0<b&&100>c;){try{if(c++,g=d.shift(),b--,0<g.win.location.toString().length&&g.win!=e)return 0==g.win.document.referrer.length||0==g.depth?{url:g.win.location,depth:g.depth}:{url:g.win.document.referrer,depth:g.depth-1}}catch(w){}a=g.win.frames.length;for(var k=
0;k<a;k++)d.push({win:g.win.frames[k],depth:g.depth+1}),b++}return{url:"",depth:""}}catch(q){return{url:"",depth:""}}}function T(e){new String;var a=new String,d,g,b;for(d=0;d<e.length;d++)b=e.charAt(d),g="!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".indexOf(b),0<=g&&(b="!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".charAt((g+47)%94)),a+=b;return a}function L(){return Math.floor(1E12*(Math.random()+
""))}function Y(){try{if("function"===typeof window.callPhantom)return 99;try{if("function"===typeof window.top.callPhantom)return 99}catch(e){}if(void 0!=window.opera&&void 0!=window.history.navigationMode||void 0!=window.opr&&void 0!=window.opr.addons&&"function"==typeof window.opr.addons.installExtension)return 4;if(void 0!=window.chrome&&"function"==typeof window.chrome.csi&&"function"==typeof window.chrome.loadTimes&&void 0!=document.webkitHidden&&(!0==document.webkitHidden||!1==document.webkitHidden))return 3;
if(void 0!=window.mozInnerScreenY&&"number"==typeof window.mozInnerScreenY&&void 0!=window.mozPaintCount&&0<=window.mozPaintCount&&void 0!=window.InstallTrigger&&void 0!=window.InstallTrigger.install)return 2;if(void 0!=document.uniqueID&&"string"==typeof document.uniqueID&&(void 0!=document.documentMode&&0<=document.documentMode||void 0!=document.all&&"object"==typeof document.all||void 0!=window.ActiveXObject&&"function"==typeof window.ActiveXObject)||window.document&&window.document.updateSettings&&
"function"==typeof window.document.updateSettings)return 1;var a=!1;try{var d=document.createElement("p");d.innerText=".";d.style="text-shadow: rgb(99, 116, 171) 20px -12px 2px";a=void 0!=d.style.textShadow}catch(g){}return(0<Object.prototype.toString.call(window.HTMLElement).indexOf("Constructor")||window.webkitAudioPannerNode&&window.webkitConvertPointFromNodeToPage)&&a&&void 0!=window.innerWidth&&void 0!=window.innerHeight?5:0}catch(b){return 0}}this.createRequest=function(){var e=!1,a=window._dv_win,
d=0,g=!1,b;try{for(b=0;10>=b;b++)if(null!=a.parent&&a.parent!=a)if(0<a.parent.location.toString().length)a=a.parent,d++,e=!0;else{e=!1;break}else{0==b&&(e=!0);break}}catch(c){e=!1}0==a.document.referrer.length?e=a.location:e?e=a.location:(e=a.document.referrer,g=!0);if(!window._dv_win.dvbsScriptsInternal||!window._dv_win.dvbsProcessed||0==window._dv_win.dvbsScriptsInternal.length)return{isSev1:!1,url:null};var w;w=!window._dv_win.dv_config||!window._dv_win.dv_config.isUT?window._dv_win.dvbsScriptsInternal.pop():
window._dv_win.dvbsScriptsInternal[window._dv_win.dvbsScriptsInternal.length-1];b=w.script;this.dv_script_obj=w;this.dv_script=b;window._dv_win.dvbsProcessed.push(w);window._dv_win._dvScripts.push(b);var k=b.src;this.dvOther=0;this.dvStep=1;var q;q=window._dv_win.dv_config?window._dv_win.dv_config.bst2tid?window._dv_win.dv_config.bst2tid:window._dv_win.dv_config.dv_GetRnd?window._dv_win.dv_config.dv_GetRnd():L():L();var m;w=window.parent.postMessage&&window.JSON;var h=!0,j=!1;if("0"==dv_GetParam(k,
"t2te")||window._dv_win.dv_config&&!0==window._dv_win.dv_config.supressT2T)j=!0;if(w&&!1==j)try{m=K(window._dv_win.dv_config.bst2turl||"https://cdn3.doubleverify.com/bst2tv3.html","bst2t_"+q),h=F(m)}catch(y){}var i;m={};try{for(var l=RegExp("[\\?&]([^&]*)=([^&#]*)","gi"),f=l.exec(k);null!=f;)"eparams"!==f[1]&&(m[f[1]]=f[2]),f=l.exec(k);i=m}catch(M){i=m}i.perf=this.perf;i.uid=q;i.script=this.dv_script;i.callbackName="__verify_callback_"+i.uid;i.tagObjectCallbackName="__tagObject_callback_"+i.uid;i.tagAdtag=
null;i.tagPassback=null;i.tagIntegrityFlag="";i.tagHasPassbackFlag="";if(!1==(null!=i.tagformat&&"2"==i.tagformat)){var l=i.script,n=null,G=null,x;m=l.src;f=dv_GetParam(m,"cmp");m=dv_GetParam(m,"ctx");x="919838"==m&&"7951767"==f||"919839"==m&&"7939985"==f||"971108"==m&&"7900229"==f||"971108"==m&&"7951940"==f?"</scr'+'ipt>":/<\/scr\+ipt>/g;"function"!==typeof String.prototype.trim&&(String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g,"")});var r=function(a){if((a=a.previousSibling)&&
"#text"==a.nodeName&&(null==a.nodeValue||void 0==a.nodeValue||0==a.nodeValue.trim().length))a=a.previousSibling;if(a&&"SCRIPT"==a.tagName&&a.getAttribute("type")&&("text/adtag"==a.getAttribute("type").toLowerCase()||"text/passback"==a.getAttribute("type").toLowerCase())&&""!=a.innerHTML.trim()){if("text/adtag"==a.getAttribute("type").toLowerCase())return n=a.innerHTML.replace(x,"<\/script>"),{isBadImp:!1,hasPassback:!1,tagAdTag:n,tagPassback:G};if(null!=G)return{isBadImp:!0,hasPassback:!1,tagAdTag:n,
tagPassback:G};G=a.innerHTML.replace(x,"<\/script>");a=r(a);a.hasPassback=!0;return a}return{isBadImp:!0,hasPassback:!1,tagAdTag:n,tagPassback:G}},l=r(l);i.tagAdtag=l.tagAdTag;i.tagPassback=l.tagPassback;l.isBadImp?i.tagIntegrityFlag="&isbadimp=1":l.hasPassback&&(i.tagHasPassbackFlag="&tagpb=1")}var J;J=(/iPhone|iPad|iPod|\(Apple TV|iOS|Coremedia|CFNetwork\/.*Darwin/i.test(navigator.userAgent)||navigator.vendor&&"apple, inc."===navigator.vendor.toLowerCase())&&!window.MSStream;l=i;if(J)f="https:";
else{f=i.script.src;m="http:";k=window._dv_win.location.toString().match("^http(?:s)?");if("https"==f.match("^https")&&("https"==k||"http"!=k))m="https:";f=m}l.protocol=f;i.ssl="0";"https:"===i.protocol&&(i.ssl="1");l=i;(f=window._dv_win.dvRecoveryObj)?("2"!=l.tagformat&&(f=f[l.ctx]?f[l.ctx].RecoveryTagID:f._fallback_?f._fallback_.RecoveryTagID:1,1===f&&l.tagAdtag?document.write(l.tagAdtag):2===f&&l.tagPassback&&document.write(l.tagPassback)),l=!0):l=!1;if(l)return{isSev1:!0};this.dvStep=2;var v=
i,t,u=window._dv_win.document.visibilityState;window[v.tagObjectCallbackName]=function(a){window.dvPerf&&window.dvPerf.addTime("dvp_in13");var b=window._dv_win.$dvbs;if(b){var c;J?c="https:":(c="http:","http:"!=window._dv_win.location.protocol&&(c="https:"));t=a.ImpressionID;b.tags.add(a.ImpressionID,v);b.tags[a.ImpressionID].set({tagElement:v.script,impressionId:a.ImpressionID,dv_protocol:v.protocol,protocol:c,uid:v.uid,serverPublicDns:a.ServerPublicDns,ServerPublicDns:a.ServerPublicDns});v.script&&
v.script.dvFrmWin&&(v.script.dvFrmWin.$uid=a.ImpressionID,b.messages&&b.messages.startSendingEvents&&b.messages.startSendingEvents(v.script.dvFrmWin,a.ImpressionID));var d=function(){var b=window._dv_win.document.visibilityState;"prerender"===u&&("prerender"!==b&&"unloaded"!==b)&&(u=b,window._dv_win.$dvbs.registerEventCall(a.ImpressionID,{prndr:0}),window._dv_win.document.removeEventListener(e,d))};if("prerender"===u)if("prerender"!==window._dv_win.document.visibilityState&&"unloaded"!==visibilityStateLocal)window._dv_win.$dvbs.registerEventCall(a.ImpressionID,
{prndr:0});else{var e;"undefined"!==typeof window._dv_win.document.hidden?e="visibilitychange":"undefined"!==typeof window._dv_win.document.mozHidden?e="mozvisibilitychange":"undefined"!==typeof window._dv_win.document.msHidden?e="msvisibilitychange":"undefined"!==typeof window._dv_win.document.webkitHidden&&(e="webkitvisibilitychange");window._dv_win.document.addEventListener(e,d,!1)}}try{var f=S(window,{verify:{prefix:"vf",stats:[{name:"duration",prefix:"dur"}]}});f&&window._dv_win.$dvbs.registerEventCall(a.ImpressionID,
f)}catch(g){}b=a.ImpressionID;try{var h=S(window,{dvbs_src:{prefix:"dvp_",stats:[{name:"fetchStart",prefix:"in1"},{name:"responseEnd",prefix:"in2"}]},dvbs_src_internal:{prefix:"dvp_",stats:[{name:"fetchStart",prefix:"in6"},{name:"responseEnd",prefix:"in7"}]},verify:{prefix:"dvp_",stats:[{name:"fetchStart",prefix:"in11"},{name:"responseEnd",prefix:"in12"}]}})||{};h.dvp_in0=performance.timing.navigationStart;for(var i=window.dvPerf.results,f=0;f<i.length;f++)h[i[f].key]=i[f].value-h.dvp_in0;window._dv_win.$dvbs.registerEventCall(b,
h)}catch(j){}};window[v.callbackName]=function(a){var b;b=window._dv_win.$dvbs&&"object"==typeof window._dv_win.$dvbs.tags[t]?window._dv_win.$dvbs.tags[t]:v;var c=window._dv_win.dv_config.bs_renderingMethod||function(a){document.write(a)};switch(a.ResultID){case 1:b.tagPassback?c(b.tagPassback):a.Passback?c(decodeURIComponent(a.Passback)):a.AdWidth&&a.AdHeight&&c(decodeURIComponent("%3Cstyle%3E%0A.dvbs_container%20%7B%0A%09border%3A%201px%20solid%20%233b599e%3B%0A%09overflow%3A%20hidden%3B%0A%09filter%3A%20progid%3ADXImageTransform.Microsoft.gradient(startColorstr%3D%27%23315d8c%27%2C%20endColorstr%3D%27%2384aace%27)%3B%0A%09%2F*%20for%20IE%20*%2F%0A%09background%3A%20-webkit-gradient(linear%2C%20left%20top%2C%20left%20bottom%2C%20from(%23315d8c)%2C%20to(%2384aace))%3B%0A%09%2F*%20for%20webkit%20browsers%20*%2F%0A%09background%3A%20-moz-linear-gradient(top%2C%20%23315d8c%2C%20%2384aace)%3B%0A%09%2F*%20for%20firefox%203.6%2B%20*%2F%0A%7D%0A.dvbs_cloud%20%7B%0A%09color%3A%20%23fff%3B%0A%09position%3A%20relative%3B%0A%09font%3A%20100%25%22Times%20New%20Roman%22%2C%20Times%2C%20serif%3B%0A%09text-shadow%3A%200px%200px%2010px%20%23fff%3B%0A%09line-height%3A%200%3B%0A%7D%0A%3C%2Fstyle%3E%0A%3Cscript%20type%3D%22text%2Fjavascript%22%3E%0A%09function%0A%20%20%20%20cloud()%7B%0A%09%09var%20b1%20%3D%20%22%3Cdiv%20class%3D%5C%22dvbs_cloud%5C%22%20style%3D%5C%22font-size%3A%22%3B%0A%09%09var%20b2%3D%22px%3B%20position%3A%20absolute%3B%20top%3A%20%22%3B%0A%09%09document.write(b1%20%2B%20%22300px%3B%20width%3A%20300px%3B%20height%3A%20300%22%20%2B%20b2%20%2B%20%2234px%3B%20left%3A%2028px%3B%5C%22%3E.%3C%5C%2Fdiv%3E%22)%3B%0A%09%09document.write(b1%20%2B%20%22300px%3B%20width%3A%20300px%3B%20height%3A%20300%22%20%2B%20b2%20%2B%20%2246px%3B%20left%3A%2010px%3B%5C%22%3E.%3C%5C%2Fdiv%3E%22)%3B%0A%09%09document.write(b1%20%2B%20%22300px%3B%20width%3A%20300px%3B%20height%3A%20300%22%20%2B%20b2%20%2B%20%2246px%3B%20left%3A50px%3B%5C%22%3E.%3C%5C%2Fdiv%3E%22)%3B%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%0A%09%09document.write(b1%20%2B%20%22400px%3B%20width%3A%20400px%3B%20height%3A%20400%22%20%2B%20b2%20%2B%20%2224px%3B%20left%3A20px%3B%5C%22%3E.%3C%5C%2Fdiv%3E%22)%3B%0A%20%20%20%20%7D%0A%20%20%20%20%0A%09function%20clouds()%7B%0A%20%20%20%20%20%20%20%20var%20top%20%3D%20%5B%27-80%27%2C%2780%27%2C%27240%27%2C%27400%27%5D%3B%0A%09%09var%20left%20%3D%20-10%3B%0A%20%20%20%20%20%20%20%20var%20a1%20%3D%20%22%3Cdiv%20style%3D%5C%22position%3A%20relative%3B%20top%3A%20%22%3B%0A%09%09var%20a2%20%3D%20%22px%3B%20left%3A%20%22%3B%0A%20%20%20%20%20%20%20%20var%20a3%3D%20%22px%3B%5C%22%3E%3Cscr%22%2B%22ipt%20type%3D%5C%22text%5C%2Fjavascr%22%2B%22ipt%5C%22%3Ecloud()%3B%3C%5C%2Fscr%22%2B%22ipt%3E%3C%5C%2Fdiv%3E%22%3B%0A%20%20%20%20%20%20%20%20for(i%3D0%3B%20i%20%3C%208%3B%20i%2B%2B)%20%7B%0A%09%09%09document.write(a1%2Btop%5B0%5D%2Ba2%2Bleft%2Ba3)%3B%0A%09%09%09document.write(a1%2Btop%5B1%5D%2Ba2%2Bleft%2Ba3)%3B%0A%09%09%09document.write(a1%2Btop%5B2%5D%2Ba2%2Bleft%2Ba3)%3B%0A%09%09%09document.write(a1%2Btop%5B3%5D%2Ba2%2Bleft%2Ba3)%3B%0A%09%09%09if(i%3D%3D4)%0A%09%09%09%7B%0A%09%09%09%09left%20%3D-%2090%3B%0A%09%09%09%09top%20%3D%20%5B%270%27%2C%27160%27%2C%27320%27%2C%27480%27%5D%3B%0A%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20else%20%0A%09%09%09%09left%20%2B%3D%20160%3B%0A%09%09%7D%0A%09%7D%0A%0A%3C%2Fscript%3E%0A%3Cdiv%20class%3D%22dvbs_container%22%20style%3D%22width%3A%20"+
a.AdWidth+"px%3B%20height%3A%20"+a.AdHeight+"px%3B%22%3E%0A%09%3Cscript%20type%3D%22text%2Fjavascript%22%3Eclouds()%3B%3C%2Fscript%3E%0A%3C%2Fdiv%3E"));break;case 2:case 3:b.tagAdtag&&c(b.tagAdtag);break;case 4:a.AdWidth&&a.AdHeight&&c(decodeURIComponent("%3Cstyle%3E%0A.dvbs_container%20%7B%0A%09border%3A%201px%20solid%20%233b599e%3B%0A%09overflow%3A%20hidden%3B%0A%09filter%3A%20progid%3ADXImageTransform.Microsoft.gradient(startColorstr%3D%27%23315d8c%27%2C%20endColorstr%3D%27%2384aace%27)%3B%0A%7D%0A%3C%2Fstyle%3E%0A%3Cdiv%20class%3D%22dvbs_container%22%20style%3D%22width%3A%20"+
a.AdWidth+"px%3B%20height%3A%20"+a.AdHeight+"px%3B%22%3E%09%0A%3C%2Fdiv%3E"))}};b=b&&b.parentElement&&b.parentElement.tagName&&"HEAD"===b.parentElement.tagName;this.dvStep=3;return N(this,i,e,a,d,g,w,h,b,J)};this.sendRequest=function(e){window.dvPerf&&window.dvPerf.addTime("dvp_in9");var a=dv_GetParam(e,"tagformat");a&&"2"==a?$dvbs.domUtilities.addScriptResource(e,document.body):dv_sendScriptRequest(e);try{var d,g=this.dv_script_obj&&this.dv_script_obj.injScripts,b=window._dv_win.dv_config=window._dv_win.dv_config||
{};b.cdnAddress=b.cdnAddress||"cdn.doubleverify.com";d='<html><head><script type="text/javascript">('+function(){try{window.$dv=window.$dvbs||parent.$dvbs,window.$dv.dvObjType="dvbs"}catch(a){}}.toString()+')();<\/script></head><body><script type="text/javascript">('+(g||"function() {}")+')("'+b.cdnAddress+'");<\/script><script type="text/javascript">setTimeout(function() {document.close();}, 0);<\/script></body></html>';var c=K("about:blank");c.id=c.name;var w=c.id.replace("iframe_","");c.setAttribute&&
c.setAttribute("data-dv-frm",w);F(c,this.dv_script);if(this.dv_script){var k=this.dv_script,q;a:{e=null;try{if(e=c.contentWindow){q=e;break a}}catch(m){}try{if(e=window._dv_win.frames&&window._dv_win.frames[c.name]){q=e;break a}}catch(h){}q=null}k.dvFrmWin=q}var j=M(c);if(j)j.open(),j.write(d);else{try{document.domain=document.domain}catch(y){}var i=encodeURIComponent(d.replace(/'/g,"\\'").replace(/\n|\r\n|\r/g,""));c.src='javascript: (function(){document.open();document.domain="'+window.document.domain+
"\";document.write('"+i+"');})()"}}catch(l){d=(window._dv_win.dv_config=window._dv_win.dv_config||{}).tpsAddress||"tps30.doubleverify.com",dv_SendErrorImp(d+"/verify.js?ctx=818052&cmp=1619415",[{dvp_jsErrMsg:"DvFrame: "+encodeURIComponent(l)}])}return!0};this.isApplicable=function(){return!0};this.onFailure=function(){};window.debugScript&&(window.CreateUrl=N);this.getVersionParamName=function(){return"ver"};this.getVersion=function(){return"84"}};


function dvbs_src_main(dvbs_baseHandlerIns, dvbs_handlersDefs) {

    this.bs_baseHandlerIns = dvbs_baseHandlerIns;
    this.bs_handlersDefs = dvbs_handlersDefs;

    this.exec = function () {
        try {
            window._dv_win = (window._dv_win || window);
            window._dv_win.$dvbs = (window._dv_win.$dvbs || new dvBsType());

            window._dv_win.dv_config = window._dv_win.dv_config || {};
            window._dv_win.dv_config.bsErrAddress = window._dv_win.dv_config.bsAddress || 'rtb0.doubleverify.com';

            var errorsArr = (new dv_rolloutManager(this.bs_handlersDefs, this.bs_baseHandlerIns)).handle();
            if (errorsArr && errorsArr.length > 0)
                dv_SendErrorImp(window._dv_win.dv_config.bsErrAddress + '/verify.js?ctx=818052&cmp=1619415&num=6', errorsArr);
        }
        catch (e) {
            try {
                dv_SendErrorImp(window._dv_win.dv_config.bsErrAddress + '/verify.js?ctx=818052&cmp=1619415&num=6&dvp_isLostImp=1', {dvp_jsErrMsg: encodeURIComponent(e)});
            } catch (e) {
            }
        }
    };
};

try {
    window._dv_win = window._dv_win || window;
    var dv_baseHandlerIns = new dv_baseHandler();
	

    var dv_handlersDefs = [];
    (new dvbs_src_main(dv_baseHandlerIns, dv_handlersDefs)).exec();
} catch (e) { }
if (window.dvPerf) window.dvPerf.addTime('dvp_in10');