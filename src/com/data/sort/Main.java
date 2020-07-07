package com.data.sort;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.life.entity.Blood;
import com.sun.mail.util.MailSSLSocketFactory;

public class Main {
    public static int A = 3;
    public static List<String> list = new ArrayList<String>();

    private static String account = "cy@lifeperfusor.com";// 登录账户
    private static String password = "dYZaoTHjSjWCWD7s";// 登录密码
    private static String host = "smtp.exmail.qq.com";// 服务器地址
    private static String port = "465";// 端口
    private static String protocol = "smtp";// 协议

    //初始化参数
    public static Session initProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        // 使用SSL,企业邮箱必需 start
        // 开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        properties.put("mail.smtp.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", port);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        // 使用SSL,企业邮箱必需 end
        // TODO 显示debug信息 正式环境注释掉
        session.setDebug(true);
        return session;
    }

    // @param sender 发件人别名
// @param subject 邮件主题
//@param content 邮件内容
//@param receiverList 接收者列表,多个接收者之间用","隔开
//@param fileSrc 附件地址
    public static void send(String sender, String subject, String content, String receiverList, String fileSrc) {
        try {
            Session session = initProperties();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(account, sender));// 发件人,可以设置发件人的别名
            // 收件人,多人接收
            InternetAddress[] internetAddressTo = new InternetAddress().parse(receiverList);
            mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
            // 主题
            mimeMessage.setSubject(subject);
            // 时间
            mimeMessage.setSentDate(new Date());
            // 容器类 附件
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 可以包装文本,图片,附件
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 设置内容
            bodyPart.setContent(content, "text/html; charset=UTF-8");
            mimeMultipart.addBodyPart(bodyPart);
            // 添加图片&附件
//        bodyPart = new MimeBodyPart();
//        bodyPart.attachFile(fileSrc);
            mimeMultipart.addBodyPart(bodyPart);
            mimeMessage.setContent(mimeMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        Field privateStringField = null;
        Class c = null;
        Constructor con = null;
        try {
            //1.获取类的无参构造方法
            Constructor<TestFile> constructor = TestFile.class.getDeclaredConstructor();
            //2.设置取消访问检查，是访问私有构造方法的关键
            constructor.setAccessible(true);
            //3.调用该构造方法，获得对象
            TestFile reflectDemo = constructor.newInstance();


            privateStringField = TestFile.class.
                    getDeclaredField("name");

            privateStringField.setAccessible(true);

            privateStringField.set(reflectDemo, new Test("zhangsan33", 180));

            Field f;
            Object name = null;
            //FanShePresenter mFanShePresenter = new FanShePresenter(this);
            Class temp = reflectDemo.getClass();
            try {
                f = temp.getDeclaredField("name");
                f.setAccessible(true);
                name = f.get(reflectDemo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Test test = (Test) name;
            System.out.println("name:" + test.getName());
            // SDKOptions name = (SDKOptions) privateStringField.get(reflectDemo);

        } catch (Exception e) {
            e.printStackTrace();

        }
//	send("cy", "test", "test content", "992131860@qq.com", "");
//	   // 收件人电子邮箱
//	   String to = "992131860@qq.com";
//
//	   // 发件人电子邮箱
//	   String from = "cy@lifeperfusor.com";
//	Properties prop = new Properties();
//	// 开启debug调试，以便在控制台查看
//	prop.setProperty("mail.debug", "true"); 
//	// 设置邮件服务器主机名
//	prop.setProperty("mail.host", "smtp.qq.com");
//	// 发送服务器需要身份验证
//	prop.setProperty("mail.smtp.auth", "true");
//	// 发送邮件协议名称
//	prop.setProperty("mail.transport.protocol", "smtp");
//
//	// 开启SSL加密，否则会失败
//	MailSSLSocketFactory sf = new MailSSLSocketFactory();
//	sf.setTrustAllHosts(true);
//	prop.put("mail.smtp.ssl.enable", "true");
//	prop.put("mail.smtp.ssl.socketFactory", sf);
//
//	// 创建session
//	Session session = Session.getInstance(prop);
//	// 通过session得到transport对象
//	Transport ts = session.getTransport();
//	// 连接邮件服务器：邮箱类型，帐号，授权码代替密码（更安全）
//	ts.connect("smtp.qq.com","cy@lifeperfusor.com", "dYZaoTHjSjWCWD7s");//后面的字符是授权码，用qq密码反正我是失败了（用自己的，别用我的，这个号是我瞎编的，为了。。。。）
//	// 创建邮件
//	Message message = createSimpleMail(session);
//	// 发送邮件
//	ts.sendMessage(message, message.getAllRecipients());
//	ts.close();
    }

    /**
     * @Method: createSimpleMail
     * @Description: 创建一封只包含文本的邮件
     */
    public static MimeMessage createSimpleMail(Session session)
            throws Exception {
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress("cy@lifeperfusor.com"));
        // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("992131860@qq.com"));
        // 邮件的标题
        message.setSubject("JavaMail测试");
        // 邮件的文本内容
        message.setContent("JavaMail发送邮件成功！", "text/html;charset=UTF-8");
        // 返回创建好的邮件对象
        return message;
    }

    public <E> void parray(E[] arr) {

    }

    private void bb(List<String> aa) {
        aa.add("cc");
    }

    /**
     * 插入排序
     */
    public static Integer[] insetionSort(Integer a[]) {

        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j > -1 && a[j] > key) {
                a[j + 1] = a[j];
                j = j - 1;
            }
            a[j + 1] = key;


        }
        return a;
    }
}
