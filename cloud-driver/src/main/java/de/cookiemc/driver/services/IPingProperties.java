package de.cookiemc.driver.services;

import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface IPingProperties extends IBufferObject {

    void setMotd(@NotNull String motd);

    @NotNull
    String getMotd();

    @Nullable
    String getVersionText();

    void setVersionText(@Nullable String versionText);

    @NotNull
    String[] getPlayerInfo();

    void setPlayerInfo(@NotNull String... playerInfo);

    void addPlayerInfo(@NotNull String... playerInfo);

    @Nullable
    String getServerIconUrl();

    void setServerIconUrl(@NotNull String url);

    boolean isUsePlayerPropertiesOfService();

    boolean isCombineAllProxiesIfProxyService();

    void setCombineAllProxiesIfProxyService(boolean state);

    void setUsePlayerPropertiesOfService(boolean state);

    void setCustomPlayerProperties(int maxPlayers, int onlinePlayers);

    int getCustomMaxPlayers();

    int getCustomOnlinePlayers();
}
