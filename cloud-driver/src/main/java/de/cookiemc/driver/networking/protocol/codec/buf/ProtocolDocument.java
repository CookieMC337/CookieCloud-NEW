package de.cookiemc.driver.networking.protocol.codec.buf;

import de.cookiemc.document.Document;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.document.DocumentWrapper;
import de.cookiemc.document.wrapped.WrappedDocument;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@NoArgsConstructor @AllArgsConstructor @Getter
public class ProtocolDocument implements WrappedDocument, IBufferObject {

	/**
	 * The wrapping document
	 */
	private Document targetDocument;

	@Override
	public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

		switch (state) {

			case READ:
				targetDocument = DocumentFactory.newJsonDocument(buf.readString());
				break;

			case WRITE:
				if (targetDocument == null) {
					buf.writeString("{}");
					return;
				}
				buf.writeString(targetDocument.asRawJsonString());
				break;
		}
	}

	@Override
	public DocumentWrapper<org.bson.Document> asBsonDocument() {
		return targetDocument.asBsonDocument();
	}

	@Override
	public DocumentWrapper<Gson> asGsonDocument() {
		return targetDocument.asGsonDocument();
	}

	@Override
	public Object getFallbackValue() {
		return targetDocument.getFallbackValue();
	}

	@Override
	public String toString() {
		return asRawJsonString();
	}

	@Override
	public Document fallbackValue(Object value) {
		return targetDocument.fallbackValue(value);
	}
}
