package io.github.kosianodangoo.eatenbytheserver.common.hostile;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Mod.EventBusSubscriber(modid = EatenByTheServer.MOD_ID)
public class HostileServerHandler {
    public static final Map<ResourceKey<Level>, HostileServer> hostileServers = new Object2ObjectOpenHashMap<>();
    public static final Map<ResourceKey<Level>, HostileServer> tickingHostileServers = new Object2ObjectOpenHashMap<>();


    public static @Nullable HostileServer getHostileServer(Level level) {
        return hostileServers.get(level.dimension());
    }

    public static HostileServer getOrCreateHostileServer(Level level) {
        return hostileServers.computeIfAbsent(level.dimension(), (key) -> new HostileServer(level));
    }

    public static void addTickingHostileServer(Level level, HostileServer hostileServer) {
        tickingHostileServers.put(level.dimension(), hostileServer);
    }

    public static void removeHostileServer(Level level) {
        hostileServers.remove(level.dimension());
        tickingHostileServers.remove(level.dimension());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        tickingHostileServers.forEach(((level, hostileServer) -> hostileServer.tick()));
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            getOrCreateHostileServer(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            HostileServer hostileServer = getHostileServer(level);
            if (hostileServer == null) return;
            hostileServer.remove();
        }
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        HostileServer hostileServer = getHostileServer(player.level());
        if (hostileServer != null) {
            hostileServer.onPlayerJoined(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        HostileServer hostileServerFrom = hostileServers.get(event.getFrom());
        if (hostileServerFrom != null) {
            hostileServerFrom.onPlayerLeaved(player);
        }
        HostileServer hostileServerTo = hostileServers.get(event.getTo());
        if (hostileServerTo != null) {
            hostileServerTo.onPlayerJoined(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeaved(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        HostileServer hostileServer = getHostileServer(player.level());
        if (hostileServer != null) {
            hostileServer.onPlayerLeaved(player);
        }
    }

}
