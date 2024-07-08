package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import utils.ConnectionHandler;

@WebServlet("/RegistrationAgent")
@MultipartConfig

public class RegistrationAgent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request , response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtain and escape params
		String username = request.getParameter("username");
		String pwd = request.getParameter("password");
		String repeat_pwd = request.getParameter("rptpassword");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		if (email == null || email.isEmpty()) {
		    // Gestisci il caso in cui l'email sia mancante o vuota
		    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email non fornita.");
		    return;
		}
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();

		boolean isBadRequest = false, emailScorretta = false, lunghezzaCampiEsagerata = false, userEsistente = false;
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

	
		try { /*Controllo che nel DB non esista gi� un utente con lo stesso username*/
			UserDAO userDAO = new UserDAO(connection);
			if(false == userDAO.uniqueUsername(username)) {
				isBadRequest = true;
				userEsistente = true;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se il nome utente esiste gi�");
		}
		
		if(isBadRequest) { /*C'� qualche errore nella compilazione della form */		
			/*controllo i boolean in ordine di importanza del tipo di errore per vedere qual � l'errore e stampare a video il giusto messaggio di errore
			 * (il primo per importanza)*/
			if(userEsistente) {
				response.getWriter().println("Username già esistente.");
			}
			else if(lunghezzaCampiEsagerata) {
				response.getWriter().println("Lunghezza massima per i campi Username e Password di 15 caratteri.");
			}
			else if(emailScorretta) {
				response.getWriter().println("Email sintatticamente sbagliata");
			}
			else {
				response.getWriter().println("Le due password non corrispondono.");
			}
		}
		else {
			// Save the user in the database
			UserDAO userDao = new UserDAO(connection);
			try {
				userDao.createUser(username, name, surname, email, pwd);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed.");
				return;
			}
			
			try {
				userDao.createUser(username, name, surname, email, pwd);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed.");
				return;
			}

			// Respond with success message
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("User registered successfully.");
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