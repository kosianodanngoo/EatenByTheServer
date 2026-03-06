package io.github.kosianodangoo.eatenbytheserver.common.hostile.skill;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.Burning;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;

public class BurningSkill extends AbstractSkill {
    @Override
    public void activate(HostileServer hostileServer) {
        if (hostileServer.level instanceof ServerLevel serverLevel) {
            serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), (livingEntity -> !hostileServer.ignorePredicate.test(livingEntity))).forEach(livingEntity -> {
                Burning burning = new Burning(hostileServer);
                burning.setPos(livingEntity.position());
                hostileServer.addProjectile(burning);
            });
        }
    }

    @Override
    public int getCoolTime() {
        return 40;
    }

    @Override
    public @NotNull Weight getWeight() {
        return Weight.of(10);
    }
}
