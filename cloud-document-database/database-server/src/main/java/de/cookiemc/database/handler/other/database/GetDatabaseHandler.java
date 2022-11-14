package de.cookiemc.database.handler.other.database;

import de.cookiemc.document.Document;
import de.cookiemc.http.api.*;
import de.cookiemc.http.api.*;

import javax.annotation.Nonnull;

@HttpRouter("database/findAll")
public class GetDatabaseHandler {


    @HttpEndpoint(
            method = HttpMethod.GET,
            permission = "cloud.database.access"
    )
    public void handle(@Nonnull HttpContext context) {
        context.getResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(Document.newJsonDocument())
                .setStatusCode(HttpCodes.OK)
                .getContext()
                .closeAfter(true)
                .cancelNext(true);
    }
}
