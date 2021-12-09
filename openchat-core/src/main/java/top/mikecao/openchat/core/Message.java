package top.mikecao.openchat.core;

public class Message {

    private byte[] body;
    private Headers headers;

    public Message(Headers headers, byte[] body) {
        this.body = body;
        this.headers = headers;
    }

    public byte[] body(){
        return body;
    }
    public Headers headers(){
        return headers;
    }
}
