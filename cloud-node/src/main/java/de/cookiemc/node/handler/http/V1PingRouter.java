package de.cookiemc.node.handler.http;


import de.cookiemc.document.DocumentFactory;
import de.cookiemc.http.api.*;
import de.cookiemc.http.api.*;

import javax.annotation.Nonnull;


@HttpRouter("ping")
public class V1PingRouter {

	@HttpEndpoint(method = HttpMethod.GET)
	public void getIndex(@Nonnull HttpContext context) {
		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(DocumentFactory.newJsonDocument("success", true))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

}
