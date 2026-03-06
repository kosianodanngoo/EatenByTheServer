package io.github.kosianodangoo.eatenbytheserver.common.init;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class EBTSDamageTypes {
    public static final ResourceKey<DamageType> EATING = ResourceKey.create
            (Registries.DAMAGE_TYPE, EatenByTheServer.getResourceLocation("eating"));

    public static DamageSource eating(Level level) {
        try {
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(EATING));
        } catch (Exception e) {
            return level.damageSources().starve();
        }
    }
}
