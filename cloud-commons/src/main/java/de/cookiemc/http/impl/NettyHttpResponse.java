package de.cookiemc.http.impl;

import de.cookiemc.http.api.HttpContext;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import javax.annotation.Nonnull;


public class NettyHttpResponse implements de.cookiemc.http.api.HttpResponse, NettyDefaultHttpMessage<de.cookiemc.http.api.HttpResponse> {

	protected final DefaultFullHttpResponse nettyResponse;
	protected final HttpContext context;

	public NettyHttpResponse(@Nonnull HttpContext context, @Nonnull HttpRequest nettyHttpRequest) {
		this.context = context;
		this.nettyResponse = new DefaultFullHttpResponse(
			nettyHttpRequest.protocolVersion(),
			HttpResponseStatus.NOT_FOUND,
			Unpooled.buffer()
		);
	}

	@Nonnull
	@Override
	public HttpContext getContext() {
		return context;
	}

	@Override
	public int getStatusCode() {
		return nettyResponse.status().code();
	}

	@Nonnull
	@Override
	public de.cookiemc.http.api.HttpResponse setStatusCode(int code) {
		nettyResponse.setStatus(HttpResponseStatus.valueOf(code));
		return this;
	}

	@Nonnull
	@Override
	public byte[] getBody() {
		return nettyResponse.content().array();
	}

	@Nonnull
	@Override
	public de.cookiemc.http.api.HttpResponse setBody(@Nonnull byte[] data) {
		nettyResponse.content().clear();
		nettyResponse.content().writeBytes(data);
		return this;
	}

	@Nonnull
	@Override
	public HttpHeaders getNettyHeaders() {
		return nettyResponse.headers();
	}

	@Nonnull
	@Override
	public HttpVersion getNettyHttpVersion() {
		return nettyResponse.protocolVersion();
	}

	@Override
	public void setNettyHttpVersion(@Nonnull HttpVersion version) {
		nettyResponse.setProtocolVersion(version);
	}
}
