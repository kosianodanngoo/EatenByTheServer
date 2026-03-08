package io.github.kosianodangoo.eatenbytheserver.common.entity;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.DisconnectionSweep;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServerHandler;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SystemInterface extends Monster implements IHostileServerSlave {
    public HostileServer hostileServer;
    public int attackCooltime = 0;

    public SystemInterface(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.hostileServer = HostileServerHandler.getHostileServer(level);
        if (this.hostileServer != null && this.hostileServer.systemInterface == null) {
            hostileServer.systemInterface = this;
        }
    }

    @Override
    public void setHostileServer(HostileServer hostileServer) {
        this.hostileServer = hostileServer;
    }

    @Override
    public HostileServer getHostileServer() {
        return this.hostileServer;
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .build();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource p_33034_) {
        return SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.BEACON_DEACTIVATE;
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() / 4;
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        super.remove(removalReason);
        HostileServer hostileServer = this.getHostileServer();
        if (!level().isClientSide() && hostileServer != null && hostileServer.systemInterface == this) {
            if (removalReason.shouldDestroy())
                hostileServer.damage(1);
            hostileServer.systemInterface = null;
        }
        this.hostileServer = null;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) {
            if (this.isAlive() && this.hostileServer != null && --attackCooltime <= 0) {
                Vec3 position = this.position();
                for (Player player : this.level().getEntitiesOfClass(Player.class, AABB.ofSize(position, 100, 100, 100))) {
                    if (this.hostileServer.ignorePredicate.test(player)) {
                        continue;
                    }
                    DisconnectionSweep disconnectionSweep = new DisconnectionSweep(this.hostileServer);
                    Vec3 randomizedPosition = position.offsetRandom(this.random, 6);
                    disconnectionSweep.setPos(randomizedPosition);
                    disconnectionSweep.setDelta(player.position().subtract(randomizedPosition).normalize());
                    this.hostileServer.addProjectile(disconnectionSweep);
                }
                attackCooltime = 5;
            } else if (hostileServer == null || hostileServer.systemInterface != this || hostileServer.removed) {
                this.discard();
            }
        }
        super.tick();
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }


    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource damageSource) {
        return false;
    }
}
