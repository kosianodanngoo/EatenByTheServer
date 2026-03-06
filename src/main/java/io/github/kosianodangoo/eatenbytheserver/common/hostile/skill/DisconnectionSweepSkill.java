package io.github.kosianodangoo.eatenbytheserver.common.hostile.skill;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.DisconnectionSweep;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DisconnectionSweepSkill extends AbstractSkill {
    @Override
    public void activate(HostileServer hostileServer) {
        if (hostileServer.level instanceof ServerLevel serverLevel) {
            RandomSource randomSource = serverLevel.random;
            serverLevel.getPlayers(serverPlayer -> !hostileServer.ignorePredicate.test(serverPlayer)).forEach(serverPlayer -> {
                DisconnectionSweep disconnectionSweep = new DisconnectionSweep(hostileServer);
                disconnectionSweep.setDelta(Vec3.directionFromRotation(randomSource.nextFloat() * 180 - 90, randomSource.nextFloat() * 360));
                disconnectionSweep.setPos(serverPlayer.position().add(disconnectionSweep.delta.scale(-40)));
                hostileServer.addProjectile(disconnectionSweep);
            });
        }
    }

    @Override
    public int getCoolTime() {
        return 15;
    }

    @Override
    public @NotNull Weight getWeight() {
        return Weight.of(20);
    }
}
