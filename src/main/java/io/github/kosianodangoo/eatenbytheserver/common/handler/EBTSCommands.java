package io.github.kosianodangoo.eatenbytheserver.common.handler;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServer;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.HostileServerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EatenByTheServer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EBTSCommands {
    public static ArgumentBuilder<CommandSourceStack, ?> HOSTILE_SERVER_COMMAND;

    public static ArgumentBuilder<CommandSourceStack, ?> HOSTILE_SERVER_HP_COMMAND;
    public static ArgumentBuilder<CommandSourceStack, ?> HOSTILE_SERVER_EATEN_COUNT_COMMAND;
    public static ArgumentBuilder<CommandSourceStack, ?> HOSTILE_SERVER_HOSTILE_COMMAND;

    static {
        HOSTILE_SERVER_HP_COMMAND = Commands.literal("hp").then(
                Commands.literal("set").then(
                        Commands.argument("hp", FloatArgumentType.floatArg()).executes(ctx -> {
                            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
                            float hp = FloatArgumentType.getFloat(ctx, "hp");
                            HostileServerHandler.getOrCreateHostileServer(serverLevel).setHp(hp);
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.hp.set.success", serverLevel.dimension().location().toString(), hp), true);
                            return 1;
                        })
                )
        ).executes(ctx -> {
            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
            HostileServer hostileServer = HostileServerHandler.getHostileServer(serverLevel);
            if (hostileServer == null) {
                ctx.getSource().sendFailure(Component.translatable("commands.eaten_by_the_server.hostile_server.no_hostile_server", serverLevel.dimension().location().toString()));
                return 0;
            }
            float hp = hostileServer.getHp();
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.hp.get.success", serverLevel.dimension().location().toString(), hp), true);
            return 1;
        });

        HOSTILE_SERVER_EATEN_COUNT_COMMAND = Commands.literal("eatenCount").then(
                Commands.literal("set").then(
                        Commands.argument("count", IntegerArgumentType.integer()).executes(ctx -> {
                            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
                            int eatenCount = IntegerArgumentType.getInteger(ctx, "count");
                            HostileServerHandler.getOrCreateHostileServer(serverLevel).setEatenCount(eatenCount);
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.eaten_count.set.success", serverLevel.dimension().location().toString(), eatenCount), true);
                            return 1;
                        })
                )
        ).executes(ctx -> {
            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
            HostileServer hostileServer = HostileServerHandler.getHostileServer(serverLevel);
            if (hostileServer == null) {
                ctx.getSource().sendFailure(Component.translatable("commands.eaten_by_the_server.hostile_server.no_hostile_server", serverLevel.dimension().location().toString()));
                return 0;
            }
            int eatenCount = hostileServer.getEatenCount();
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.eaten_count.get.success", serverLevel.dimension().location().toString(), eatenCount), true);
            return 1;
        });

        HOSTILE_SERVER_HOSTILE_COMMAND = Commands.literal("hostile").then(
                Commands.literal("set").then(
                        Commands.argument("isHostile", BoolArgumentType.bool()).executes(ctx -> {
                            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
                            boolean isHostile = BoolArgumentType.getBool(ctx, "isHostile");
                            HostileServerHandler.getOrCreateHostileServer(serverLevel).setHostile(isHostile);
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.hostile.set.success", serverLevel.dimension().location().toString(), isHostile), true);
                            return 1;
                        })
                )
        ).executes(ctx -> {
            ServerLevel serverLevel = DimensionArgument.getDimension(ctx, "dimension");
            HostileServer hostileServer = HostileServerHandler.getHostileServer(serverLevel);
            if (hostileServer == null) {
                ctx.getSource().sendFailure(Component.translatable("commands.eaten_by_the_server.hostile_server.no_hostile_server", serverLevel.dimension().location().toString()));
                return 0;
            }
            boolean isHostile = hostileServer.isHostile();
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.eaten_by_the_server.hostile_server.hostile.get.success", serverLevel.dimension().location().toString(), isHostile), true);
            return 1;
        });

        HOSTILE_SERVER_COMMAND = Commands.literal("hostileServer").then(
                Commands.argument("dimension", DimensionArgument.dimension())
                        .then(HOSTILE_SERVER_HP_COMMAND)
                        .then(HOSTILE_SERVER_EATEN_COUNT_COMMAND)
                        .then(HOSTILE_SERVER_HOSTILE_COMMAND)
        );
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("eaten_by_the_server").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(HOSTILE_SERVER_COMMAND)
        );
    }
}
