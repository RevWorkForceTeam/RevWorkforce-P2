package com.rev.revworkforcep2.dto.request.announcement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAnnouncementRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    // Default Constructor
    public CreateAnnouncementRequest() {
    }

    // Parameterized Constructor
    public CreateAnnouncementRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }
}