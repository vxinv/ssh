package com.biubiu.core.server;


import ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import cn.hutool.core.util.ArrayUtil;
import com.biubiu.core.common.Const;
import com.biubiu.model.Remote;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;

@Scope("prototype")
@Component
@Slf4j
public class Terminal implements Runnable{

    private Remote remote;
    public JSch jsch;

    private InputStream inputStream;
    private OutputStream outputStream;
    private javax.websocket.Session wsSession;
    Channel channel;
    Session session;

    public Terminal init(Remote remote) {
        this.remote = remote;
        this.jsch = new JSch();
        return this;
    }

    public void openConnect(javax.websocket.Session wsSession) throws Exception {
        log.info("user: {},host: {},port: {}, password: {}, identity: {}", remote.getUser(), remote.getHost(), remote.getPort(), remote.getPassword(), remote.getIdentity());
        try{
            // rsa验证方式
            if (Const.AUTH_METHOD.equalsIgnoreCase(remote.getAuthMethod()) && Files.exists(Paths.get(remote.getIdentity()))) {
                jsch.addIdentity(remote.getIdentity(), remote.getPassphrase());
            }
            session = jsch.getSession(remote.getUser(), remote.getHost(), remote.getPort());
            // 密码验证方式设置密码
            if(!Const.AUTH_METHOD.equalsIgnoreCase(remote.getAuthMethod())) {
                session.setPassword(remote.getPassword());
            }
            session.setConfig("StrictHostKeyChecking", "no");
            // 跳过Kerberos身份验证
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.connect(3000);
            channel = session.openChannel("shell");
            this.inputStream = channel.getInputStream();
            this.outputStream = channel.getOutputStream();
            channel.connect(3000);
            this.wsSession = wsSession;
            this.send("Hello,this Developed by Lixin \n");
            new Thread(this).start();
        }catch (Exception e) {
            this.send( e.getMessage());
        }
    }


    /**
     * 向网页发送文本
     * @param message
     */
    public synchronized void send(String message) {
        try {
            this.wsSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("", e);
        }
    }


    public void exec(String command) {
        if (!session.isConnected()){
            try {
                send("正在重新连接");
                openConnect(wsSession);
            } catch (Exception e) {
                send("重连失败");
                log.info("重连失败");
            }
        }
        try {
            // \r 立即执行,若没检测到\r 即是回车键命令，shell不会执行任何命令。知道检测到\r为止，相当于在shell上输入了一串命令并没有按回车执行
            byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
            outputStream.flush();
            log.info(command);
        } catch (Exception e) {
            log.error("", e);
        }

    }

    public void close(){
        try {
            remote = null;
            inputStream.close();
            outputStream.close();
            wsSession.close();
            channel.disconnect();
            session.disconnect();
        }catch (Exception e){
            log.error("关闭和远程服务器链接出错",e);
        }

    }

    final int capcity = 3128;
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // 新建缓冲区
            byte[] buff = new byte[capcity];
            // 阻塞接收,前端不需要输入显示太多，全部由后台接收就行
            int readLen = 0;
            try {
                readLen = inputStream.read(buff);
            } catch (IOException e) {
                log.error("io关闭,线程结束 ",e);
                break;
            }

            if (readLen != -1) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buff, 0, readLen);
                byte[] readData = new byte[readLen];
                byteBuffer.position(0);
                byteBuffer.get(readData);
                String data = new String(readData, StandardCharsets.UTF_8);
                //log.info("resp data: {}", data);
                this.send( data);
            }
        }
        log.info("确认线程关闭 {}",Thread.currentThread().isAlive());
    }

    public static int splitLength(byte[] buff,long readLen){
        int i = 0;
        for (; i < readLen; i++) {
            //0110xxxx
            if (buff[i] >>> 7 == 0b00000000){
                continue;
            }

            // 1111110x
            if (buff[i] >>> 1 == 0b01111110){
                if ((i+5) < readLen){
                    i+=5;
                }else {
                    return i;
                }
            }
            // 111110xx
            if (buff[i] >>> 2 == 0b00111110){
                if ((i+4) < readLen){
                    i+=4;
                }else {
                    return i;
                }
            }
            //11110xxx
            if (buff[i] >>> 3 == 0b00011110){
                if ((i+3) < readLen){
                    i+=3;
                }else {
                    return i;
                }
            }
            //1110xxxx
            if (buff[i] >>> 4 == 0b00001110){
                if ((i+2) < readLen){
                    i+=2;
                }else {
                    return i;
                }
            }
            //110xxxxx
            if (buff[i] >>> 5 == 0b00000110){
                if ((i+1) < readLen){
                    i+=1;
                }else {
                    return i;
                }
            }
        }
        return i;
    }

    public static void main(String[] args) {
        // 字符串转十六进制大大阿达8eycsyc8ydsc8hbcj你好
        byte[] bs = {(byte) 0xe5,(byte) 0xad,(byte) 0x97,(byte) 0xe7,(byte) 0xac,(byte) 0xa6,(byte) 0xe4,(byte) 0xb8,(byte) 0xb2,(byte) 0xe8,(byte) 0xbd,(byte) 0xac,(byte) 0xe5,
                (byte) 0x8d,(byte) 0x81,(byte) 0xe5,(byte) 0x85,(byte) 0xad,(byte) 0xe8,(byte) 0xbf,(byte) 0x9b,(byte) 0xe5,(byte) 0x88,(byte) 0xb6,(byte) 0xe5,(byte) 0xa4,
                (byte) 0xa7,(byte) 0xe5,(byte) 0xa4,(byte) 0xa7,(byte) 0xe9,(byte) 0x98,(byte) 0xbf,(byte) 0xe8,(byte) 0xbe,(byte) 0xbe,0x38,0x65,0x79,0x63,0x73,0x79,0x63,0x38,
                0x79,0x64,0x73,0x63,0x38,0x68,0x62,0x63,0x6a, (byte) 0xe4, (byte) 0xbd, (byte) 0xa0, (byte) 0xe5};
        int i = splitLength(bs, bs.length);
        System.out.println(i);
        byte[] bytes = new byte[i];
        System.arraycopy(bs,0,bytes,0,i);
        System.out.println(new String(bytes));
    }
}

