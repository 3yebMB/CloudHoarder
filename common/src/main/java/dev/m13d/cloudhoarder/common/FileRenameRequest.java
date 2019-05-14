package dev.m13d.cloudhoarder.common;

public class FileRenameRequest extends AbstractMessage {
    private String fileName;
    private String newFileName;

    public String getFileName() {
        return fileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public FileRenameRequest(String fileName, String newFileName) {
        this.fileName = fileName;
        this.newFileName = newFileName;
    }
}
