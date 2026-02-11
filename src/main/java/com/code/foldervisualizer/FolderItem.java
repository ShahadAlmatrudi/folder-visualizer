package com.code.foldervisualizer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderItem extends FSItem {

    private final Path path;
    private final List<FSItem> children = new ArrayList<>();

    public FolderItem(Path path) {
        super(path.getFileName().toString());
        this.path = path;
    }

    public void addChild(FSItem child) {
        children.add(child);
    }

    public List<FSItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Path getPath() { return path; }

    @Override
    public long calculateSize() {
        long total = 0;
        for (FSItem child : children) {
            total += child.calculateSize();
        }
        this.size = total;
        return size;
    }
}
