package de.cookiemc.driver.node.data;

import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.networking.protocol.codec.buf.PacketBuffer;
import de.cookiemc.driver.networking.protocol.packets.BufferState;
import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.management.ManagementFactory;


@NoArgsConstructor
@Getter
public class DefaultNodeCycleData implements INodeCycleData {

	private static Long startupTime;

	static {
		current(); // init management
	}

	@Nonnull
	public static DefaultNodeCycleData current() {
		OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

		float cpuUsage = (float) system.getSystemCpuLoad() * 100f;
		int cores = system.getAvailableProcessors();
		long maxMemory = system.getTotalPhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes
		long freeRam = system.getFreePhysicalMemorySize() / 1024 / 1024; // bytes -> kilobytes -> megabytes
		if (startupTime == null) {
			startupTime = System.currentTimeMillis();
		}

		return new DefaultNodeCycleData(cpuUsage, cores, maxMemory, freeRam);
	}

	private float cpuUsage; // cpu usage in percent
	private int cores; // the amount of cores the machine of the node has
	private long maxRam; // the ram the machine of the node has in megabytes
	private long freeRam; // the ram the machine of the node has left in megabytes
	private int latency; // the amount of time it takes to send a packet from the node to the server in ms
	private long timestamp;

	public DefaultNodeCycleData(float cpuUsage, int cores, long maxRam, long freeRam) {
		this.cpuUsage = cpuUsage;
		this.cores = cores;
		this.maxRam = maxRam;
		this.freeRam = freeRam;
		this.latency = -1;
	}


	@Override
	public void applyBuffer(BufferState state, @NotNull PacketBuffer buf) throws IOException {

		switch (state) {

			case READ:
				cpuUsage = buf.readFloat();
				cores = buf.readVarInt();
				maxRam = buf.readVarInt();
				freeRam = buf.readVarInt();
				timestamp = buf.readLong();
				latency = (int) (System.currentTimeMillis() - timestamp);
				break;

			case WRITE:
				buf.writeFloat(cpuUsage);
				buf.writeVarInt(cores);
				buf.writeVarLong(maxRam);
				buf.writeVarLong(freeRam);
				buf.writeLong(System.currentTimeMillis());
				break;
		}
	}

	@Override
	public long getUpTime() {
		return System.currentTimeMillis() - getStartupTime();
	}

	@Override
	public long getStartupTime() {
		return startupTime;
	}

	public boolean hasTimedOut() {
		long lastCycleDelay = System.currentTimeMillis() - timestamp - 30; // we allow 30ms delay
		int lostCycles = (int) lastCycleDelay / CloudDriver.NODE_PUBLISH_INTERVAL;
		if (lostCycles > 0) {
			CloudDriver.getInstance().getLogger().trace("Node timeout: lost {} cycles ({}ms)", lostCycles, lastCycleDelay);
		}
		return lostCycles >= CloudDriver.NODE_MAX_LOST_CYCLES;
	}

	@Override
	public String toString() {
		return "NodeCycleData[" + "cpuUsage=" + cpuUsage + " cores=" + cores + " maxRam=" + maxRam + " freeRam=" + freeRam + " latency=" + latency + "ms]";
	}
}
