package de.cookiemc.node.config;

import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.driver.common.CloudMessages;
import de.cookiemc.http.ProtocolAddress;
import de.cookiemc.driver.node.config.DefaultNodeConfig;
import de.cookiemc.driver.node.config.JavaVersion;
import de.cookiemc.driver.node.config.ServiceCrashPrevention;
import de.cookiemc.driver.services.utils.ServiceProcessType;
import de.cookiemc.driver.uuid.PlayerLoginProcessing;
import de.cookiemc.node.NodeDriver;
import de.cookiemc.node.database.config.DatabaseConfiguration;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;


@Getter
@AllArgsConstructor
@Setter
public class MainConfiguration {

    private LogLevel logLevel;
    private ServiceProcessType serviceProcessType;
    private PlayerLoginProcessing playerLoginProcessing;
    private int proxyStartPort, spigotStartPort;
    private boolean uniqueIdCaching;
    private Collection<String> whitelistedPlayers;

    private ProtocolAddress[] httpListeners;
    private ServiceCrashPrevention serviceCrashPrevention;
    private Collection<JavaVersion> javaVersions;

    private DatabaseConfiguration databaseConfiguration;
    private DefaultNodeConfig nodeConfig;
    private CloudMessages messages;

    public Collection<JavaVersion> getJavaVersions() {
        return new ArrayList<>(javaVersions);
    }

    public static MainConfiguration getInstance() {
        return NodeDriver.getInstance().getConfigManager().getConfig();
    }

}
