package de.cookiemc.database.handler.other.document;


import de.cookiemc.common.VersionInfo;
import de.cookiemc.document.Document;
import de.cookiemc.http.api.*;
import de.cookiemc.http.api.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@HttpRouter("documents/get")
public class GetDocumentHandler {

    @HttpEndpoint(
            method = HttpMethod.GET,
            permission = "cloud.database.access"
    )
    public void handle(@Nonnull HttpContext context) {
        Map<String, List<String>> queryParameters = context.getRequest().getQueryParameters();
        System.out.println(queryParameters);
        context.getResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(Document.newJsonDocument(VersionInfo.getCurrentVersion()))
                .setStatusCode(HttpCodes.OK)
                .getContext()
                .closeAfter(true)
                .cancelNext(true);
    }
}
