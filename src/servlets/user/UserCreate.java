package servlets.user;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class UserCreate extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
		response.setContentType(" application/json ");
		PrintWriter out = response.getWriter();
		String valueNom = request.getParameter("nom");
		String valuePrenom = request.getParameter("prenom");
		String valueLogin = request.getParameter("login");
		String valuePswd = request.getParameter("pswd");
		JSONObject json = services.UserServices.createUser(valueNom, valuePrenom, valueLogin, valuePswd);
		out.println(json);
	}
}
