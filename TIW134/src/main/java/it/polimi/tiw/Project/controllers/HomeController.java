//package it.polimi.tiw.Project.controllers;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.util.List;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.WebContext;
//import org.thymeleaf.templatemode.TemplateMode;
//import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
//
//import it.polimi.tiw.Project.beans.User;
//import it.polimi.tiw.Project.dao.FoldersDAO;
//import it.polimi.tiw.Project.utils.ConnectionHandler;
//
//@WebServlet(name = "FolderController", urlPatterns = {"/", "/folder/*", "/subfolder/*"})
//public class HomeController extends HttpServlet{
//	
//	private Connection connection = null;
//	private static final long serialVersionUID = 1L;
//	private TemplateEngine templateEngine;
//
//    @Override
//    public void init() throws ServletException {
//        connection = ConnectionHandler.getConnection(getServletContext());
//        ServletContext servletContext = getServletContext();
//        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        this.templateEngine = new TemplateEngine();
//        this.templateEngine.setTemplateResolver(templateResolver);
//        templateResolver.setSuffix(".html");
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String path = request.getServletPath();
//
//        if (path.equals("/")) {
//            handleHome(request, response);
//        } else if (path.startsWith("/folder/")) {
//            handleFolder(request, response, path.substring(8));
//        } else if (path.startsWith("/subfolder/")) {
//            handleFolder(request, response, path.substring(11));
//        }
//    }
//
//    private void handleHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
//    	HttpSession session = request.getSession(false);
//    	FoldersDAO folder = new FoldersDAO(connection);
//    	User user = (User) session.getAttribute("user");
//    	int userId = user.getId();
//        // Se la sessione non esiste o non contiene l'ID dell'utente, reindirizza al login
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendRedirect("/login");
//            return;
//        }
//        //List<Folder> folders = folder.getFoldersByUserId(userId);
//
//        WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
//        //context.setVariable("folders", folders);
//
//        templateEngine.process("home", context, response.getWriter());
//    }
//
//    private void handleFolder(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
//    	
//        FoldersDAO folder = new FoldersDAO(connection);
//        //search for the folder + id
//        //open related page.html
//        WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
//        context.setVariable("folder", folder);
//
//        templateEngine.process("folder", context, response.getWriter());
//    }
//}
