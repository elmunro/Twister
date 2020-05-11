package servlets.friends;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class RemoveFriend extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String valueKey = request.getParameter("key");
        String valueFriendID = request.getParameter("id_friend");
        JSONObject json = services.FriendServices.removeFriend(valueKey, valueFriendID);
        out.println(json);
    }

}
