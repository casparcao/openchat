package top.mikecao.openchat.core;

public enum HKEY {

    EVENT("EVENT", "消息事件，如登录，广播，点对点"),
    BODYTYPE("BODYTYPE", "消息体类型，如文本，文件"),
    FROM("FROM", "发送人"),
    TO("TO", "接收人"),
    TOKEN("TOKEN", "令牌，除了登录类型之外的其他消息类型，必须携带令牌"),
    UNAME("UNAME", "用户名，用于登录"),
    PASSWD("PASSWD", "密码，用于登录"),
    MSGID("MSGID", "消息id，每条消息都有唯一id标识");

    private String ID;
    private String desc;

    HKEY(String name, String desc){
        this.ID = name;
        this.desc = desc;
    }
    public String getID() {
        return ID;
    }
}
