package com.keshawn.iomodels.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Package: com.keshawn.nio
 */


public class NIOServerHandler {

    private int port;

    public NIOServerHandler(int port) {
        this.port = port;
    }

    public void start() throws  IOException {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();

        //thread for processing new connection
        //register socket in clientSelector
        new Thread(() -> {
            try {
                // lestening for new connection, the blocking times is 1ms
                ServerSocketChannel listenerChanner = ServerSocketChannel.open();
                listenerChanner.socket().bind(new InetSocketAddress(port));
                listenerChanner.configureBlocking(false);
                listenerChanner.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true){
                    //blocking time 1 ms
                    if(serverSelector.select(1) > 0){
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> it = set.iterator();

                        while(it.hasNext()){
                            SelectionKey key = it.next();
                            if(key.isAcceptable()){

                                try {
                                    //for every new connection, no other thread need,register it on clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    it.remove();
                                }
                            }
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        //thread for processing read data
        new Thread(() -> {
            try {
                while(true){
                    if(clientSelector.select(1) > 0){
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> it = set.iterator();
                        while(it.hasNext()){
                            SelectionKey key = it.next();

                            if(key.isReadable()){

                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                                    int readBytes = clientChannel.read(byteBuffer);
                                    if (readBytes > 0) {
                                        byteBuffer.flip();
                                        byte[] bytes = new byte[readBytes];
                                        byteBuffer.get(bytes);
                                        String msg = new String(bytes, "UTF-8");
                                        System.out.println("Receive form client: " + msg);
                                        System.out.println("Send to client: " + msg);
                                        doWrite(clientChannel, msg);
                                    }
                                }
                                finally {
                                    it.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void doWrite(SocketChannel channel, String response)
            throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
