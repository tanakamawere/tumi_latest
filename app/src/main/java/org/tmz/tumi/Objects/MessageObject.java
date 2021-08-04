package org.tmz.tumi.Objects;

public class MessageObject {
    String messageText, messageKey, date, time, senderKey, recipientKey, senderName;

    public MessageObject() {

    }

    public MessageObject(String messageText, String messageKey, String date, String time, String senderKey, String recipientKey, String senderName) {
        this.messageText = messageText;
        this.messageKey = messageKey;
        this.date = date;
        this.time = time;
        this.senderKey = senderKey;
        this.recipientKey = recipientKey;
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public String getRecipientKey() {
        return recipientKey;
    }
}
