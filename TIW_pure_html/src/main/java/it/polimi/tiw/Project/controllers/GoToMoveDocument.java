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

@WebServlet("/GoToMoveDocument")
public class GoToMoveDocument extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	public GoToMoveDocument() {
        super();
    }
	
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		HttpSession session = req.getSession(false);
    	FolderDAO folder = new FolderDAO(connection);
    	User user = (User) session.getAttribute("user");
    	int userId = user.getId();
        
        int currentFolderId = Integer.parseInt(req.getParameter("folderId"));
        int docId = Integer.parseInt(req.getParameter("docId"));
        String docName = req.getParameter("docName");
        
        List<Folder> folders = new ArrayList<Folder>();
		try {
			folders = folder.getRootFoldersByOwner(userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Folder fromFolder = null;
		try {
			fromFolder = folder.getFolderByOwner(userId, currentFolderId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fromFolder == null)
			resp.sendRedirect("GoToHome?errorMsg=Invalid source folder"); 
		

        String path ="/WEB-INF/Home.html";
        WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());
        context.setVariable("folders", folders);
        context.setVariable("selectingDestination", true);
        context.setVariable("docId", docId);
        context.setVariable("docName", docName);
        context.setVariable("from", currentFolderId);
        context.setVariable("folderName", fromFolder.getFolderName());
        templateEngine.process(path, context, resp.getWriter());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
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
