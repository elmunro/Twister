package servlets.message;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class AddComment  extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String valueKey = request.getParameter("key");
		String valueText = request.getParameter("text");
		String valuePostID = request.getParameter("post_id");
        JSONObject json = services.MsgServices.addComment(valueKey, valuePostID, valueText);
		out.println(json);
	}

}
