package it.polimi.tiw.Project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.Project.beans.Folders;

public class FoldersDAO {
	
	private Connection con;

	public FoldersDAO(Connection connection) {
		this.con = connection;
	}

	public List<Folders> getFoldersByOwner(int ownerId) throws SQLException {
        List<Folders> folders = new ArrayList<>();

        String sql = "WITH RECURSIVE FolderHierarchy AS ("
                   + " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id, 0 AS level, CAST(folder_id AS CHAR(255)) AS path"
                   + " FROM folders"
                   + " WHERE owner_id = ? AND parent_folder_id IS NULL"
                   + " UNION ALL"
                   + " SELECT f.folder_id, f.owner_id, f.folder_name, f.created_at, f.parent_folder_id, fh.level + 1 AS level, CONCAT(fh.path, ' > ', f.folder_id) AS path"
                   + " FROM folders f"
                   + " INNER JOIN FolderHierarchy fh ON f.parent_folder_id = fh.folder_id"
                   + ")"
                   + " SELECT folder_id, owner_id, folder_name, created_at, parent_folder_id, level, path"
                   + " FROM FolderHierarchy"
                   + " ORDER BY path";

        PreparedStatement ps = con.prepareStatement(sql); 

        ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Folders folder = new Folders();
                    folder.setFolderId(rs.getInt("folder_id"));
                    folder.setOwnerId(rs.getInt("owner_id"));
                    folder.setName(rs.getString("folder_name"));
                    folder.setCreatedAt(rs.getString("created_at"));
                    folder.setParentFolderId(rs.getObject("parent_folder_id", Integer.class));
                    folder.setLevel(rs.getInt("level"));
                    folder.setPath(rs.getString("path"));
                    folders.add(folder);
                }
            }

        return folders;
    }
	
	//metodo per aggiungere cartelle padre
	
	//metodo per aggiungere cartelle di secondo livello

}
