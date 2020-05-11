package servlets.message;

import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FriendMessages extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String valueKey = request.getParameter("key");
        String valueFriends = request.getParameter("login");
        JSONObject json = services.MsgServices.FriendMessages(valueKey, valueFriends);
        out.println(json);
    }

}
