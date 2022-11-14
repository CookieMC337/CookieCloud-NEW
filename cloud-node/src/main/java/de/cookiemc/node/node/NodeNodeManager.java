package de.cookiemc.node.node;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.node.base.DefaultNodeManager;
import de.cookiemc.driver.node.INode;
import de.cookiemc.node.NodeDriver;
import org.jetbrains.annotations.NotNull;


public class NodeNodeManager extends DefaultNodeManager {

    public NodeNodeManager() {
        this.registerNode((INode) CloudDriver.getInstance().thisSidesClusterParticipant());
    }


    @Override
    public void registerNode(@NotNull INode node) {
        if (getNode(node.getName()).isPresent()) {
            return;
        }
        this.allCachedNodes.add(node);
        if (node.getName().equalsIgnoreCase(CloudDriver.getInstance().thisSidesClusterParticipant().getName())) {
            return;
        }
        CloudDriver.getInstance().getLogger().info("The Node '§b" + node.getName() + "§7' has joined the cluster§8!");
    }


    @Override
    public void unRegisterNode(@NotNull INode node) {
        if (getNode(node.getName()).isNull()) {
            return;
        }
        this.allCachedNodes.remove(node);
        if (node.getName().equalsIgnoreCase(CloudDriver.getInstance().thisSidesClusterParticipant().getName())) {
            return;
        }
        NodeDriver.getInstance().getLogger().info("The Node '§b" + node.getName() + "§7' has left the cluster§8!");
    }

    @Override
    public INode getHeadNode() {
        return getAllCachedNodes().stream().filter(node -> !node.getConfig().isRemote()).findFirst().orElse(null);
    }

}
