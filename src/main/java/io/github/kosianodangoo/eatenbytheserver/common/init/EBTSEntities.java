package io.github.kosianodangoo.eatenbytheserver.common.init;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.common.entity.SystemInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EBTSEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EatenByTheServer.MOD_ID);

    public static final RegistryObject<EntityType<SystemInterface>> SYSTEM_INTERFACE = register("system_interface", () ->
            EntityType.Builder.of(SystemInterface::new, MobCategory.MONSTER)
                    .sized(4F, 4F)
                    .clientTrackingRange(10)
                    .canSpawnFarFromPlayer()
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType.Builder<T>> type) {
        return ENTITY_TYPES.register(name, () -> type.get().build(EatenByTheServer.MOD_ID + ":" + name));
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
