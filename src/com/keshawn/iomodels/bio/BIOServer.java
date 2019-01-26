package com.keshawn.iomodels.bio;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Package: com.keshawn.bio
 * Description： TODO
 * Author: keshawn1
 * Date: Created in 2019/1/26 10:11
 * Company: 公司
 * Copyright: Copyright (c) 2019
 * Version: 0.0.1
 * Modified By:
 */
public class BIOServer {


    public static void main(String[] args) {
        int port = 8080;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            Socket socket = null;
            while (true) {
                socket = server.accept();   //program is blocked in here
                //每个新的连接都新建一个线程
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
