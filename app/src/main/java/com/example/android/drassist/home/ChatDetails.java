package com.example.android.drassist.home;

public class ChatDetails {
    String chatWith, chatName,chatAddress,chatEmail;
    long chatLatestTime;

    public ChatDetails() {
    }

    public ChatDetails(String chatWith, long chatLatestTime, String chatName, String chatAddress, String chatEmail) {
        this.chatWith = chatWith;
        this.chatLatestTime = chatLatestTime;
        this.chatName = chatName;
        this.chatAddress = chatAddress;
        this.chatEmail = chatEmail;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public long getChatLatestTime() {
        return chatLatestTime;
    }

    public void setChatLatestTime(long chatLatestTime) {
        this.chatLatestTime = chatLatestTime;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatAddress() {
        return chatAddress;
    }

    public void setChatAddress(String chatAddress) {
        this.chatAddress = chatAddress;
    }

    public String getChatEmail() {
        return chatEmail;
    }

    public void setChatEmail(String chatEmail) {
        this.chatEmail = chatEmail;
    }
}
