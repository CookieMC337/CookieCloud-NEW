package de.cookiemc.node.config;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.task.Task;
import de.cookiemc.document.Document;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.driver.networking.protocol.packets.defaults.StorageUpdatePacket;
import de.cookiemc.driver.storage.INetworkDocumentStorage;
import de.cookiemc.node.NodeDriver;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class NodeNetworkDocumentStorage implements INetworkDocumentStorage {

	/**
	 * The raw data of this config stored in a document
	 */
	private Document rawData;

	public NodeNetworkDocumentStorage() {
		Logger.constantInstance().trace("Initializing NetworkDocumentStorage (Node-Side)...");
	}

	@Nonnull
	@Override
	public INetworkDocumentStorage setRawData(@Nonnull Document data) {
		rawData = data;
		update();
		return this;
	}

	@Override
	public void update() {
		NodeDriver.getInstance().getNetworkExecutor().sendPacketToAll(new StorageUpdatePacket(StorageUpdatePacket.StoragePayLoad.UPDATE, rawData));
	}

	@Override
	public void fetch() {
		rawData = DocumentFactory.newJsonDocument();
		Logger.constantInstance().trace("DriverStorage is now set up!");
	}

	@Override
	public Task<Document> fetchAsync() {
		return Task.callAsync(() -> {
			fetch();
			return rawData;
		});
	}
}
