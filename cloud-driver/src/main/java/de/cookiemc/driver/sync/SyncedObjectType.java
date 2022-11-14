package de.cookiemc.driver.sync;

import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.player.ICloudPlayer;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.task.bundle.ITaskGroup;
import lombok.Getter;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class SyncedObjectType<T extends IBufferObject> {

    public static final SyncedObjectType<ICloudPlayer> PLAYER
            = new SyncedObjectType<>(0x00, ICloudPlayer.class, String.class, UUID.class);

    public static final SyncedObjectType<ICloudServer> SERVER
            = new SyncedObjectType<>(0x01, ICloudServer.class, String.class, UUID.class);

    public static final SyncedObjectType<IServiceTask> TASK
            = new SyncedObjectType<>(0x02, IServiceTask.class, String.class);

    public static final SyncedObjectType<ITaskGroup> GROUP
            = new SyncedObjectType<>(0x03, ITaskGroup.class, String.class);

    public static final SyncedObjectType<INode> NODE
            = new SyncedObjectType<>(0x04, INode.class, String.class);

    public static SyncedObjectType<?>[] values() {
        return new SyncedObjectType[]{PLAYER, SERVER, TASK, GROUP, NODE};
    }

    public static SyncedObjectType<?> fromId(int id) {
        return Arrays.stream(values()).filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    private final int id;
    private final Class<T> objectClass;
    private final Class<?>[] acceptedQueryParameters;

    private SyncedObjectType(int id, Class<T> objectClass, Class<?>... acceptedQueryParameters) {
        this.id = id;
        this.objectClass = objectClass;
        this.acceptedQueryParameters = acceptedQueryParameters;
    }
}
