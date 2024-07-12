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
		String repeat_pwd = request.getParameter("repeat_password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		
		if (email == null || email.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Email must be not null");
		    return;
		}
		
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();

		if(!matchFound) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Email is invalid");
		    return;
		}
		
		/*Controllo che password e ripeti password siano uguali*/
		if(!pwd.equals(repeat_pwd)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Passwords do not match.");
		    return;
		}
		
		
		/*Controllo che username e password siano al pi� di 20 caratteri*/
		if(username.length()>15 || pwd.length()>15) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Username or Passwords must be max 15 characters.");
			return;
		}

	
		try { /*Controllo che nel DB non esista gi� un utente con lo stesso username*/
			UserDAO userDAO = new UserDAO(connection);
			if(false == userDAO.uniqueUsername(username)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Username already used.");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare se il nome utente esiste gi�");
		}
		
		// Save the user in the database
		UserDAO userDao = new UserDAO(connection);
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

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}