package weixinService.util;

import java.util.Date;

import weixinService.domain.TextMessage;

/*
 * ��Ϣ��������
 */
public class MessageUtil {
	public static final String MSGTYPE_EVENT = "event";//��Ϣ����--�¼�
	public static final String MESSAGE_SUBSCIBE = "subscribe";//��Ϣ�¼�����--�����¼�
	public static final String MESSAGE_UNSUBSCIBE = "unsubscribe";//��Ϣ�¼�����--ȡ�������¼�
	public static final String MESSAGE_TEXT = "text";//��Ϣ����--�ı���Ϣ
	
	/*
	 * ��װ�ı���Ϣ
	 */
	public static String textMsg(String toUserName,String fromUserName,String content){
		TextMessage text = new TextMessage();
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MESSAGE_TEXT);
		text.setCreateTime(new Date().getTime());
		text.setContent(content);
		return XmlUtil.textMsgToxml(text);
	}
	
	/*
	 * ��Ӧ�����¼�--�ظ��ı���Ϣ
	 */
	public static String subscribeForText(String toUserName,String fromUserName){
		return textMsg(toUserName, fromUserName, "��ӭ��ע���������ݲ��ݴ��������");
	}
	
	/*
	 * ��Ӧȡ�������¼�
	 */
	public static String unsubscribe(String toUserName,String fromUserName){
		//TODO ���Խ���ȡ�غ����������ҵ����

		return "";
	}
}
