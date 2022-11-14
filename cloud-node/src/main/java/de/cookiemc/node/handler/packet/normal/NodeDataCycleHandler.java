package de.cookiemc.node.handler.packet.normal;

import de.cookiemc.common.logging.Logger;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.node.packet.NodeCycleDataPacket;
import de.cookiemc.driver.networking.protocol.packets.PacketHandler;
import de.cookiemc.driver.networking.protocol.wrapped.PacketChannel;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.node.data.INodeCycleData;


public class NodeDataCycleHandler implements PacketHandler<NodeCycleDataPacket> {

	@Override
	public void handle(PacketChannel wrapper, NodeCycleDataPacket packet) {

		String name = packet.getNodeName();
		INodeCycleData data = packet.getData();
		Logger logger = CloudDriver.getInstance().getLogger();
		INodeManager nodeManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class);

		INode node = nodeManager.getNodeByNameOrNull(name);

		if (node == null) {
			logger.warn("Tried updating non-existent node {}! Data: {}", name, data);
			return;
		}
		logger.trace("Updated Node {} => {}", node.getName(), data);
		node.setLastCycleData(data);
	}
}
