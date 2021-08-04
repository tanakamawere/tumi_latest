package org.tmz.tumi.Objects;

public class RequestObject {
    String storyTitle, storyDescription, storyBody, storyKey, storyDate, storyTime, storyWriter, requesterUID, requesterPhone;

    public RequestObject() {
    }

    public RequestObject(String requesterPhone, String storyTitle, String storyDescription, String storyBody, String storyKey, String storyDate, String storyTime, String storyWriter, String requesterUID) {
        this.storyTitle = storyTitle;
        this.storyDescription = storyDescription;
        this.storyBody = storyBody;
        this.storyKey = storyKey;
        this.storyDate = storyDate;
        this.storyTime = storyTime;
        this.storyWriter = storyWriter;
        this.requesterUID = requesterUID;
        this.requesterPhone = requesterPhone;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }

    public String getStoryBody() {
        return storyBody;
    }

    public void setStoryBody(String storyBody) {
        this.storyBody = storyBody;
    }

    public String getStoryKey() {
        return storyKey;
    }

    public void setStoryKey(String storyKey) {
        this.storyKey = storyKey;
    }

    public String getStoryDate() {
        return storyDate;
    }

    public void setStoryDate(String storyDate) {
        this.storyDate = storyDate;
    }

    public String getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(String storyTime) {
        this.storyTime = storyTime;
    }

    public String getStoryWriter() {
        return storyWriter;
    }

    public void setStoryWriter(String storyWriter) {
        this.storyWriter = storyWriter;
    }

    public String getRequesterUID() {
        return requesterUID;
    }

    public void setRequesterUID(String requesterUID) {
        this.requesterUID = requesterUID;
    }

    public String getRequesterPhone() {
        return requesterPhone;
    }
}

