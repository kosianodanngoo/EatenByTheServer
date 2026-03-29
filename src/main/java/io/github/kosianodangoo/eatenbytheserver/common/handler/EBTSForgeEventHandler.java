package io.github.kosianodangoo.eatenbytheserver.common.handler;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSDamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EatenByTheServer.MOD_ID)
public class EBTSForgeEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().is(EBTSDamageTypes.EATING)) {
            event.setCanceled(false);
            LivingEntity livingEntity = event.getEntity();
            if (livingEntity.isAlive()) {
                livingEntity.setHealth(Float.MIN_VALUE);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().is(EBTSDamageTypes.EATING)) {
            event.setCanceled(false);
            if (event.getAmount() < 0) {
                event.setAmount(1);
            }
        }
    }
}
