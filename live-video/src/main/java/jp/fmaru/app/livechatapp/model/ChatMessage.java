package jp.fmaru.app.livechatapp.model;

/**
 * Message model
 * Created by HoangVuNam on 3/23/17.
 */

public class ChatMessage {
    private boolean isMyMessage;
    private String mName;
    private String mMessageContent;

    public ChatMessage(boolean isMyMessage, String name, String messageContent) {
        this.isMyMessage = isMyMessage;
        mName = name;
        mMessageContent = messageContent;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        isMyMessage = myMessage;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMessageContent() {
        return mMessageContent;
    }

    public void setMessageContent(String messageContent) {
        mMessageContent = messageContent;
    }
}
