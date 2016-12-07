package me.nicolasschelkens.screenshotinterceptor.api.model;

import me.nicolasschelkens.screenshotinterceptor.Screenshot;

public class ImageResponse {
    private boolean success;
    private int status;
    private Screenshot data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Screenshot getData() {
        return data;
    }

    public void setData(Screenshot data) {
        this.data = data;
    }
}
