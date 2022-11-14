package de.cookiemc.driver.storage;



import de.cookiemc.common.task.Task;
import de.cookiemc.document.Bundle;
import de.cookiemc.document.Document;
import de.cookiemc.document.IEntry;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;


public interface INetworkDocumentStorage {

	@Nonnull
    Document getRawData();

	@Nonnull
    INetworkDocumentStorage setRawData(@Nonnull Document rawData);

	@Nonnull
	default INetworkDocumentStorage set(@Nonnull String path, @Nonnull Object value) {
		getRawData().set(path, value);
		return this;
	}

	default <T> T get(@Nonnull String path, @Nonnull Class<T> classOfT) {
		return getRawData().getInstance(path, classOfT);
	}

	default IEntry get(@Nonnull String path) {
		return getRawData().get(path);
	}

	default <T> T get(@Nonnull String path, @Nonnull Type classOfT) {
		return getRawData().getInstance(path, classOfT);
	}

	default <T> T get(@Nonnull String path, @Nonnull Class<T> classOfT, T defaultValue) {
		return getRawData().fallbackValue(defaultValue).getInstance(path, classOfT);
	}
	default Bundle getBundle(@Nonnull String path) {
		return getRawData().getBundle(path);
	}

	/**
	 * Synchronizes all global config instances with the properties of this instance
	 */
	void update();

	/**
	 * Fetches the data
	 */
	void fetch();

	Task<Document> fetchAsync();
}
