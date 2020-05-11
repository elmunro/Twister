package servlets.message;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class Search extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String valueKey = request.getParameter("key");
        String valueQuery = request.getParameter("query");
        String valprofileID = request.getParameter("profileID");
        JSONObject json = services.MsgServices.search(valueKey, valueQuery, valprofileID);
        out.println(json);
    }

}
