package com.life.utils.wechat;
public class PayConfigUtil {
	//初始化
	public final static String APP_ID = "wx8d93a4fbade11124"; //公众账号appid（改为自己实际的）
	public final static String APP_SECRET = "5391bdbaf15a46bdc6ab8e7d6d8bbb45";
	public final static String MCH_ID = "1500678351"; //商户号（改为自己实际的）
	public final static String API_KEY = "asdf323ADGAeD9aDAHAasdfasdfasd34"; //（改为自己实际的）key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
	
	//有关url
	public final static String UFDODER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public final static String NOTIFY_URL = "http://www.lifeperfusor.com/transbox/url.do?action=payMoney"; //微信支付回调接口，就是微信那边收到（改为自己实际的）
	//企业向个人账号付款的URL
	public final static String SEND_EED_PACK_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
	
	public final static String CREATE_IP = "116.62.28.28";//发起支付ip（改为自己实际的）
	
}
