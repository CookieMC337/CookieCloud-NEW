package de.cookiemc.http.impl;

import de.cookiemc.http.HttpAddress;
import de.cookiemc.http.api.HttpChannel;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class NettyHttpChannel implements HttpChannel {

	protected final HttpAddress serverAddress, clientAddress;
	protected final Channel nettyChannel;

	@Override
	public void close() {
		nettyChannel.close();
	}

	@Override
	public String toString() {
		return "HttpChannel[client=" + clientAddress + " server=" + serverAddress + "]";
	}
}
