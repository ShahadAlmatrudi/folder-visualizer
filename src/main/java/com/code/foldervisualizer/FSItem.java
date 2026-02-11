package com.code.foldervisualizer;

public abstract class FSItem {
    protected final String name;
    protected long size;

    // shared visualization strategy
    protected static VisualizationStrategy visualizer;

    protected FSItem(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public long getSize() { return size; }

    // each subclass must compute its size
    public abstract long calculateSize();

    // set the strategy from outside
    public static void setVisualizer(VisualizationStrategy v) {
        visualizer = v;
    }

    // single-line call: x.visualize();
    public void visualize() {
        if (visualizer != null) {
            visualizer.visualize(this);
        }
    }
    public static String formatSize(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;

        if (mb >= 1) {
            return String.format("(%.1f MB)", mb);
        } else if (kb >= 1) {
            return String.format("(%.1f KB)", kb);
        } else {
            return "(" + bytes + " B)";
        }
    }

}
