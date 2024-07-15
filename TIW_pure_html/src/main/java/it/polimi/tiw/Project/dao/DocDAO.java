package it.polimi.tiw.Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.Project.beans.Doc;

public class DocDAO {

	private Connection con;

	public DocDAO(Connection connection) {
		this.con = connection;
	}
	
	public boolean checkOwner(int ownerId, int documentId) throws SQLException {
		String query = "SELECT document_id "
				+ "FROM folder f INNER JOIN doc d ON f.folder_id = d.folder_id "
				+ "WHERE d.document_id = ? AND f.owner_id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, documentId);
			pstatement.setInt(2, ownerId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				return result.isBeforeFirst();
			}
		}
	}
	
	public Doc getDocById(int ownerId, int docId) throws SQLException {

        String sql = " SELECT d.document_id, d.folder_id, d.doc_name, d.summary, d.created_at, d.type"
                   + " FROM doc d INNER JOIN folder f ON d.folder_id = f.folder_id"
                   + " WHERE d.document_id = ? AND f.owner_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, docId);
        ps.setInt(2, ownerId);

        Doc doc = null;
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setFolderId(rs.getInt("folder_id"));  
                doc.setName(rs.getString("doc_name"));
                doc.setSummary(rs.getString("summary"));
                doc.setType(rs.getString("type"));
                return doc;
            }
        }
        
        return doc;
    }
	
	public List<Doc> getDocsByFolder(int folderId) throws SQLException {
        List<Doc> docs = new ArrayList<>();

        String sql = " SELECT document_id, folder_id, doc_name, summary, created_at, type"
                   + " FROM doc"
                   + " WHERE folder_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, folderId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setFolderId(rs.getInt("folder_id"));  
                doc.setName(rs.getString("doc_name"));
                doc.setSummary(rs.getString("summary"));
                doc.setType(rs.getString("type"));
                docs.add(doc);
            }
        }
        
        return docs;
    }
	
	public void createDoc(int folder_id, String doc_name, String summary, String type) throws SQLException {

		String query = "INSERT into doc (folder_id, doc_name, summary, type) VALUES(?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, folder_id);
			pstatement.setString(2, doc_name);
			pstatement.setString(3, summary);
			pstatement.setString(4, type);
			
			pstatement.executeUpdate();
		}
	}

	public boolean uniqueFile(int ownerId, int folderId, String name) throws SQLException {
		String query = "SELECT document_id "
				+ "FROM doc d INNER JOIN folder f ON d.folder_id = f.folder_id "
				+ "WHERE d.doc_name = ? AND f.owner_id = ? AND d.folder_id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, name);
			pstatement.setInt(2, ownerId);
			pstatement.setInt(3, folderId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				return !result.next();
			}
		}
	}
	
	public boolean moveDoc(int doc_id, int from, int to) throws SQLException {

		String query = "UPDATE doc SET folder_id = ? WHERE document_id = ? AND folder_id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, to);
			pstatement.setInt(2, doc_id);
			pstatement.setInt(3, from);
			
			int rows = pstatement.executeUpdate();
			return rows > 0;
		}
	}
	
	//metodo per aggiungere documenti ad una cartella
	
	//metodo per ottenere tutti i documenti in una cartella
}
