package io.github.kosianodangoo.eatenbytheserver.common.hostile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ServerProjectile {
    public HostileServer hostileServer;
    public Level level;
    public ServerLevel serverLevel;
    public Vec3 pos = Vec3.ZERO;
    public int tickCount = 0;

    public ServerProjectile(HostileServer hostileServer) {
        this.hostileServer = hostileServer;
        this.setLevel(hostileServer.level);
    }

    abstract public int getLifeTime();

    public void tick() {
        if (getLifeTime() <= getTickCount()) {
            remove();
        }
        tickCount++;
    }

    public void remove() {
        hostileServer.removeProjectile(this);
    }

    public int getTickCount() {
        return tickCount;
    }

    public Vec3 getPos() {
        return pos;
    }

    public void setPos(Vec3 vec3) {
        this.pos = vec3;
    }

    public void setLevel(Level level) {
        this.level = level;
        this.serverLevel = level instanceof ServerLevel ? (ServerLevel) level : null;
    }
}
