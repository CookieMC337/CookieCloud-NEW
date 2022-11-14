package de.cookiemc.http.api;

import javax.annotation.Nonnull;


public interface HttpResponse extends HttpMessage<HttpResponse> {

	int getStatusCode();

	@Nonnull
	HttpResponse setStatusCode(int code);

}
