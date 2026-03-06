package io.github.kosianodangoo.eatenbytheserver.client.handler;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.client.entity.SystemInterfaceRenderer;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EatenByTheServer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EBTSClientModEventHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EBTSEntities.SYSTEM_INTERFACE.get(), SystemInterfaceRenderer::new);
    }
}
