package it.polimi.tiw.Project.beans;

public class Doc {
    private int documentId;
    private int ownerId;
    private int folderId;
    private String name;
    private String summary;
    private String type;

    public Doc() {
    }

    public Doc(int documentId, int ownerId, int folderId, String name, String summary, String type) {
        this.documentId = documentId;
        this.ownerId = ownerId;
        this.folderId = folderId;
        this.name = name;
        this.summary = summary;
        this.type = type;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", ownerId=" + ownerId +
                ", folderId=" + folderId +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
