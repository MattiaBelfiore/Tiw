package it.polimi.tiw.Project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.Project.beans.Folder;
import it.polimi.tiw.Project.beans.User;
import it.polimi.tiw.Project.dao.FolderDAO;
import it.polimi.tiw.Project.utils.ConnectionHandler;

//@WebServlet(name = "FolderController", urlPatterns = {"/", "/folder/*", "/subfolder/*"})
@WebServlet("/GoToContentManagement")
public class GoToContentManagement extends HttpServlet{
	
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
    	String path = request.getServletPath();
        FolderDAO folder = new FolderDAO(connection);
        
    	User user = (User) session.getAttribute("user");
    	int userId = user.getId();
        
        String errorMsgRF = request.getParameter("errorMsgRF");
        String errorMsgF = request.getParameter("errorMsgF");
        String errorMsgD = request.getParameter("errorMsgD");
        
        List<Folder> folders = new ArrayList<Folder>();
		try {
			folders = folder.getFoldersByOwner(userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        path ="/WEB-INF/GestioneContenuti.html";
        WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
        context.setVariable("folders", folders);
        context.setVariable("errorMsgRF", errorMsgRF);
        context.setVariable("errorMsgF", errorMsgF);
        context.setVariable("errorMsgD", errorMsgD);
        templateEngine.process(path, context, response.getWriter());
    }
}
