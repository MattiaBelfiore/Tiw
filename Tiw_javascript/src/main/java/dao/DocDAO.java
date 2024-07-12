package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Doc;
import beans.Folder;

public class DocDAO {

	private Connection con;

	public DocDAO(Connection connection) {
		this.con = connection;
	}
	
	public Doc getDocById(int docId) throws SQLException {

        String sql = " SELECT document_id, owner_id, folder_id, doc_name, summary, created_at, type"
                   + " FROM doc"
                   + " WHERE document_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, docId);

        Doc doc = null;
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setOwnerId(rs.getInt("owner_id"));
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

        String sql = " SELECT document_id, owner_id, folder_id, doc_name, summary, created_at, type"
                   + " FROM doc"
                   + " WHERE folder_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, folderId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setOwnerId(rs.getInt("owner_id"));
                doc.setFolderId(rs.getInt("folder_id"));  
                doc.setName(rs.getString("doc_name"));
                doc.setSummary(rs.getString("summary"));
                doc.setType(rs.getString("type"));
                docs.add(doc);
            }
        }
        
        return docs;
    }
	
	public List<Doc> getDocsByOwner(int ownerId) throws SQLException {
        List<Doc> docs = new ArrayList<>();

        String sql = " SELECT document_id, owner_id, folder_id, doc_name, summary, created_at, type"
                   + " FROM doc"
                   + " WHERE owner_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, ownerId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doc doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setOwnerId(rs.getInt("owner_id"));
                doc.setFolderId(rs.getInt("folder_id"));  
                doc.setName(rs.getString("doc_name"));
                doc.setSummary(rs.getString("summary"));
                doc.setType(rs.getString("type"));
                docs.add(doc);
            }
        }
        
        return docs;
    }
	
	public Doc createDoc(int owner_id, int folder_id, String doc_name, String summary, String type) throws SQLException {

		String query = "INSERT into doc (owner_id, folder_id, doc_name, summary, type) VALUES(?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, owner_id);
			pstatement.setInt(2, folder_id);
			pstatement.setString(3, doc_name);
			pstatement.setString(4, summary);
			pstatement.setString(5, type);
			
			pstatement.executeUpdate();
		}
		
		String query2 = "SELECT document_id, owner_id, folder_id, doc_name, summary, created_at, type" + " FROM doc"
				+ " WHERE owner_id = ? AND doc_name = ? AND folder_id = ?";
		
		PreparedStatement pstatement = con.prepareStatement(query2);
		
		pstatement.setInt(1, owner_id);
		pstatement.setString(2, doc_name);
		pstatement.setInt(3, folder_id);
		
		try (ResultSet rs = pstatement.executeQuery()) {
			while (rs.next()) {
				Doc doc = new Doc();
                doc.setDocumentId(rs.getInt("document_id"));
                doc.setOwnerId(rs.getInt("owner_id"));
                doc.setFolderId(rs.getInt("folder_id"));  
                doc.setName(rs.getString("doc_name"));
                doc.setSummary(rs.getString("summary"));
                doc.setType(rs.getString("type"));
				return doc;
			}
		}
		return null;
	}

	public boolean uniqueFile(int ownerId, int folderId, String name) throws SQLException {
		String query = "SELECT document_id FROM doc WHERE doc_name = ? AND owner_id = ? AND folder_id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, name);
			pstatement.setInt(2, ownerId);
			pstatement.setInt(3, folderId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				return !result.next();
			}
		}
	}
	
	public void moveDoc(int owner_id, int doc_id, int to) throws SQLException {

		String query = "UPDATE doc SET folder_id = ? WHERE owner_id = ? AND document_id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, to);
			pstatement.setInt(2, owner_id);
			pstatement.setInt(3, doc_id);
			
			pstatement.executeUpdate();
		}
	}

	public boolean removeDoc(int docId) {
		
		String query = "DELETE FROM doc WHERE document_id = ?";

        PreparedStatement preparedStatement;

        try{
            preparedStatement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, Integer.toString(docId));

           preparedStatement.executeUpdate();
           
           return true;
        } catch (SQLException e) {
            System.out.println("Bad prepared statement requesting file in DocDao.java updatePosition()");
            e.printStackTrace();
        }
		return false;
	}
}
