package io.rong.messages;

import io.rong.util.GsonUtil;

/**
 *
 * 文本消息。
 *
 */
public class DizNtfMessage extends BaseMessage {
	private int type = 1;
	private String extension = "";
	private String operator = "";
	private transient static final String TYPE = "RC:DizNtf";
	
	 
	public DizNtfMessage(){}
	public DizNtfMessage(int type, String extension, String operator) {
		super();
		this.type = type;
		this.extension = extension;
		this.operator = operator;
	}


	public String getType() {
		return TYPE;
	}
	
 
	public String getExtension() {
		return extension;
	}


	public void setExtension(String extension) {
		this.extension = extension;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public void setType(int type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return GsonUtil.toJson(this, DizNtfMessage.class);
	}
}