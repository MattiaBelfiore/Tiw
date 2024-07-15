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

import beans.Doc;
import dao.DocDAO;
import dao.FolderDAO;
import utils.ConnectionHandler;

@WebServlet("/MoveDocument")
@MultipartConfig
public class MoveDocument extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ownerId = 0, docId = 0, newFolderId = 0; 
        String docName = null;
        
        try {
            // Estrai i parametri dalla request
            ownerId = Integer.parseInt(request.getParameter("userId"));
            docId = Integer.parseInt(request.getParameter("docId"));
            docName = request.getParameter("docName");
            newFolderId = Integer.parseInt(request.getParameter("newFolderId"));
            
            // Aggiungi controlli sui parametri
            if (newFolderId == 0 || docId == 0) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        response.getWriter().println("Wrong data");
            }
            
            //controllo che non esistano altre folders con lo stesso nome
            try { 
            	FolderDAO folderdao = new FolderDAO(connection);
            	if(!folderdao.checkOwner(ownerId, newFolderId)) {
    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		        response.getWriter().println("Invalid folder");
    		        return;
    			}
            	
    			DocDAO docdao = new DocDAO(connection);
    			if(!docdao.uniqueFile(ownerId , newFolderId, docName)) {
    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		        response.getWriter().println("A document with the same name already exists");
    			} else {
                    Doc updatedDoc = docdao.moveDoc(docId, newFolderId);
            		String serialized_doc = new Gson().toJson(updatedDoc);
            		response.setStatus(HttpServletResponse.SC_OK);
            		response.setContentType("application/json");
            		response.setCharacterEncoding("UTF-8");
            		response.getWriter().println(serialized_doc);
                }
    		}catch(SQLException e) {
    			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        		response.getWriter().println("Unable to move document");
    		}

        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println("Invalid parameters");
        } catch (IllegalArgumentException e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().println(e.getMessage());
        }
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
