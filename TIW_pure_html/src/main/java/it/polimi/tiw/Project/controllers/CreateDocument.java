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
import it.polimi.tiw.Project.dao.DocDAO;
import it.polimi.tiw.Project.dao.FolderDAO;
import it.polimi.tiw.Project.utils.ConnectionHandler;

@WebServlet("/CreateDocument")
public class CreateDocument extends HttpServlet {
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
        int ownerId = 0, folderId = 0;
        String summary = null, name = null , type = null;
        HttpSession session = request.getSession(false);

        try {
            // Estrai i parametri dalla request
            User user = (User) session.getAttribute("user");
            ownerId = user.getId();
            name = request.getParameter("docName");
            summary = request.getParameter("summary");
            type = request.getParameter("type");
            folderId = Integer.parseInt(request.getParameter("parentFolderDoc"));

            // controllo che siano valorizzati tutti i parametri
            if (name == null || name.isEmpty() || summary == null || summary.isEmpty() || type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Parametri non validi");
            }
            
            try { 
            	FolderDAO folderdao = new FolderDAO(connection);
    			if(!folderdao.checkOwner(ownerId, folderId)) {
    				String errorMsg = "Unauthorized";
    				response.sendRedirect("GoToContentManagement?errorMsgD=" + URLEncoder.encode(errorMsg, "UTF-8"));
    				return;
    			}
    			
    			DocDAO docDAO = new DocDAO(connection);
    			if(!docDAO.uniqueFile(ownerId , folderId, name)) {
    				String errorMsg = "A document with the same name already exists";
    				response.sendRedirect("GoToContentManagement?errorMsgD=" + URLEncoder.encode(errorMsg, "UTF-8"));
    			}
                else {
                	docDAO.createDoc(folderId, name, summary, type);
                	response.sendRedirect("GoToHome");
                }
    		}catch(SQLException e) {
    			String errorMsg = "Impossibile creare il documento";
				response.sendRedirect("GoToContentManagement?errorMsgD=" + URLEncoder.encode(errorMsg, "UTF-8"));
    		}
    
        } catch (NumberFormatException | NullPointerException e) {
        	String errorMsg = "Impossibile estrarre i parametri della form";
			response.sendRedirect("GoToContentManagement?errorMsgD=" + URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (IllegalArgumentException e) {
        	String errorMsg = e.getMessage();
			response.sendRedirect("GoToContentManagement?errorMsgD=" + URLEncoder.encode(errorMsg, "UTF-8"));
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
