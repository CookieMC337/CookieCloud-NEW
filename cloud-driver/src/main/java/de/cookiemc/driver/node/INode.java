package de.cookiemc.driver.node;

import de.cookiemc.common.task.Task;
import de.cookiemc.driver.common.IClusterObject;
import de.cookiemc.driver.networking.INetworkExecutor;
import de.cookiemc.driver.networking.protocol.packets.NetworkResponseState;
import de.cookiemc.driver.node.config.INodeConfig;
import de.cookiemc.driver.node.data.INodeCycleData;
import de.cookiemc.driver.services.ICloudServer;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * The {@link INode} represents the big Cluster-Nodes in the API
 * You can retrieve their {@link INodeConfig} or their last {@link INodeCycleData},
 * start a certain {@link ICloudServer}, stop a certain {@link ICloudServer}
 * or even shutdown the node or directly message them
 *
 * @author CookieMC337
 * @version SNAPSHOT-1.1
 */
public interface INode extends IClusterObject<INode>, INetworkExecutor {

    /**
     * The name of the node object using {@link #getConfig()}
     */
    @Nonnull
    default String getName() {
        return getConfig().getNodeName();
    }

    /**
     * Returns the {@link INodeConfig} of this node
     * @see INodeConfig
     */
    @Nonnull
    INodeConfig getConfig();

    /**
     * Returns the {@link INodeCycleData} of this node
     * @see INodeCycleData
     */
    @Nonnull
    INodeCycleData getLastCycleData();

    /**
     * Sets the {@link INodeCycleData} for this node
     * <b>Caution:</b> Only use if you know why you do it
     *
     * @param lastCycleData the cycle data
     */
    void setLastCycleData(@Nonnull INodeCycleData lastCycleData);

    /**
     * Returns if this node has enough memory left to
     * start a certain {@link ICloudServer}
     *
     * @param cloudServer the server to check
     */
    boolean hasEnoughMemoryToStart(@Nonnull ICloudServer cloudServer);

    /**
     * Adds up the memory used by every single {@link ICloudServer}
     * and returns that value
     */
    long getUsedMemoryByServices();

    /**
     * Shuts down this node and if head-node
     * migrates to another node
     */
    void shutdown();

    /**
     * Stops a certain {@link ICloudServer} on this {@link INode}.
     * If the {@link ICloudServer} is not running on this Node
     * nothing will happen and the request is being ignored
     *
     * @param server the server to stop
     */
    void stopServer(@Nonnull ICloudServer server);

    /**
     * Starts a certain {@link ICloudServer} on this {@link INode}.
     * If the {@link ICloudServer} is not running on this Node
     * nothing will happen and the request is being ignored
     *
     * @param server the server to start
     */
    void startServer(@Nonnull ICloudServer server);

    /**
     * Starts a certain {@link ICloudServer} on this {@link INode} async.
     * If the {@link ICloudServer} is not running on this Node
     * nothing will happen and the request is being ignored
     *
     * @param server the server to start
     * @return task instance containing the response for stopping
     */
    @Nonnull
    Task<NetworkResponseState> startServerAsync(@Nonnull ICloudServer server);

    /**
     * Stops a certain {@link ICloudServer} on this {@link INode} async.
     * If the {@link ICloudServer} is not running on this Node
     * nothing will happen and the request is being ignored
     *
     * @param server the server to stop
     * @return task instance containing the response for stopping
     */
    @Nonnull
    Task<NetworkResponseState> stopServerAsync(@Nonnull ICloudServer server);

    /**
     * Retrieves a {@link Collection} of all running {@link ICloudServer}s
     * on this {@link INode}.
     * If no servers are running the {@link Collection} will be empty.
     */
    @Nonnull
    Collection<ICloudServer> getRunningServers();

    @Nonnull
    Task<Collection<ICloudServer>> getRunningServersAsync();

}
