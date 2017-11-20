package hello;

public class MessageToServer {

    private String message, channel;

    public MessageToServer() {
    }

    public MessageToServer(String name) {
        this.message = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
