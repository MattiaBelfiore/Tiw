package it.polimi.tiw.Project.controllers;

import java.io.IOException;    
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.Project.dao.UserDAO;
import it.polimi.tiw.Project.utils.ConnectionHandler;

@WebServlet("/RegistrationAgent")
public class RegistrationAgent extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	     
	public RegistrationAgent() {
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = "/WEB-INF/Register.html";
		resp.setContentType("text");
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
		templateEngine.process(path, webContext, resp.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*
		 * controlliamo i parametri della request
		 * 1. estrarre i parametri della request
		 * 2. controllare che non siano null or empty e che siano della dimensione giusta
		 * 3. controllare che username sia unico (effettuare la query del db e verificare che il cursore sia prima del primo)
		 * 4. controllare che pw = ripeti pw
		 * 5. creare utente oppure mandare errore
		 */
		boolean isBadRequest = false;
		boolean userEsistente = false;
		boolean lunghezzaCampiEsagerata = false;
		boolean emailScorretta = false;
		String username = null;
		String name = null;
		String surname = null;
		String email = null;
		String pwd = null;
		String repeat_pwd = null;
		try {
			/*estraggo i parametri dalla request, se sono nulli vengono mandate eccezioni*/
			username = request.getParameter("username");
			name = request.getParameter("name");
			surname = request.getParameter("surname");
			email = request.getParameter("email");
			pwd = request.getParameter("password");
			repeat_pwd = request.getParameter("repeat_password");
			
			/*Controllo la correttezza della mail*/			
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(email);
			boolean matchFound = m.matches();

			if(!matchFound) {
				isBadRequest = true;
				emailScorretta = true;
			}
			
			/*Controllo che password e ripeti password siano uguali*/
			if(!pwd.equals(repeat_pwd)) {
				isBadRequest = true;
			}
			
			
			/*Controllo che username e password siano al pi� di 20 caratteri*/
			if(username.length()>15 || pwd.length()>15) {
				isBadRequest = true;
				lunghezzaCampiEsagerata = true;
			}
			
			
		} catch (NumberFormatException | NullPointerException e) { /*Le eccezioni vengono lanciate se ci sono campi con formati errati o non compilati*/
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile estrarre i parametri della form");
		}
		
		try { /*Controllo che nel DB non esista gi� un utente con lo stesso username*/
			UserDAO userDAO = new UserDAO(connection);
			if(false == userDAO.uniqueUsername(username)) {
				isBadRequest = true;
				userEsistente = true;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se il nome utente esiste gi�");
		}
			
		
		/*se isBadRequest � a true vuol dire che i parametri non sono corretti in quanto o si � cercato di inviare la form con dei campi mancanti
		 *  oppure pw e ripeti_pw non sono uguali
		*/
		if(isBadRequest) { /*C'� qualche errore nella compilazione della form */
			String path;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			/*controllo i boolean in ordine di importanza del tipo di errore per vedere qual � l'errore e stampare a video il giusto messaggio di errore
			 * (il primo per importanza)*/
			if(userEsistente) {
				ctx.setVariable("errorMsg", "Username già esistente.");
			}
			else if(lunghezzaCampiEsagerata) {
				ctx.setVariable("errorMsg", "Lunghezza massima per i campi Username e Password di 15 caratteri.");
			}
			else if(emailScorretta) {
				ctx.setVariable("errorMsg", "Email sintatticamente sbagliata");
			}
			else {
				ctx.setVariable("errorMsg", "Le due password non corrispondono.");
			}
			
			path ="/WEB-INF/Register.html";
			templateEngine.process(path, ctx, response.getWriter());
		}
		else
		{	
			/* Creo Utente in db (ho gi� controllato prima (righe 93-98) che non ci siano gi� altri utenti con lo stesso username nel DB)
			 * Se entra nell'else vuol dire che � tutto a posto e posso creare l'utente nel DB senza fare ulteriori controlli.
			 */
			UserDAO userDAO = new UserDAO(connection);
			try {
					userDAO.createUser(username, name, surname, email, pwd);
					
					response.sendRedirect("CheckLogin");
				
			} catch (SQLException e) { /*la creatwUser pu� lanciare SQL exception in caso di errore del server che non riesce a fare la insert nel DB, in tal caso devo dare messaggio di internal error server*/
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare l'Utente");
			}
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
