package io.github.kosianodangoo.eatenbytheserver.common.handler;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.common.entity.SystemInterface;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EatenByTheServer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EBTSModEventHandler {
    @SubscribeEvent
    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(EBTSEntities.SYSTEM_INTERFACE.get(), SystemInterface.createAttributes());
    }
}
