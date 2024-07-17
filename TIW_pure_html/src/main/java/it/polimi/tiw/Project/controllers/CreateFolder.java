package it.polimi.tiw.Project.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.Project.beans.User;
import it.polimi.tiw.Project.dao.FolderDAO;
import it.polimi.tiw.Project.utils.ConnectionHandler;

@WebServlet("/createFolder")
public class CreateFolder extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ownerId = 0, parentId = 0;
        String foldername = null;
        HttpSession session = request.getSession(false);

        try {
            // Estrai i parametri dalla request
            User user = (User) session.getAttribute("user");
            ownerId = user.getId();
            foldername = request.getParameter("folderName");
            parentId = Integer.parseInt(request.getParameter("parentFolder"));
            
            // Aggiungi controlli sui parametri
            if (foldername == null || foldername.isEmpty()) {
                throw new IllegalArgumentException("Nome cartella non valido");
            }
            //controllo che non esistano altre folders con lo stesso nome
            try { 
    			FolderDAO folderDAO = new FolderDAO(connection);
    			if(!folderDAO.checkOwner(ownerId, parentId)) {
    				String errorMsg = "Unauthorized";
    				response.sendRedirect("GoToContentManagement?errorMsgF=" + URLEncoder.encode(errorMsg, "UTF-8"));
    				return;
    			}
    			
    			if(!folderDAO.uniqueFolder(ownerId , parentId, foldername)) {
    				String errorMsg = "A folder with the same name already exists";
    				response.sendRedirect("GoToContentManagement?errorMsgF=" + URLEncoder.encode(errorMsg, "UTF-8"));
    			} else {
                    folderDAO.createFolder(ownerId, foldername, parentId);
                    response.sendRedirect("GoToHome");
                }
    		}catch(SQLException e) {
    			String errorMsg = "Impossibile controllare se il nome utente esiste già";
    			response.sendRedirect("GoToContentManagement?errorMsgF=" + URLEncoder.encode(errorMsg, "UTF-8"));
    		}

        } catch (NumberFormatException | NullPointerException e) {
        	String errorMsg = "Impossibile estrarre i parametri della form";
			response.sendRedirect("GoToContentManagement?errorMsgF=" + URLEncoder.encode(errorMsg, "UTF-8"));
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
