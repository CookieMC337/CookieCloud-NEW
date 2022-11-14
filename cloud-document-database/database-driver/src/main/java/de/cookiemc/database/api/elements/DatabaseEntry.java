package de.cookiemc.database.api.elements;

import de.cookiemc.document.Document;

public interface DatabaseEntry extends Document {

    void setDocument(Document document);

    String getId();

    void setId(String id);
}
