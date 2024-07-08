package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, username, email, name, surname FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
					return user;
				}
			}
		}
	}
	public boolean uniqueUsername(String username) throws SQLException {
		String query = "SELECT username FROM user  WHERE username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // returns true if a user with the given username is not found
					return true;
				else {
					return false;
				}
			}
		}
	}
	public void createUser(String username, String name, String surname, String email, String pw) throws SQLException {

		String query = "INSERT into user (username, email, password, name, surname) VALUES(?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, email);
			pstatement.setString(3, pw);
			pstatement.setString(4, name);
			pstatement.setString(5, surname);
			pstatement.executeUpdate();
		}
	}
}

