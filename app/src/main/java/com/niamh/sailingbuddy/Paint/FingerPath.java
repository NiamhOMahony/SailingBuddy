package com.niamh.sailingbuddy.Paint;

import android.graphics.Path;

//adapted from world of codings - Android PaintApp in 5 min android studio tutorial https://www.youtube.com/watch?v=s5WtcJ-gE3M
//

public class FingerPath {

    // delaring variables for painting activity
    private int color;
    private int strokeWidth;
    private Path path;

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    //getters and setters
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
