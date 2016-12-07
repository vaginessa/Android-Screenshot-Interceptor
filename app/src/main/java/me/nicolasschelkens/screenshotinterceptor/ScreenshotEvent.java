package me.nicolasschelkens.screenshotinterceptor;

public class ScreenshotEvent {
    private String path;

    public ScreenshotEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
