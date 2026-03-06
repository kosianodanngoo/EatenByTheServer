package io.github.kosianodangoo.eatenbytheserver.common.item;

import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServerHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EdibleServerItem extends Item {
    public EdibleServerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity user) {
        if (!level.isClientSide()) {
            HostileServer hostileServer = HostileServerHandler.getOrCreateHostileServer(level);
            hostileServer.handleEatTheServer();
            if (user instanceof Player player) {
                FoodData foodData = player.getFoodData();
                foodData.setFoodLevel(-255);
                foodData.setSaturation(-255);
            }
            user.sendSystemMessage(Component.translatable("item.eaten_by_the_server.edible_server.access_denied", hostileServer.getEatenCount(), user.getDisplayName()));
        }
        return stack;
    }
}
