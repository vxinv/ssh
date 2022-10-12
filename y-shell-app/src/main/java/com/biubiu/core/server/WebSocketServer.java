package com.biubiu.core.server;

import com.alibaba.fastjson.JSON;
import com.biubiu.core.common.Const;
import com.biubiu.dao.EcsDao;
import com.biubiu.model.MessageModel;
import com.biubiu.model.Remote;
import com.biubiu.pojo.Ecs;
import com.biubiu.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@ServerEndpoint("/terminal")
@Component
public class WebSocketServer {

    private static final CopyOnWriteArraySet<WebSocketServer> socketList = new CopyOnWriteArraySet<WebSocketServer>();



    // 每一次连接都会新建 WebSocketServer,因此就会有新的 terminal
    public Terminal terminal;

    @Autowired
    public EcsDao ecsDao;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("onOpen {}", session);
        if (ecsDao == null) {
            ecsDao = SpringContextUtil.getBean(EcsDao.class);
        }
        socketList.add(this);
        log.info("websocket连接 sessionId = {}", session.getId());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        terminal.close();
        socketList.remove(this);
        log.info("onClose: {}", session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        //log.info("message: {},当前连接数 {}", message,socketList.size());
        MessageModel messageModel = JSON.parseObject(message, MessageModel.class);
        switch (messageModel.getType()) {
            case "connect":
                // 获取前端传来的id
                String nodeId = messageModel.getData();
                // nodeId
                Ecs ecs = ecsDao.findById(Long.parseLong(nodeId));
                // 创建的信息
                Remote remote = JSON.parseObject(ecs.getConfig(), Remote.class);
                if (StringUtils.isNotBlank(remote.getIdentity())) {
                    remote.setIdentity(Const.upload + remote.getHost() + "/" + remote.getIdentity());
                }
                if (null == terminal) {
                    this.terminal = new Terminal();
                    this.terminal = terminal.init(remote);
                    this.terminal.openConnect(session);
                }
                break;
            case "command":
                //log.info("command: {}", messageModel.getData());
                String cmd = messageModel.getData();
                this.terminal.exec(cmd);
            case "heartbeat":
                // 什么也不做就可以
                this.terminal.session.sendKeepAliveMsg();
                this.terminal.send("\u0007");
            default:
                break;
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("{}错误: {}", session, error);
        terminal.close();
        socketList.remove(this);
        terminal = null;
    }


}
