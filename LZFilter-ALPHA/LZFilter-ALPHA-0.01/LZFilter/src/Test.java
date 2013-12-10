import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;


public class Test extends HttpServlet  {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		try {
			
			String chinese="";
			chinese=new String (request.getParameter("chinese").getBytes("windows-1252"));
			chinese=request.getParameter("chinese");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(chinese);
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public static void main(String[] args) {
		JSONArray jArray=JSONArray.fromObject(new String[]{"aaaa","bbbb"});
		System.out.println(jArray);
	}
	
}
