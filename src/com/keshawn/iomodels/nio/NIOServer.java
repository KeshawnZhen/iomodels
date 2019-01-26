package com.keshawn.iomodels.nio;

import java.io.IOException;

/**
 * Package: com.keshawn.nio
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        new NIOServerHandler(port).start();


    }

}
