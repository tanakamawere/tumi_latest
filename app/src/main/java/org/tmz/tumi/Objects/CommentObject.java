package org.tmz.tumi.Objects;

public class CommentObject {
    String text, key, date, time, commenter;

    public CommentObject(String text, String key, String date, String time, String commenter) {
        this.text = text;
        this.key = key;
        this.date = date;
        this.time = time;
        this.commenter = commenter;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCommenter() {
        return commenter;
    }
}
