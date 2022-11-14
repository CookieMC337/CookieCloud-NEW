package de.cookiemc.database.handler.other;


import de.cookiemc.common.logging.Logger;
import de.cookiemc.document.Document;
import de.cookiemc.http.HttpAddress;
import de.cookiemc.http.api.*;
import de.cookiemc.http.api.*;

import javax.annotation.Nonnull;

@HttpRouter("connection/close")
public class ConnectionCloseHandler {

    @HttpEndpoint(
            method = HttpMethod.POST,
            permission = "cloud.database.access"
    )
    public void handle(@Nonnull HttpContext context) {
        HttpRequest request = context.getRequest();
        String bodyString = request.getBodyString();
        Document document = Document.newJsonDocument(bodyString);

        String user = document.get("name").toString();
        HttpAddress address = HttpAddress.fromString(document.get("address").toString());
        Logger.constantInstance().info("User {} disconnected from {}", user, address.toString());

        context.getResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(Document.newJsonDocument("message", "Successfully closed connection!"))
                .setStatusCode(HttpCodes.OK)
                .getContext()
                .closeAfter(true)
                .cancelNext(true);
    }
}
