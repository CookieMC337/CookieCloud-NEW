package de.cookiemc.database.api;

import de.cookiemc.document.Bundle;
import de.cookiemc.document.Document;

public interface IPayLoad {

    boolean isSuccess();

    int getStatusCode();

    String getResponse();

    Document toDocument();

    Bundle toBundle();

    default <T> T as(Class<T> typeClass) {
        return toDocument().toInstance(typeClass);
    }
}
