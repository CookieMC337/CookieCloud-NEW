package de.cookiemc.driver.common;

import de.cookiemc.driver.networking.IPacketExecutor;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;

public interface IClusterObject<T> extends IPacketExecutor, ICopyableObject<T>, IdentityObject, IPlaceHolderObject, IBufferObject {

    String getName();
}
