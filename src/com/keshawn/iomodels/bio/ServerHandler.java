package com.keshawn.iomodels.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Package: com.keshawn.bio
 * Description： TODO
 * Author: keshawn1
 * Date: Created in 2019/1/26 10:16
 * Company: 公司
 * Copyright: Copyright (c) 2019
 * Version: 0.0.1
 * Modified By:
 */
public class ServerHandler implements Runnable {

    private Socket socket;



    public ServerHandler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String content;
            while(true){
                content = in.readLine();
                System.out.println("receive form client: " + content);
                out.println(content);
                System.out.println("send to client: " + content);
            }
            //这个线程将什么时候销毁？
        }catch (Exception e){
            if(in != null){
                try {
                    in.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }

            if(out != null){
                out.close();
                out = null;
            }

            if(this.socket != null){
                try{
                    this.socket.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }finally {
            Thread.currentThread().interrupt();
        }
    }
}
