package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Folder;

public class FolderDAO {
	
	private Connection con;

	public FolderDAO(Connection connection) {
		this.con = connection;
	}

	public List<Folder> getRootFoldersByOwner(int ownerId) throws SQLException {
        List<Folder> folders = new ArrayList<>();

        String sql = " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id"
                   + " FROM folder"
                   + " WHERE owner_id = ?"
                   + " ORDER BY folder_id";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, ownerId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Folder folder = new Folder();
                folder.setFolderId(rs.getInt("folder_id"));
                folder.setOwnerId(rs.getInt("owner_id"));
                folder.setFolderName(rs.getString("folder_name"));
                folder.setCreatedAt(rs.getString("created_at"));
                folder.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
                folders.add(folder);
            }
        }
        
        Map<Integer, Folder> folderMap = new HashMap<>();
        List<Folder> rootFolders = new ArrayList<>();

        // Costruisci la mappa delle cartelle e identifica le cartelle root
        for (Folder folder : folders) {
            folderMap.put(folder.getFolderId(), folder);
            if (folder.getParentFolderId() == null) {
                rootFolders.add(folder);
            }
        }

        // Assegna le sottocartelle alle rispettive cartelle padre
        for (Folder folder : folders) {
            if (folder.getParentFolderId() != null) {
                Folder parentFolder = folderMap.get(folder.getParentFolderId());
                if (parentFolder != null) {
                    parentFolder.addChild(folder);
                }
            }
        }

        return rootFolders;
    }
	
	public List<Folder> getFoldersByOwner(int ownerId) throws SQLException {
        List<Folder> folders = new ArrayList<>();

        String sql = " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id"
                   + " FROM folder"
                   + " WHERE owner_id = ?";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, ownerId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Folder folder = new Folder();
                folder.setFolderId(rs.getInt("folder_id"));
                folder.setOwnerId(rs.getInt("owner_id"));
                folder.setFolderName(rs.getString("folder_name"));
                folder.setCreatedAt(rs.getString("created_at"));
                folder.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
                folders.add(folder);
            }
        }
        
        return folders;
    }
	
	public Folder getFolderByOwner(int ownerId, int folderId) throws SQLException {
		String sql = " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id"
		       + " FROM folder"
		       + " WHERE owner_id = ? AND folder_id = ?";
		
		PreparedStatement ps = con.prepareStatement(sql); 
		
		ps.setInt(1, ownerId);
		ps.setInt(2, folderId);
		
		Folder folder = null;
		try (ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				folder = new Folder();
				folder.setFolderId(rs.getInt("folder_id"));
				folder.setOwnerId(rs.getInt("owner_id"));
				folder.setFolderName(rs.getString("folder_name"));
				folder.setCreatedAt(rs.getString("created_at"));
				folder.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
				break;
			}
		}
		
		if(folder == null) {
			return folder;
		}
		
		String sql2 = " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id" + " FROM folder"
				+ " WHERE owner_id = ? AND parent_folder_id = ?";

		PreparedStatement ps2 = con.prepareStatement(sql2);

		ps2.setInt(1, ownerId);
		ps2.setInt(2, folderId);

		try (ResultSet rs = ps2.executeQuery()) {
			while (rs.next()) {
				Folder f = new Folder();
				f.setFolderId(rs.getInt("folder_id"));
				f.setOwnerId(rs.getInt("owner_id"));
				f.setFolderName(rs.getString("folder_name"));
				f.setCreatedAt(rs.getString("created_at"));
				f.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
				folder.addChild(f);
			}
		}

        return folder;
    }

	public void createRootFolder(int ownerId, String foldername) throws SQLException {
		String query = "INSERT into folder (owner_id, folder_name) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, ownerId);
			pstatement.setString(2, foldername);
			
			pstatement.executeUpdate();
		}
		
	}
	
	public Folder createFolder(int ownerId, String foldername, int parentId) throws SQLException {
		String query = "INSERT into folder (owner_id, folder_name, parent_folder_id) VALUES(?, ?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, ownerId);
			pstatement.setString(2, foldername);
			pstatement.setInt(3, parentId);
			pstatement.executeUpdate();
		}
		
		String query2 = "SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id" + " FROM folder"
				+ " WHERE owner_id = ? AND folder_name = ? AND parent_folder_id = ?";
		
		PreparedStatement pstatement = con.prepareStatement(query2);
		
		pstatement.setInt(1, ownerId);
		pstatement.setString(2, foldername);
		pstatement.setInt(3, parentId);
		
		try (ResultSet rs = pstatement.executeQuery()) {
			while (rs.next()) {
				Folder f = new Folder();
				f.setFolderId(rs.getInt("folder_id"));
				f.setOwnerId(rs.getInt("owner_id"));
				f.setFolderName(rs.getString("folder_name"));
				f.setCreatedAt(rs.getString("created_at"));
				f.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
				return f;
			}
		}
		return null;
	}

	public boolean uniqueRoot(int ownerId, String foldername) throws SQLException {
		String query = "SELECT folder_name FROM folder WHERE folder_name = ? AND owner_id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, foldername);
			pstatement.setInt(2, ownerId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return true;
				else {
					return false;
				}
			}
		}
	}

	public boolean uniqueFolder(int ownerId, int parentId, String foldername) throws SQLException {
		String query = "SELECT folder_name FROM folder WHERE folder_name = ? AND owner_id = ? AND parent_folder_id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, foldername);
			pstatement.setInt(2, ownerId);
			pstatement.setInt(3, parentId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return true;
				else {
					return false;
				}
			}
		}
	}
	

}
