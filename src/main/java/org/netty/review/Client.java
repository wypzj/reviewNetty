package org.netty.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.netty.review.client.ClientHandler;

import java.net.InetSocketAddress;

/**
 * @author wyp
 * @version 1.0
 * @description description
 * @date in 16:35 26/11/2020
 * @since 1.0
 */
public class Client {
    private String server;
    private Integer port;

    public Client(String server, Integer port) {
        this.server = server;
        this.port = port;
    }

    public static void main(String[] args) {
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        System.out.println("client connects server:" + args[0] + " port:" + args[1]);
        client.start();
    }

    public void start() {
        final ClientHandler clientHandler = new ClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap
                .group(group)
                .remoteAddress(new InetSocketAddress(this.server, this.port))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(clientHandler);
                    }
                });
        try {
            ChannelFuture sync = clientBootstrap.connect().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
