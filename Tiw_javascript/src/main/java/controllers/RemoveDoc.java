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

@WebServlet("/docDeleter/*")
@MultipartConfig

public class RemoveDoc extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    response.getWriter().println("User not authenticated");
		    return;
		}
    	int userId = user.getId();

    	String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println("Invalid request");
            return;
        }
        
        int docId = 0;
        try {
        	docId = Integer.parseInt(pathInfo.substring(1));
        } 
        catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid parameters");
            return;
        }

        
        try {
	        DocDAO docDAO = new DocDAO(connection);
	        if(!docDAO.checkOwner(userId, docId)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        response.getWriter().println("Unauthorized, you can't delete this document");
		        return;
			}
	        if (docDAO.removeDoc(docId))
	            response.setStatus(HttpServletResponse.SC_OK);
	        else
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		response.getWriter().println("Unable to delete document");
		}
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
