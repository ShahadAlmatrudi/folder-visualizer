package com.code.foldervisualizer;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileItem extends FSItem {

    private final Path path;
    private final String extension;

    public FileItem(Path path) {
        super(path.getFileName().toString());
        this.path = path;

        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        this.extension = (dot >= 0 ? fileName.substring(dot + 1) : "");
    }

    @Override
    public long calculateSize() {
        try {
            size = Files.size(path);
        } catch (Exception e) {
            size = 0;
        }
        return size;
    }

    public String getExtension() { return extension; }

    public Path getPath() { return path; }
}
