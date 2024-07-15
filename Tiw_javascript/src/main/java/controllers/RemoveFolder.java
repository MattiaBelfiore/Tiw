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
import dao.FolderDAO;
import utils.ConnectionHandler;

@WebServlet("/folderDeleter/*")
@MultipartConfig

public class RemoveFolder extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private Connection connection;

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("User not authenticated");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println("Invalid request");
            return;
        }
        
        int folderId;
        try {
            folderId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid folder ID");
            return;
        }

        FolderDAO folderDAO = new FolderDAO(connection);
        try {
            boolean deletionSuccessful = folderDAO.removeFolder(folderId, user.getId());
            if (deletionSuccessful) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Unauthorized or folder not found");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error");
        }
    }

    public void destroy() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ignore) {}
    }

}
