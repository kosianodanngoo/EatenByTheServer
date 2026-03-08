package io.github.kosianodangoo.eatenbytheserver.common.entity;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;

public interface IHostileServerSlave {
    void setHostileServer(HostileServer hostileServer);

    HostileServer getHostileServer();
}
