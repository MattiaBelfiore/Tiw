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

import dao.DocDAO;
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    			DocDAO docdao = new DocDAO(connection);
    			if(!docdao.uniqueFile(ownerId , newFolderId, docName)) {
    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    				response.setContentType("application/json");
            		response.setCharacterEncoding("UTF-8");
            		response.getWriter().println("{'errorMsg': 'A folder with the same name already exists'}");
    			} else {
                    docdao.moveDoc(ownerId, docId, newFolderId);
            		response.setStatus(HttpServletResponse.SC_OK);
                }
    		}catch(SQLException e) {
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se il nome utente esiste già");
    		}

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Impossibile estrarre i parametri della form");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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
