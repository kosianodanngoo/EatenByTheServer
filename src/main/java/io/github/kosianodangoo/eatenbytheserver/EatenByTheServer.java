package io.github.kosianodangoo.eatenbytheserver;

import com.mojang.logging.LogUtils;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSCreativeTabs;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSEntities;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(EatenByTheServer.MOD_ID)
public class EatenByTheServer {
    public static final String MOD_ID = "eaten_by_the_server";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EatenByTheServer() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = context.getModEventBus();

        EBTSItems.register(modEventBus);
        EBTSCreativeTabs.register(modEventBus);
        EBTSEntities.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation getResourceLocation(String location) {
        return getResourceLocation(MOD_ID, location);
    }

    public static ResourceLocation getResourceLocation(String nameSpace, String location) {
        return new ResourceLocation(nameSpace, location);
    }
}
