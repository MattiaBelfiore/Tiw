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
import utils.ConnectionHandler;

@WebServlet("/CreateDocument")
@MultipartConfig
public class CreateDocument extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ownerId = 0, parentId = 0;
        String docName = null, docSummary = null, docType = null;
        
        try {
            // Estrai i parametri dalla request
            ownerId = Integer.parseInt(request.getParameter("userId"));
            parentId = Integer.parseInt(request.getParameter("parentId"));
            docName = request.getParameter("docName");
            docSummary = request.getParameter("docSummary");
            docType = request.getParameter("docType");
            
            // Aggiungi controlli sui parametri
            if (docName == null || docName.isEmpty() || docSummary == null || docSummary.isEmpty() || docType == null || docType.isEmpty()) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    			response.getWriter().println("Name, summary and type must be not null");
            }
            
            try { 
    			DocDAO docDAO = new DocDAO(connection);
    			if(!docDAO.uniqueFile(ownerId, parentId, docName)) {
    				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		        response.setContentType("application/json");
    		        response.setCharacterEncoding("UTF-8");
    		        response.getWriter().println("{\"error\": \"A document with the same name already exists\"}");
    			}
                else {
                	Doc newDoc = docDAO.createDoc(ownerId, parentId, docName, docSummary, docType);
                	String serialized_doc = new Gson().toJson(newDoc);
            		response.setStatus(HttpServletResponse.SC_OK);
            		response.setContentType("application/json");
            		response.setCharacterEncoding("UTF-8");
            		response.getWriter().println(serialized_doc);
                }
    		}catch(SQLException e) {
    			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		    response.setContentType("application/json");
    		    response.setCharacterEncoding("UTF-8");
    		    response.getWriter().println("{\"error\": \"The document can't be created\"}");
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
