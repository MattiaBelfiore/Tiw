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

import beans.Folder;
import beans.User;
import dao.FolderDAO;
import utils.ConnectionHandler;

@WebServlet("/GetFolders")
public class GetFolders extends HttpServlet{
	
	private Connection connection = null;
	private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleHome(request, response);
    }

    private void handleHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(false);
    	FolderDAO folder = new FolderDAO(connection);
    	User user = (User) session.getAttribute("user");
    	int userId = user.getId();
        
        List<Folder> folders = new ArrayList<Folder>();
		try {
			folders = folder.getRootFoldersByOwner(userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String serialized_folders = new Gson().toJson(folders);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(serialized_folders);
    }
}
