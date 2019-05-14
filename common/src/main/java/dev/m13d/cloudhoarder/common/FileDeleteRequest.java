package dev.m13d.cloudhoarder.common;

public class FileDeleteRequest extends AbstractMessage {
    private String fileName;

    public FileDeleteRequest(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
