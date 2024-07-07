package it.polimi.tiw.Project.beans;

import java.util.ArrayList;
import java.util.List;

public class Folder {
    private int folderId;
    private int ownerId;
    private String createdAt;
    private String folderName;
    private Integer parentFolderId; // può essere null
    private List<Folder> children;

    public Folder() {
    	this.children = new ArrayList<>();
    }

    public Folder(int folderId, int ownerId, String name, Integer parentFolderId) {
        this.folderId = folderId;
        this.ownerId = ownerId;
        this.folderName = name;
        this.parentFolderId = parentFolderId;
        this.children = new ArrayList<>();
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String name) {
        this.folderName = name;
    }

    public Integer getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(Integer parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Folder> getChildren() {
    	return children;
    }
    
    public void setChildren(List<Folder> children) {
        this.children = children;
    }
    
    public void addChild(Folder folder) {
    	this.children.add(folder);
    }
    
    @Override
    public String toString() {
        return "Folder{" +
                "folderId=" + folderId +
                ", ownerId=" + ownerId +
                ", name='" + folderName + '\'' +
                ", parentFolderId=" + parentFolderId +
                ", children=" + String.join(",", children.stream().map(c -> c.folderName).toList()) +
                '}';
    }


}