package de.cookiemc.driver.networking.protocol.codec.buf.defaults;

import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BufferedDocument implements IBuffered<BufferedDocument, Document> {

    private Document wrapped;

    @Override
    public void setWrapped(Document wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Class<BufferedDocument> getWrapperClass() {
        return BufferedDocument.class;
    }

    @Override
    public Document read(PacketBuffer buffer) {
        return buffer.readDocument();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeDocument(wrapped);
    }
}
