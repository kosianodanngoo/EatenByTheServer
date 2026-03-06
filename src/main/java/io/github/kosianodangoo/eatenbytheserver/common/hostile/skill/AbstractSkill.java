package io.github.kosianodangoo.eatenbytheserver.common.hostile.skill;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import net.minecraft.util.random.WeightedEntry;

public abstract class AbstractSkill implements WeightedEntry {
    abstract public void activate(HostileServer hostileServer);

    abstract public int getCoolTime();
}
