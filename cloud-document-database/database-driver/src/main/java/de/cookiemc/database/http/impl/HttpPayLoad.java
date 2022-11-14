package de.cookiemc.database.http.impl;

import de.cookiemc.database.api.IPayLoad;
import de.cookiemc.document.Bundle;
import de.cookiemc.document.Document;
import de.cookiemc.http.api.HttpCodes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HttpPayLoad implements IPayLoad {

    private int statusCode;
    private String queryString;
    private String response;

    @Override
    public boolean isSuccess() {
        return statusCode == HttpCodes.OK;
    }

    @Override
    public Document toDocument() {
        return Document.newJsonDocument(response);
    }

    @Override
    public Bundle toBundle() {
        return Bundle.newJsonBundle(response);
    }
}
