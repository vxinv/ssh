package com.biubiu.core.config;

import com.jcraft.jsch.Session;

import java.util.*;

public class MyThread  implements Runnable{

    private List<Session> sessionList = Collections.synchronizedList(new LinkedList<>());

    public void  addSession(Session session){
        sessionList.add(session);
    }

    @Override
    public void run() {
        for (Session session : sessionList) {
            //session.
        }
    }
}
