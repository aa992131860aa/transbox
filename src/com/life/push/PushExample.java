package com.life.push;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.SMS;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.InterfaceAdapter;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class PushExample {
	protected static final Logger LOG = LoggerFactory
			.getLogger(PushExample.class);

	// demo App defined in resources/jpush-api.conf
	private static final String appKey = "e0d6ddb4792507cf6fd73cda";
	private static final String masterSecret = "493551d04913cf5394894b5d";

	protected static final String APP_KEY = "e0d6ddb4792507cf6fd73cda";
	protected static final String MASTER_SECRET = "493551d04913cf5394894b5d";

	public static final String TITLE = "器官云监控";
	public static final String ALERT = "器官云监控";
	public static final String MSG_CONTENT = "Test from API Example - msgContent";
	public static final String REGISTRATION_ID = "0900e8d85ef";
	public static final String TAG = "tag_api";
	public static long sendCount = 0;
	private static long sendTotalTime = 0;

	public static void main(String[] args) {

		// testSendPushWithCustomConfig();
		// testSendIosAlert();
		// testSendPush();
		// testSendPushes();
		// testSendPush_fromJSON();
		// testSendPushWithCallback();
		String [] b = {"18398850872","17606505795"};
	
		//sendPushCustom("transfer","18398850872");
		String  a = "1000s";
		long c = 1000l;
		Long.parseLong(a);
		//System.out.println();
	}

	// 使用 NettyHttpClient 异步接口发送请求
	public static void testSendPushWithCallback() {
		ClientConfig clientConfig = ClientConfig.getInstance();
		String host = (String) clientConfig.get(ClientConfig.PUSH_HOST_NAME);
		final NettyHttpClient client = new NettyHttpClient(ServiceHelper
				.getBasicAuthorization(APP_KEY, MASTER_SECRET), null,
				clientConfig);
		try {
			URI uri = new URI(host + clientConfig.get(ClientConfig.PUSH_PATH));
			PushPayload payload = buildPushObject_all_alias_alert();
			// client.sendRequest(HttpMethod.POST, payload.toString(), uri, new
			// NettyHttpClient.BaseCallback() {
			// @Override
			// public void onSucceed(ResponseWrapper responseWrapper) {
			// LOG.info("Got result: " + responseWrapper.responseContent);
			// }
			// });
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void sendPush(String content, String targetPhone) {
		JPushClient jPushClient = new JPushClient(masterSecret, appKey);
		PushPayload pushPayload = PushPayload.newBuilder().setPlatform(
				Platform.all())
		// 别名 Audience.all()全部
				.setAudience(Audience.alias(targetPhone)).setNotification(
						Notification.alert(content)).setMessage(
						Message.content(content)).build();
		try {
			jPushClient.sendPush(pushPayload);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 多个
	 * @param content
	 * @param targetPhone
	 */
	public static void sendPushOrganSeg(String content, String[] targetPhone) {
		JPushClient jPushClient = new JPushClient(masterSecret, appKey);
		PushPayload pushPayload = PushPayload.newBuilder().setPlatform(
				Platform.all())
		// 别名 Audience.all()全部
				.setAudience(Audience.alias(targetPhone)).setNotification(
						Notification.alert(content)).setMessage(
						Message.content("message content")).build();
		try {
			jPushClient.sendPush(pushPayload);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    /**
     * 发送自定义消息(没有通知栏)
     */
	public static void sendPush(String content,String [] targetPhones) {
		JPushClient jPushClient = new JPushClient(masterSecret, appKey);
	
		PushPayload pushPayload =  PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(targetPhones))
				.setNotification(
						Notification.newBuilder().addPlatformNotification(
								IosNotification.newBuilder().setAlert(ALERT)
										.setBadge(5).setSound("happy")
										.addExtra("from", "JPush").build())
								.build()).setMessage(
						Message.content(content)).setOptions(
						Options.newBuilder().setApnsProduction(true).build())
				.build();
		try {
			jPushClient.sendPush(pushPayload);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}
	
    /**
     * 发送自定义消息(没有通知栏)
     */
	public static void sendPushCustom(String content,String  targetPhones) {
		JPushClient jPushClient = new JPushClient(masterSecret, appKey);
	
		PushPayload pushPayload =  PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias(targetPhones))
				.setNotification(
						Notification.newBuilder().addPlatformNotification(
								IosNotification.newBuilder().setAlert(ALERT)
										.setBadge(5).setSound("happy")
										.addExtra("from", "JPush").build())
								.build()).setMessage(
						Message.content(content)).setOptions(
						Options.newBuilder().setApnsProduction(true).build())
				.build();
		try {
			jPushClient.sendPush(pushPayload);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}

	public static void testSendPush() {
		// HttpProxy proxy = new HttpProxy("localhost", 3128);
		// Can use this https proxy: https://github.com/Exa-Networks/exaproxy
		ClientConfig clientConfig = ClientConfig.getInstance();
		final JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY,
				null, clientConfig);
		String authCode = ServiceHelper.getBasicAuthorization(APP_KEY,
				MASTER_SECRET);
		// Here you can use NativeHttpClient or NettyHttpClient or
		// ApacheHttpClient.
		NativeHttpClient httpClient = new NativeHttpClient(authCode, null,
				clientConfig);
		// Call setHttpClient to set httpClient,
		// If you don't invoke this method, default httpClient will use
		// NativeHttpClient.
		// ApacheHttpClient httpClient = new ApacheHttpClient(authCode, null,
		// clientConfig);
		jpushClient.getPushClient().setHttpClient(httpClient);
		final PushPayload payload = buildPushObject_android_newly_support();
		for (int i = 0; i < 1; i++) {
			Thread thread = new Thread() {
				public void run() {
					for (int j = 0; j < 1; j++) {
						long start = System.currentTimeMillis();
						try {
							PushResult result = jpushClient.sendPush(payload);
							LOG.info("Got result - " + result);
							//System.out.println("got result:" + result);

						} catch (APIConnectionException e) {
							LOG.error("Connection error. Should retry later. ",
									e);
							//System.out .println("Sendno: " + payload.getSendno());

						} catch (APIRequestException e) {
							LOG
									.error(
											"Error response from JPush server. Should review and fix it. ",
											e);
							//System.out.println("HTTP Status: " + e.getStatus());
							//System.out.println("Error Code: " + e.getErrorCode());
							//System.out.println("Error Message: " + e.getErrorMessage());
							//System.out.println("Msg ID: " + e.getMsgId());
							//System.out .println("Sendno: " + payload.getSendno());
						}

						//System.out.println("耗时" + (System.currentTimeMillis() - start) + "毫秒 sendCount:" + (++sendCount));
					}
				}
			};
			thread.start();
		}

		// // For push, all you need do is to build PushPayload object.
		// PushPayload payload = buildPushObject_all_alias_alert();
		// try {
		// PushResult result = jpushClient.sendPush(payload);
		// LOG.info("Got result - " + result);
		// // 如果使用 NettyHttpClient，需要手动调用 close 方法退出进程
		// // If uses NettyHttpClient, call close when finished sending request,
		// otherwise process will not exit.
		// // jpushClient.close();
		// } catch (APIConnectionException e) {
		// LOG.error("Connection error. Should retry later. ", e);
		// LOG.error("Sendno: " + payload.getSendno());
		//
		// } catch (APIRequestException e) {
		// LOG.error("Error response from JPush server. Should review and fix it. ",
		// e);
		// LOG.info("HTTP Status: " + e.getStatus());
		// LOG.info("Error Code: " + e.getErrorCode());
		// LOG.info("Error Message: " + e.getErrorMessage());
		// LOG.info("Msg ID: " + e.getMsgId());
		// LOG.error("Sendno: " + payload.getSendno());
		// }
	}

	// use String to build PushPayload instance
	public static void testSendPush_fromJSON() {
		ClientConfig clientConfig = ClientConfig.getInstance();
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null,
				clientConfig);
		Gson gson = new GsonBuilder().registerTypeAdapter(
				PlatformNotification.class,
				new InterfaceAdapter<PlatformNotification>()).create();
		// Since the type of DeviceType is enum, thus the value should be
		// uppercase, same with the AudienceType.
		String payloadString = "{\"platform\":{\"all\":false,\"deviceTypes\":[\"IOS\"]},\"audience\":{\"all\":false,\"targets\":[{\"audienceType\":\"TAG_AND\",\"values\":[\"tag1\",\"tag_all\"]}]},\"notification\":{\"notifications\":[{\"soundDisabled\":false,\"badgeDisabled\":false,\"sound\":\"happy\",\"badge\":\"5\",\"contentAvailable\":false,\"alert\":\"Test from API Example - alert\",\"extras\":{\"from\":\"JPush\"},\"type\":\"cn.jpush.api.push.model.notification.IosNotification\"}]},\"message\":{\"msgContent\":\"Test from API Example - msgContent\"},\"options\":{\"sendno\":1429488213,\"overrideMsgId\":0,\"timeToLive\":-1,\"apnsProduction\":true,\"bigPushDuration\":0}}";
		PushPayload payload = gson.fromJson(payloadString, PushPayload.class);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
			LOG.error("Sendno: " + payload.getSendno());

		} catch (APIRequestException e) {
			LOG
					.error(
							"Error response from JPush server. Should review and fix it. ",
							e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
			LOG.error("Sendno: " + payload.getSendno());
		}
	}

	public static void testSendPushes() {

	}

	public static PushPayload buildPushObject_all_all_alert() {
		return PushPayload.alertAll(ALERT);
	}

	public static PushPayload buildPushObject_all_alias_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.alias("alias1")).setNotification(
						Notification.alert(ALERT)).build();
	}

	public static PushPayload buildPushObject_android_tag_alertWithTitle() {
		return PushPayload.newBuilder().setPlatform(Platform.android())
				.setAudience(Audience.tag("tag1")).setNotification(
						Notification.android(ALERT, TITLE, null)).build();
	}

	public static PushPayload buildPushObject_android_and_ios() {
		return PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.tag("tag1"))
				.setNotification(
						Notification.newBuilder().setAlert("alert content")
								.addPlatformNotification(
										AndroidNotification.newBuilder()
												.setTitle("Android Title")
												.build())
								.addPlatformNotification(
										IosNotification.newBuilder().incrBadge(
												1).addExtra("extra_key",
												"extra_value").build()).build())
				.build();
	}

	public static void buildPushObject_with_extra() {

		JsonObject jsonExtra = new JsonObject();
		jsonExtra.addProperty("extra1", 1);
		jsonExtra.addProperty("extra2", false);

		Map<String, String> extras = new HashMap<String, String>();
		extras.put("extra_1", "val1");
		extras.put("extra_2", "val2");

		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.tag("tag1"))
				.setNotification(
						Notification.newBuilder().setAlert("alert content")
								.addPlatformNotification(
										AndroidNotification.newBuilder()
												.setTitle("Android Title")
												.addExtras(extras).addExtra(
														"booleanExtra", false)
												.addExtra("numberExtra", 1)
												.addExtra("jsonExtra",
														jsonExtra).build())
								.addPlatformNotification(
										IosNotification.newBuilder().incrBadge(
												1).addExtra("extra_key",
												"extra_value").build()).build())
				.build();

		//System.out.println(payload.toJSON());
	}

	public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
		return PushPayload.newBuilder().setPlatform(Platform.ios())
				.setAudience(Audience.tag_and("tag1", "tag_all"))
				.setNotification(
						Notification.newBuilder().addPlatformNotification(
								IosNotification.newBuilder().setAlert(ALERT)
										.setBadge(5).setSound("happy")
										.addExtra("from", "JPush").build())
								.build()).setMessage(
						Message.content(MSG_CONTENT)).setOptions(
						Options.newBuilder().setApnsProduction(true).build())
				.build();
	}

	public static PushPayload buildPushObject_android_newly_support() {
		JsonObject inbox = new JsonObject();
		inbox.add("line1", new JsonPrimitive("line1 string"));
		inbox.add("line2", new JsonPrimitive("line2 string"));
		inbox.add("contentTitle", new JsonPrimitive("title string"));
		inbox.add("summaryText", new JsonPrimitive("+3 more"));
		Notification notification = Notification.newBuilder()
				.addPlatformNotification(
						AndroidNotification.newBuilder().setAlert(ALERT)
								.setBigPicPath("path to big picture")
								.setBigText("long text").setBuilderId(1)
								.setCategory("CATEGORY_SOCIAL").setInbox(inbox)
								.setStyle(1).setTitle("Alert test")
								.setPriority(1).build()).build();
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.registrationId("18071adc030dcba91c0"))
				.setNotification(notification).setOptions(Options.sendno())
				.build();
	}

	public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
		return PushPayload.newBuilder().setPlatform(Platform.android_ios())
				.setAudience(
						Audience.newBuilder().addAudienceTarget(
								AudienceTarget.tag("tag1", "tag2"))
								.addAudienceTarget(
										AudienceTarget
												.alias("alias1", "alias2"))
								.build()).setMessage(
						Message.newBuilder().setMsgContent(MSG_CONTENT)
								.addExtra("from", "JPush").build()).build();
	}

	public static PushPayload buildPushObject_all_tag_not() {
		return PushPayload.newBuilder().setPlatform(Platform.all())
				.setAudience(Audience.tag_not("abc", "123")).setNotification(
						Notification.alert(ALERT)).build();
	}

	public static void testSendPushWithCustomConfig() {
		ClientConfig config = ClientConfig.getInstance();
		// Setup the custom hostname
		config.setPushHostName("https://api.jpush.cn");

		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null,
				config);

		// For push, all you need do is to build PushPayload object.
		PushPayload payload = buildPushObject_all_all_alert();

		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			LOG
					.error(
							"Error response from JPush server. Should review and fix it. ",
							e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
		}
	}

	public static void testSendIosAlert() {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);

		IosAlert alert = IosAlert.newBuilder().setTitleAndBody("test alert",
				"subtitle", "test ios alert json").setActionLocKey("PLAY")
				.build();
		try {
			PushResult result = jpushClient.sendIosNotificationWithAlias(alert,
					new HashMap<String, String>(), "alias1");
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG
					.error(
							"Error response from JPush server. Should review and fix it. ",
							e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}
	

	public static void testSendWithSMS() {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		try {
			SMS sms = SMS.content("Test SMS", 10);
			PushResult result = jpushClient.sendAndroidMessageWithAlias(
					"Test SMS", "test sms", sms, "alias1");
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG
					.error(
							"Error response from JPush server. Should review and fix it. ",
							e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

}