package dev.m13d.cloudhoarder.common;

public class FileRequest extends AbstractMessage {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }
}
