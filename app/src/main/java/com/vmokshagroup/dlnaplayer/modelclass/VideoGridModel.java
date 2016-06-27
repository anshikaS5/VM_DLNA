package com.vmokshagroup.dlnaplayer.modelclass;

import android.graphics.Bitmap;

/**
 * Created by mehtermu on 22-04-2016.
 */
public class VideoGridModel {

    private int icon;
    private String title;
    private String description;
    private String description2;
    private String iconUrl;
    private Boolean hideIcon;
    private String ItemsValue;
    private String fileIcon;
    private String duration;
    private Bitmap imagebitmap;

    public VideoGridModel(int icon, String title, String description, String description2, String iconUrl, Boolean hideIcon, String itemsValue, String fileIcon) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.description2 = description2;
        this.iconUrl = iconUrl;
        this.hideIcon = hideIcon;
        ItemsValue = itemsValue;
        this.fileIcon = fileIcon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Boolean getHideIcon() {
        return hideIcon;
    }

    public void setHideIcon(Boolean hideIcon) {
        this.hideIcon = hideIcon;
    }

    public String getItemsValue() {
        return ItemsValue;
    }

    public void setItemsValue(String itemsValue) {
        ItemsValue = itemsValue;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Bitmap getImagebitmap() {
        return imagebitmap;
    }

    public void setImagebitmap(Bitmap imagebitmap) {
        this.imagebitmap = imagebitmap;
    }
}
