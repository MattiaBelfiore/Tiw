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

import it.polimi.tiw.Project.dao.FolderDAO;
import it.polimi.tiw.Project.beans.User;
import it.polimi.tiw.Project.dao.DocDAO;
import it.polimi.tiw.Project.utils.ConnectionHandler;

@WebServlet("/MoveDocument")
public class MoveDocument extends HttpServlet{
	
	private Connection connection = null;
	private static final long serialVersionUID = 1L;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
    	
    	User user = (User) session.getAttribute("user");
    	int userId = user.getId();
    	
        // Se la sessione non esiste o non contiene l'ID dell'utente, reindirizza al login
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/CheckLogin");
            return;
        }
        
        int docId = Integer.parseInt(request.getParameter("docId"));
        String docName = request.getParameter("docName");
        int from = Integer.parseInt(request.getParameter("from"));
        int to = Integer.parseInt(request.getParameter("to"));

        DocDAO docdao = new DocDAO(connection);
        try {
        	FolderDAO folderdao = new FolderDAO(connection);
        	String id = Integer.toString(to);
        	if(!folderdao.checkOwner(userId, to) || !folderdao.checkOwner(userId, from)) {
        		String errorMsg = "Invalid folder";
        		response.sendRedirect("GoToContents?errorMsg=" + URLEncoder.encode(errorMsg, "UTF-8") + "&id=" + URLEncoder.encode(id, "UTF-8"));
		        return;
			}
        	
        	if(!docdao.uniqueFile(userId , to, docName)) {
        		String errorMsg = "A document with the same name already exists";
				response.sendRedirect("GoToContents?errorMsg=" + URLEncoder.encode(errorMsg, "UTF-8") + "&id=" + URLEncoder.encode(id, "UTF-8"));
			} else {
				if(docdao.moveDoc(docId,from,to)) {
					String successMsg = "Document successfully moved";
					response.sendRedirect("GoToContents?successMsg=" + URLEncoder.encode(successMsg, "UTF-8") + "&id=" + URLEncoder.encode(id, "UTF-8"));
				} else {
					String errorMsg = "Unauthorized move";
					response.sendRedirect("GoToContents?errorMsg=" + URLEncoder.encode(errorMsg, "UTF-8") + "&id=" + URLEncoder.encode(id, "UTF-8"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } 
}
