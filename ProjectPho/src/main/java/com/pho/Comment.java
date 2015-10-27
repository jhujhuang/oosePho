package com.pho;

/**
 * A Comment on a photo in the edit interface.
 */
public class Comment {
    private String content;
    private String author;
    private String createdTime;

    /**
     * Creates a Comment instance.
     * @param content the actual content that the user has entered.
     * @param author the userId of the author.
     * @param createdTime string representing the time of comment.
     */
    public Comment(String content, String author, String createdTime) {
        this.content = content;
        this.author = author;
        this.createdTime = createdTime;
    }

    /**
     * Retrieves the content of the comment.
     * @return string of the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Retrieves the author of the comment.
     * @return string of user id of the author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Retrieves the created time of the comment.
     * @return string of the time that the comment was created.
     */
    public String getCreatedTime() {
        return this.createdTime;
    }

}
