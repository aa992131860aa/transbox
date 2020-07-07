package com.life.transfer;

import com.life.utils.CONST;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Mina长连接服务端搭建
 * <p>
 * 负责session的创建及消息发送和接收的监听
 */
public class MyHandler extends IoHandlerAdapter {
    private String name;

    public MyHandler(String name) {
        this.name = name;
    }


    // session创建时回调
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);

        System.out.println("sessionCreated ");
    }

    // session打开时回调
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);

        System.out.println("sessionOpened ");
    }

    // 消息接收时回调
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);


        String msg = message.toString();
        session.write("服务端给返回的消息：" + msg); // 给客户端返回
        //System.out.println("服务端接收消息： " + msg);

        //先移除存在的，在添加
        if (msg.length() == 5) {
            for (String key : CONST.ALL_IO_SESSION.keySet()) {
                if (key.equals(msg)) {
                    CONST.ALL_IO_SESSION.get(key).closeNow();
                    CONST.ALL_IO_SESSION.remove(key);
                }
            }
            CONST.ALL_IO_SESSION.put(msg, session);
            CONST.SEND_PUSH = 10;
        }

        System.out.println("当前个数:" + CONST.ALL_IO_SESSION.size() + ":msg:" + msg);
    }

    // 消息发送时回调
    public void messageSent(final IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        //session.write(message);
        System.out.println("messageSent " + message);
    }

    // session关闭时回调
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        System.out.println("sessionClosed ");
    }
}