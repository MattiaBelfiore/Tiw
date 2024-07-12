package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.User;
import dao.DocDAO;
import utils.ConnectionHandler;

@WebServlet("/docDeleter")
@MultipartConfig

public class RemoveDoc extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int docId = Integer.parseInt(request.getParameter("docId"));
        User user = (User) request.getSession().getAttribute("user");

        DocDAO docDAO = new DocDAO(connection);
        if (docDAO.removeDoc(docId, user.getId()))
            response.setStatus(HttpServletResponse.SC_OK);
        else
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ignore){}
    }
}
