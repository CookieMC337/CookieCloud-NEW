package de.cookiemc.driver.networking.cluster.client;

import de.cookiemc.common.collection.ThreadRunnable;
import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.common.task.Task;
import de.cookiemc.document.Document;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverConnectEvent;
import de.cookiemc.driver.networking.protocol.codec.NetworkBossHandler;
import de.cookiemc.driver.networking.protocol.codec.PacketDecoder;
import de.cookiemc.driver.networking.protocol.codec.PacketEncoder;
import de.cookiemc.driver.networking.protocol.codec.prepender.NettyPacketLengthDeserializer;
import de.cookiemc.driver.networking.protocol.codec.prepender.NettyPacketLengthSerializer;
import de.cookiemc.driver.networking.protocol.packets.ConnectionType;
import de.cookiemc.driver.networking.protocol.packets.IPacket;
import de.cookiemc.driver.networking.protocol.packets.defaults.HandshakePacket;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.networking.AbstractNetworkComponent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.AlreadyConnectedException;

@Getter
public abstract class ClusterParticipant extends AbstractNetworkComponent<ClusterParticipant> {

    private MultithreadEventLoopGroup workerGroup;
    private boolean active;
    private Channel channel;
    private Document customData;
    private String connectedNodeName;
    private String authKey;

    public ClusterParticipant(String authKey, String clientName, ConnectionType type, Document customData) {
        super(type, clientName);

        this.authKey = authKey;
        this.active = false;
        this.channel = null;
        this.customData = customData;
        this.connectedNodeName = "UNKNOWN";
    }

    @Override
    public PacketChannel getPacketChannel() {
        return channel.pipeline().get(NetworkBossHandler.class).getPacketChannel();
    }


    public Task<Channel> openConnection(String hostname, int port) {
        Task<Channel> result = Task.empty();

        if (active) {
            result.setFailure(new AlreadyConnectedException());
            return result;
        }

        this.active = true;
        this.workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        ThreadRunnable runnable = new ThreadRunnable(() -> {

            new Bootstrap()
                    .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                    .group(workerGroup)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {

                            channel.pipeline()
                                    .addLast(new NettyPacketLengthDeserializer())
                                    .addLast(new PacketDecoder(ClusterParticipant.this))
                                    .addLast(new NettyPacketLengthSerializer())
                                    .addLast(new PacketEncoder(ClusterParticipant.this))
                                    .addLast(new NetworkBossHandler(ClusterParticipant.this) {

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            ClusterParticipant.this.onActivated(ctx);
                                            ClusterParticipant.this.sendPacket(new HandshakePacket(authKey, getName(), ClusterParticipant.this.type, customData));

                                            //fire connect event
                                            CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverConnectEvent());
                                            super.channelActive(ctx);

                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverConnectEvent());
                                            onClose(ctx);
                                            super.channelInactive(ctx);
                                        }

                                    });
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .connect(hostname, port).addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            channel = future.channel();
                            result.setResult(channel);
                        } else {
                            result.setFailure(future.cause());
                            workerGroup.shutdownGracefully();
                        }
                    });
        });

        if (this.bootAsync) {
            runnable.runAsync();
        } else {
            runnable.run();
        }
        return result;
    }

    @Override
    public <T extends IPacket> void handlePacket(PacketChannel wrapper, @NotNull T packet) {
        if (packet instanceof HandshakePacket) {
            HandshakePacket handshake = (HandshakePacket) packet;
            connectedNodeName = handshake.getNodeName();

            ThreadRunnable runnable = new ThreadRunnable(() -> onAuthenticationChanged(wrapper));
            if (handlePacketsAsync) {
                runnable.runAsync();
            } else {
                runnable.run();
            }
            return;
        }
        super.handlePacket(wrapper, packet);
    }


    public Task<Boolean> shutdown() {
        Task<Boolean> task = Task.empty();
        this.workerGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) {
                task.setResult(true);
            } else {
                task.setFailure(future.cause());
            }
        });
        return task;
    }

    @Override
    public Task<Void> sendPacketAsync(IPacket packet) {
        Task<Void> task = Task.empty();

        if (this.channel == null) {
            System.out.println("CHANNEL NULL");
            //re-schedule request
            Scheduler.runTimeScheduler().executeIf(() -> sendPacket(packet), () -> getChannel() != null);
        } else {
            this.getChannel().writeAndFlush(packet).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    task.setFailure(future.cause());
                } else {
                    task.setResult(null);
                }
            });
        }

        return task;
    }

    @Override
    public void sendPacket(IPacket packet) {
        if (this.channel == null) {
            //re-schedule request

            Scheduler.runTimeScheduler().executeIf(() -> sendPacket(packet), () -> getChannel() != null);
            return;
        }
        this.getChannel().writeAndFlush(packet).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) future.cause().printStackTrace();
        });
    }

    public abstract void onAuthenticationChanged(PacketChannel wrapper);

    public abstract void onActivated(ChannelHandlerContext ctx);

    public abstract void onClose(ChannelHandlerContext ctx);

}
