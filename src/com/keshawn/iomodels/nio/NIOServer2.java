package com.keshawn.iomodels.nio;

import com.sun.org.apache.bcel.internal.generic.Select;

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
 * Package: com.keshawn.iomodels.nio
 */
public class NIOServer2 {

    private Selector selector;

    private ServerSocketChannel servChannerl;

    private int port;

    public NIOServer2() {
        try {
            port = 8080;
            selector = Selector.open();
            servChannerl = ServerSocketChannel.open();
            servChannerl.configureBlocking(false);
            servChannerl.socket().bind(new InetSocketAddress(port), 1024);
            servChannerl.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NIOServer2().start();
    }

    private void start(){
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleKey(key);
                    } catch (IOException e) {
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleKey(SelectionKey key) throws IOException {
        if(key.isValid()){
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if(key.isReadable()){
                SocketChannel socketChannel = (SocketChannel)key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBytes];
                    readBuffer.get(bytes);
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("Receive form client: " + msg);
                    System.out.println("Send to client: " + msg);
                    doWrite(socketChannel, msg);
                }else if(readBytes < 0){
                    key.cancel();
                    socketChannel.close();
                }else{
                    ;
                }
            }
        }

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
