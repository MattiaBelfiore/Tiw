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

import com.google.gson.Gson;

import beans.Folder;
import dao.FolderDAO;
import utils.ConnectionHandler;

@WebServlet("/CreateFolder")
@MultipartConfig
public class CreateFolder extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ownerId = 0;
        String foldername = null;
        String parentId = null;
        
        try {
            // Estrai i parametri dalla request
            ownerId = Integer.parseInt(request.getParameter("userId"));
            foldername = request.getParameter("folderName");
            parentId = request.getParameter("parentId");

            // Aggiungi controlli sui parametri
            if (foldername == null || foldername.isEmpty()) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        response.getWriter().println("Folder name must be not null");
            }
            
            //controllo che non esistano altre folders con lo stesso nome
            try { 
    			FolderDAO folderDAO = new FolderDAO(connection);
    			if(!parentId.isEmpty()) {
	    			if(!folderDAO.uniqueFolder(ownerId , Integer.parseInt(parentId), foldername)) {
	    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		        response.getWriter().println("A folder with the same name already exists");
	    			} else {
	                    Folder newFolder = folderDAO.createFolder(ownerId, foldername, Integer.parseInt(parentId));
	                    String serialized_folder = new Gson().toJson(newFolder);
	            		response.setStatus(HttpServletResponse.SC_OK);
	            		response.setContentType("application/json");
	            		response.setCharacterEncoding("UTF-8");
	            		response.getWriter().println(serialized_folder);
	                }
    			} else {
    				if(!folderDAO.uniqueRoot(ownerId, foldername)) {
	    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    		        response.getWriter().println("A folder with the same name already exists");
	    			} else {
	                    Folder newFolder = folderDAO.createRootFolder(ownerId, foldername);
	                    String serialized_folder = new Gson().toJson(newFolder);
	            		response.setStatus(HttpServletResponse.SC_OK);
	            		response.setContentType("application/json");
	            		response.setCharacterEncoding("UTF-8");
	            		response.getWriter().println(serialized_folder);
	                }
    			}
    		}catch(SQLException e) {
    			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		        response.getWriter().println("Impossibile controllare se il nome utente esiste gia'");
    		}

        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println("Impossibile estrarre i parametri della form");
        } catch (IllegalArgumentException e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println(e.getMessage());
        }
    }
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
