package io.github.kosianodangoo.eatenbytheserver.common.hostile;

import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public class Burning extends ServerProjectile {
    public Burning(HostileServer hostileServer) {
        super(hostileServer);
    }

    @Override
    public int getLifeTime() {
        return 100;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.serverLevel != null) {
            if (tickCount < 20) {
                this.serverLevel.sendParticles(ParticleTypes.FLAME, this.pos.x, this.pos.y, this.pos.z, 5, 0, 0, 0, 0.2);
                return;
            }
            serverLevel.playSound(null, BlockPos.containing(pos), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.2f, 0);
            this.serverLevel.sendParticles(ParticleTypes.FLAME, this.pos.x, this.pos.y, this.pos.z, 50, 0.5, 0.5, 0.5, 0.5);
            this.serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), AABB.ofSize(pos, 4, 4, 4), (livingEntity) -> !hostileServer.ignorePredicate.test(livingEntity)).forEach((livingEntity -> {
                float oldHealth = livingEntity.getHealth();
                livingEntity.setHealth(oldHealth - livingEntity.getMaxHealth() / 20);
                if (oldHealth > 0 && livingEntity.getHealth() <= 0) {
                    livingEntity.die(livingEntity.damageSources().inFire());
                }
                livingEntity.hurt(livingEntity.damageSources().inFire(), 5);
                livingEntity.hurt(EBTSDamageTypes.eating(serverLevel), 0);
            }));
        }
    }
}
