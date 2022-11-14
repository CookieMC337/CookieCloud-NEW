package de.cookiemc.node.handler.http;

import de.cookiemc.common.VersionInfo;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.http.api.*;
import de.cookiemc.http.api.*;

import javax.annotation.Nonnull;


@HttpRouter("status")
public class V1StatusRouter {

	@HttpEndpoint(method = HttpMethod.GET)
	public void getIndex(@Nonnull HttpContext context) {
		CloudDriver<?> driver = CloudDriver.getInstance();

		context.getResponse()
			.setHeader("Content-Type", "application/json")
			.setBody(DocumentFactory.newJsonDocument(VersionInfo.getCurrentVersion()))
			.setStatusCode(HttpCodes.OK)
			.getContext()
			.closeAfter(true)
			.cancelNext(true);
	}

}
