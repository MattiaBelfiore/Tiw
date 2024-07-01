package it.polimi.tiw.Project.beans;


public class Folders {
    private int folderId;
    private int ownerId;
    private String createdAt;
    private String folder_name;
    private Integer parentFolderId; // può essere null
    private int level;
    private String path;

    public Folders() {
    }

    public Folders(int folderId, int ownerId, String name, Integer parentFolderId) {
        this.folderId = folderId;
        this.ownerId = ownerId;
        this.folder_name = name;
        this.parentFolderId = parentFolderId;
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

    public String getName() {
        return folder_name;
    }

    public void setName(String name) {
        this.folder_name = name;
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
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "folderId=" + folderId +
                ", ownerId=" + ownerId +
                ", name='" + folder_name + '\'' +
                ", parentFolderId=" + parentFolderId +
                '}';
    }


}