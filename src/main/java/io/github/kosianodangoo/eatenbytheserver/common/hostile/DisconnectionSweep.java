package io.github.kosianodangoo.eatenbytheserver.common.hostile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DisconnectionSweep extends ServerProjectile {
    public Vec3 delta = Vec3.ZERO;
    public static final Component DISCONNECTION_MESSAGE = Component.translatable("boss.eaten_by_the_server.eaten_by_the_server.disconnect");

    public DisconnectionSweep(HostileServer hostileServer) {
        super(hostileServer);
    }

    public void setDelta(Vec3 delta) {
        this.delta = delta;
    }

    @Override
    public int getLifeTime() {
        return 100;
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.getPos().add(delta));
        if (this.serverLevel != null) {
            serverLevel.playSound(null, BlockPos.containing(pos), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 0.25f, 1);
            this.serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.pos.x, this.pos.y, this.pos.z, 2, 0.5, 0.5, 0.5, 0);
            this.serverLevel.getEntities(EntityTypeTest.forClass(ServerPlayer.class), AABB.ofSize(pos, 1.5f, 1.5f, 1.5f), (serverPlayer) -> !this.hostileServer.ignorePredicate.test(serverPlayer) && !serverPlayer.hasDisconnected()).forEach((serverPlayer -> {
                Component message = Component.translatable("boss.eaten_by_the_server.eaten_by_the_server.disconnect.player", serverPlayer.getDisplayName());
                serverPlayer.connection.disconnect(DISCONNECTION_MESSAGE);
                serverLevel.getServer().getPlayerList().broadcastSystemMessage(message, false);
            }));
        }
    }
}
