import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.mozilla.intl.chardet.nsPSMDetector;


public class EncodeFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException {
		
		//获取原始request对象，避开中间件对参数的编码
		HttpServletRequest newRequest = (HttpServletRequest) request;
		
		//查看request对象的参数
		try {
			/*JSONArray jArray=JSONArray.fromObject(new String[]{"aaaa","bbbb"});
			System.out.println(jArray);*/
		} catch (Exception e) {
		}
		
		//如果没有参数则直接跳过
		String paramString =  newRequest.getQueryString();
		Map<String, String[]> paramMap=newRequest.getParameterMap();
		boolean haveParamMap=paramMap.keySet().size()>0;
		if (!haveParamMap&&(paramString==null||paramString.equals(""))) {chain.doFilter(request, response);return;}
		//检查出可能的编码并打印出来（测试用）
		if (paramString!=null&&!paramString.equals("")) {
			String charsets[]=StringCodeDetector.detectedStringCode(paramString,nsPSMDetector.CHINESE);
			BytesEncodingDetect bytesEncodingDetect = new BytesEncodingDetect();
			int encodedingIndex=bytesEncodingDetect.detectEncoding(paramString.getBytes());
			String encodedingNicename=BytesEncodingDetect.nicename[encodedingIndex];
			System.out.println("encodedingNicename:"+encodedingNicename);
		}
		//获取方法类型（GET/POST）
		String methodName=newRequest.getMethod();
		//判断请求客户端的浏览器类型(解决IE的Get请求URL拼接参数没进行URIEncode问题)
		String userAgent=newRequest.getHeader("User-Agent");
		boolean isIE=userAgent.indexOf("MSIE")>-1;
		if (isIE) {
			newRequest=processIeRequest(newRequest,response,chain);
		}else {
			newRequest=processOtherRequest(newRequest,response,chain);
		}
		chain.doFilter(newRequest, response);
	}
	
	/***
	 * 解决IE通过Get/PGOS/地址栏访问方式请求乱码
	 * @param request
	 * @param response
	 * @param chain
	 * @return
	 */
	private HttpServletRequest processIeRequest(HttpServletRequest request, ServletResponse response,FilterChain chain) {
		String paramCharset="windows-1252";
		try {
			request.setCharacterEncoding(paramCharset);
		} catch (UnsupportedEncodingException e1) {}
		//通过装饰模式创建一个新的request对象，解决paramMap无法修改问题
		HttpServletRequest newRequest=convertRequest(request);  
		Map<String, String[]> paramMap=newRequest.getParameterMap();
		//把自动转码后的参数放入新的request对象里面
		for (String key : paramMap.keySet()) {
			String valueArray[]=paramMap.get(key);
			for (int i = 0; i < valueArray.length; i++) {
				try {
					//检查出可能的编码并打印出来（测试用）
					if (valueArray[i]!=null&&!valueArray[i].equals("")) {
						String charsets[]=StringCodeDetector.detectedStringCode(valueArray[i],nsPSMDetector.CHINESE);
						BytesEncodingDetect bytesEncodingDetect = new BytesEncodingDetect();
						int encodedingIndex=bytesEncodingDetect.detectEncoding(valueArray[i].getBytes());
						String encodedingNicename=BytesEncodingDetect.nicename[encodedingIndex];
						System.out.println("encodedingNicename:"+encodedingNicename);
					}
					valueArray[i]=new String(valueArray[i].getBytes("windows-1252"));
					System.out.println("1111111111      =     "+valueArray[i]);
					valueArray[i]=URLDecoder.decode(valueArray[i],"UTF-8");
					System.out.println("2222222222      =     "+valueArray[i]);
					valueArray[i]=URLDecoder.decode(valueArray[i],"UTF-8");
					System.out.println("3333333333      =     "+valueArray[i]);
				} catch (Exception e) {}
			}
			paramMap.put(key, valueArray);
		}
		return newRequest;
	}
	
	/***
	 * 对非IE通过Get/Post方式请求的参数URLDecode解码
	 * @param request
	 * @param response
	 * @param chain
	 * @return
	 */
	private HttpServletRequest processOtherRequest(HttpServletRequest request, ServletResponse response,FilterChain chain) {
		//通过装饰模式创建一个新的request对象，解决paramMap无法修改问题
		HttpServletRequest newRequest=convertRequest(request);   
		Map<String, String[]> paramMap=newRequest.getParameterMap();
		HashMap<String, String[]> newParamMap = new HashMap<String, String[]>();
		String queryStrings=newRequest.getQueryString();
		if (queryStrings==null||queryStrings.equals("")) {
			for (String key:paramMap.keySet()) {
				String[] values=paramMap.get(key);
				for (int i = 0; i < values.length; i++) {
					try {
						System.out.println("1111111111     =     "+values[i]);
						values[i]=URLDecoder.decode(values[i],"UTF-8");
						System.out.println("2222222222     =     "+values[i]);
						values[i]=URLDecoder.decode(values[i],"UTF-8");
						System.out.println("3333333333     =     "+values[i]);
					} catch (UnsupportedEncodingException e) {}
				}
				newParamMap.put(key, values);
			}
		}else {
			String userAgent=newRequest.getHeader("User-Agent");
			boolean isFirefox=userAgent.indexOf("Firefox")>-1;
			String queryStringArray[]=queryStrings.split("&");
			//把自动转码后的参数放入新的request对象里面
			for (int i = 0; i < queryStringArray.length; i++) {
				String paramArray[]=queryStringArray[i].split("=");
				String key="";
				String value="";
				try {
					key=paramArray[0];
					value=paramArray[1];
					System.out.println("1111111111     =     "+value);
					value=URLDecoder.decode(value,isFirefox?"GBK":"UTF-8");
					System.out.println("2222222222     =     "+value);
					value=URLDecoder.decode(value,"UTF-8");
					System.out.println("3333333333     =     "+value);
					value=URLDecoder.decode(value,"UTF-8");
					System.out.println("4444444444     =     "+value);
				} catch (Exception e) {}
				if (key==null||key.equals("")) {continue;}
				String[] paramValueArray=newParamMap.get(key);
				if (paramValueArray!=null&&paramValueArray.length>0) {
					List<String> paramList=Arrays.asList(paramValueArray);
					paramList.add(value);
					paramValueArray=(String[]) paramList.toArray();
					newParamMap.put(key, paramValueArray);
				}else {
					newParamMap.put(key, new String[]{value});
				}
			}
		}
		paramMap.putAll(newParamMap);
		
		return newRequest;
	}
	
	private HttpServletRequest convertRequest(final HttpServletRequest request) {
		HttpServletRequest newRequest=new HttpServletRequestWrapper(request){
			private Map params=new HashMap<String, String[]>(request.getParameterMap());
			
			
			public Map getParameterMap() {
				return params;
			}

			public Enumeration getParameterNames() {
				Vector l = new Vector(params.keySet());
				return l.elements();
			}

			public String[] getParameterValues(String name) {
				Object v = params.get(name);
				if (v == null) {
					return null;
				} else if (v instanceof String[]) {
					return (String[]) v;
				} else if (v instanceof String) {
					return new String[] { (String) v };
				} else {
					return new String[] { v.toString() };
				}
			}

			public String getParameter(String name) {
				Object v = params.get(name);
				if (v == null) {
					return null;
				} else if (v instanceof String[]) {
					String[] strArr = (String[]) v;
					if (strArr.length > 0) {
						return strArr[0];
					} else {
						return null;
					}
				} else if (v instanceof String) {
					return (String) v;
				} else {
					return v.toString();
				}
			}
		}; 
		return newRequest;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}

}
