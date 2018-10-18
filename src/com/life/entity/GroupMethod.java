package com.life.entity;

import com.google.gson.Gson;

public class GroupMethod {
    /**
     * fromUserId	String	发送人用户 Id 。（必传）
     toGroupId	String	接收群 Id，提供多个本参数可以实现向多群发送消息，最多不超过 3 个群组。（必传）
     toUserId	String	群定向消息功能，向群中指定的一个或多个用户发送消息，群中其他用户无法收到该消息，当 toGroupId 为一个群组时此参数有效。注：如果开通了“单群聊消息云存储”功能，群定向消息不会存储到云端，向群中部分用户发送消息阅读状态回执时可使用此功能。（可选）
     objectName	String	消息类型，参考融云消息类型表.消息标志；可自定义消息类型，长度不超过 32 个字符，您在自定义消息时需要注意，不要以 "RC:" 开头，以避免与融云系统内置消息的 ObjectName 重名。（必传）
     content	String	发送消息内容，参考融云消息类型表.示例说明；如果 objectName 为自定义消息类型，该参数可自定义格式。（必传）
     pushContent	String	定义显示的 Push 内容，如果 objectName 为融云内置消息类型时，则发送后用户一定会收到 Push 信息。 如果为自定义消息，则 pushContent 为自定义消息显示的 Push 内容，如果不传则用户不会收到 Push 通知。(可选)
     pushData	String	针对 iOS 平台为 Push 通知时附加到 payload 中，Android 客户端收到推送消息时对应字段名为 pushData。(可选)
     isPersisted	Int	针对用户当前使用的客户端版本，如果没有对应 objectName 赋值的消息类型时，客户端收到消息后是否进行存储，0 表示为不存储、 1 表示为存储，默认为 1 存储消息。(可选)
     isCounted	Int	针对用户当前使用的客户端版本，如果没有对应 objectName 赋值的消息类型时，客户端收到消息后是否进行未读消息计数，0 表示为不计数、 1 表示为计数，默认为 1 计数，未读消息数增加 1。(可选)
     isIncludeSender	Int	发送用户自己是否接收消息，0 表示为不接收，1 表示为接收，默认为 0 不接收。（可选）
     isMentioned	Int	是否为 @消息，0 表示为普通消息，1 表示为 @消息，默认为 0。当为 1 时 content 参数中必须携带 mentionedInfo @消息的详细内容。为 0 时则不需要携带 mentionedInfo。当指定了 toUserId 时，则 @ 的用户必须为 toUserId 中的用户。（可选）
     contentAvailable	Int	针对 iOS 平台，对 SDK 处于后台暂停状态时为静默推送，是 iOS7 之后推出的一种推送方式。 允许应用在收到通知后在后台运行一段代码，且能够马上执行，查看详细。1 表示为开启，0 表示为关闭，默认为 0（可选）
     */
    private String fromUserId;
    private String toGroupId;
    private String toUserId;
    private String objectName;
    private String content;
    private String pushContent;
    private String pushData;
    private String isPersisted;
    private String isCounted;
    private String isIncludeSender;
    private String isMentioned;
    private String contentAvailable;

    public GroupMethod(String fromUserId, String toGroupId, String objectName, String content) {
        this.fromUserId = fromUserId;
        this.toGroupId = toGroupId;
        this.objectName = objectName;
        this.content = content;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToGroupId() {
        return toGroupId;
    }

    public void setToGroupId(String toGroupId) {
        this.toGroupId = toGroupId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getPushData() {
        return pushData;
    }

    public void setPushData(String pushData) {
        this.pushData = pushData;
    }

    public String getIsPersisted() {
        return isPersisted;
    }

    public void setIsPersisted(String isPersisted) {
        this.isPersisted = isPersisted;
    }

    public String getIsCounted() {
        return isCounted;
    }

    public void setIsCounted(String isCounted) {
        this.isCounted = isCounted;
    }

    public String getIsIncludeSender() {
        return isIncludeSender;
    }

    public void setIsIncludeSender(String isIncludeSender) {
        this.isIncludeSender = isIncludeSender;
    }

    public String getIsMentioned() {
        return isMentioned;
    }

    public void setIsMentioned(String isMentioned) {
        this.isMentioned = isMentioned;
    }

    public String getContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(String contentAvailable) {
        this.contentAvailable = contentAvailable;
    }
    public static void main(String[] args) {
		//System.out.println(new Gson().toJson(new GroupMethod("18398850872", "123", "RC:DizNtf", "sd")));
	}
}
