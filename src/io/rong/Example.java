package io.rong;

import io.rong.messages.*;
import io.rong.models.*;
import io.rong.util.GsonUtil;

import java.io.Reader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 一些api的调用示例
 */
public class Example {
	private static final String JSONFILE = Example.class.getClassLoader().getResource("jsonsource").getPath()+"/";
	/**
	 * 本地调用测试
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String appKey = "n19jmcy5nety9";//替换成您的appkey
		String appSecret = "SdwTI3aFmYb";//替换成匹配上面key的secret
		
		Reader reader = null ;
		RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
				



	 }
}