package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import beans.Doc;
import beans.User;
import dao.DocDAO;
import utils.ConnectionHandler;

@WebServlet("/GetDocs")
public class GetDocs extends HttpServlet{
	
	private Connection connection = null;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
    	DocDAO docdao = new DocDAO(connection);
    	User user = (User) session.getAttribute("user");
    	int userId = user.getId();
        
        List<Doc> docs = new ArrayList<Doc>();
		try {
			docs = docdao.getDocsByOwner(userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String serialized_docs = new Gson().toJson(docs);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(serialized_docs);
    }
}
