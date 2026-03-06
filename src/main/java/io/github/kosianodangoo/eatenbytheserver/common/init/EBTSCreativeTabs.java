package io.github.kosianodangoo.eatenbytheserver.common.init;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class EBTSCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EatenByTheServer.MOD_ID);
    public static List<RegistryObject<? extends Item>> ITEMS = new ArrayList<>();


    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TABS.register("eaten_by_the_server", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.eaten_by_the_server"))
            .icon(() -> EBTSItems.EDIBLE_SERVER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (var item : ITEMS) {
                    output.accept(item.get());
                }
            })
            .build());


    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
