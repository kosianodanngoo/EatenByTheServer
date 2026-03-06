package io.github.kosianodangoo.eatenbytheserver.common.init;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.common.item.EdibleServerItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EBTSItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EatenByTheServer.MOD_ID);

    public static final RegistryObject<Item> EDIBLE_SERVER = register("edible_server", () ->
            new EdibleServerItem(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(255).saturationMod(1).build())), true);


    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> itemSupplier, boolean isCreativeTab) {
        RegistryObject<T> registryObject = ITEMS.register(name, itemSupplier);
        if (isCreativeTab) {
            EBTSCreativeTabs.ITEMS.add(registryObject);
        }
        return registryObject;
    }


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
