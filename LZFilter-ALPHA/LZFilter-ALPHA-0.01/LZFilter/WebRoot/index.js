
function CreateXMLHttpRequest() {
	var xhrObj;
	if (window.ActiveXObject) {// 如果当前浏览器支持Active Xobject，则创建ActiveXObject对象
		try {//自动适配新旧IE对象
			xhrObj = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				xhrObj = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				xhrObj = false;
			}
		}
	} else if (window.XMLHttpRequest) {// 如果当前浏览器支持XMLHttpRequest，则创建XMLHttpRequest对象
		xhrObj = new XMLHttpRequest();
	}
	return xhrObj;
}

var XHR =function (url) { // 主程序函数
	var config=arguments[1];
	// 创建XHR对象
	var xhrObj = CreateXMLHttpRequest(); 
	//拼接参数
	var paramString='';
	var param=config&&config.param?config.param:null;
	if (param) {
		for ( var key in param) {
			var value=param[key];
			paramString+='&'+key+'='+value;
		}
		paramString=paramString.substr(1,paramString.length);
	}
	xhrObj.open(config&&config.type?config.type:"POST", url+(config&&config.type&&config.type=="GET"?"?"+paramString:""), config?!config.isSync:false); // 调用
	xhrObj.setRequestHeader("cache-control", config&&config.cache?config.cache:"no-cache");
	xhrObj.setRequestHeader("contentType", config&&config.contentType?config.contentType:"text/html;charset=UTF-8") // 指定发送的编码
	xhrObj.setRequestHeader("Content-Type", config&&config.contenttype?config.contenttype:"application/x-www-form-urlencoded;charset=UTF-8"); // 设置请求头信息
	xhrObj.onreadystatechange = function (){
		if (xhrObj.readyState==4) {
			if (xhrObj.status==200) {
				if (config&&config.callbackFun) {
					config.callbackFun(xhrObj);
				}
			}else {
				//throw Error("Request error of : "+xmlobj.status);
			}
		}
	}; // 回调状态处理

	xhrObj.send(paramString); // 设置为发送给服务器数据
}

