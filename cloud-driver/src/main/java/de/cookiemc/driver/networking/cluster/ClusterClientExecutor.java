package de.cookiemc.driver.networking.cluster;

import de.cookiemc.common.task.Task;
import de.cookiemc.document.Document;
import de.cookiemc.driver.networking.INetworkExecutor;


import io.netty.channel.Channel;

public interface ClusterClientExecutor extends INetworkExecutor {

    Channel getChannel();

    Document getData();

    String getName();

    boolean isAuthenticated();

    void setAuthenticated(boolean state);

    void setName(String name);

    default Task<Boolean> close() {
        Task<Boolean> task = Task.empty();
        getChannel().close().addListener(future -> {
            if (future.isSuccess()) {
                task.setResult(true);
            } else {
                task.setFailure(future.cause());
            }
        });
        return task;
    }
}
