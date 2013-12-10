<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>JAVA过滤器示例</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <script type="text/javascript" src="<%=basePath%>index.js"></script>
  <script type="text/javascript" src="<%=basePath%>lib/jquery/jquery-1.10.2.min.js"></script>
    <script type="text/javascript">
   	var url='<%=basePath%>Test';
   	var chinese='这是java字符串处理的另一个标准函数,和上一个函数的作用相反,将字节数组按照charset编码进行组合识别,最后转换为unicode存储。参考上述getBytes的例子,"gbk" 和"';
   	function clearReuslt(){
   		document.getElementById('resultText').innerHTML='';
   	}
	function getChinese(){
		var value=0;
		var param=chinese;
		var encodeTimesArray=document.getElementsByName('encodeTimes');
		for(var i=0;i<encodeTimesArray.length;i++){
			var encodeTimesDom=encodeTimesArray[i];
			if(encodeTimesDom.checked){value=encodeTimesDom.value;}
		}
		switch (value) {
		case '1':
			param=encodeURI(param);
			break;
		case '2':
			param=encodeURI(encodeURI(param));
			break;
		default:
			break;
		}
		return param;
	}
   	function sendXhr(type){
   		clearReuslt();
       XHR(url,{
        	type:type,
        	isSync:false,
        	cache:'no-cache',
        	contentType:'text/html',
        	contenttype:'application/x-www-form-urlencoded',
        	param:{chinese:getChinese()},
        	callbackFun:function(){
        		var xhrObj=arguments[0];
        		var responeText=xhrObj.responseText;
        		document.getElementById('resultText').innerHTML=responeText;
        	}
        });
   	}
   function sendJq(type){
		clearReuslt();
        jQuery.ajax({ 
         	url:url,
         	//type:'GET',
         	//type:'POST',
         	type:type,
         	cache:false,
         	dataType:'text/html',
         	data:{chinese:getChinese()},
         	complete :function(){
          		var xhrObj=arguments[0];
          		var responeText=xhrObj.responseText;
          		document.getElementById('resultText').innerHTML=responeText;
        	}
        });
   }
	</script>
  <body>
    <font>参数编码次数:</font></br>
    <input type="radio" name="encodeTimes" value="0" checked="checked"/><font>0次:</font>
    <input type="radio" name="encodeTimes" value="1" /><font>1次:</font>
    <input type="radio" name="encodeTimes" value="2" /><font>2次:</font>
    </br>
    <input type="button" value="XMLHttpRequest Get请求" onclick="sendXhr('GET');"/>
    <input type="button" value="XMLHttpRequest Post请求" onclick="sendXhr('POST');"/>
    </br>
    <input type="button" value="Jquery Get请求" onclick="sendJq('GET');"/>
    <input type="button" value="Jquery Post请求" onclick="sendJq('POST');"/>
    <a id="gotoPage" href='' >地址栏请求拼接参数</a>
    <script type="text/javascript">
    	document.getElementById('gotoPage').onclick=function(){
    		document.getElementById('gotoPage').href=url+'?chinese='+getChinese();
    	}
    </script>
    </br>
    <input type="button" value="清除结果" onclick="clearReuslt();"/>
    <div id="resultText" style="border: solid;">在这里显示结果</div>
  </body>
</html>
