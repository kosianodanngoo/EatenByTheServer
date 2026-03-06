package io.github.kosianodangoo.eatenbytheserver.common.hostile;

import io.github.kosianodangoo.eatenbytheserver.EatenByTheServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class HostileServerSavedData extends SavedData {
    public static final String REMOVED_TAG = "removed";
    public static final String EATEN_COUNT_TAG = "eaten_count";
    public static final String HOSTILE_TAG = "hostile";
    public static final String HP_TAG = "hp";

    public boolean removed = false;

    public HostileServer hostileServer;
    public ServerLevel serverLevel;

    public HostileServerSavedData() {
        super();
    }

    public static HostileServerSavedData getOrCreate(ServerLevel serverLevel, HostileServer hostileServer) {
        HostileServerSavedData savedData = serverLevel.getDataStorage().computeIfAbsent((compoundTag -> {
            HostileServerSavedData savedData1 = new HostileServerSavedData();
            savedData1.serverLevel = serverLevel;
            savedData1.hostileServer = hostileServer;
            savedData1.load(compoundTag);
            return savedData1;
        }), HostileServerSavedData::new, EatenByTheServer.MOD_ID + "_hostile_server");
        savedData.serverLevel = serverLevel;
        return savedData;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        compoundTag.putBoolean(REMOVED_TAG, removed);
        if (this.hostileServer != null) {
            compoundTag.putInt(EATEN_COUNT_TAG, this.hostileServer.getEatenCount());
            compoundTag.putBoolean(HOSTILE_TAG, this.hostileServer.isHostile());
            compoundTag.putFloat(HP_TAG, this.hostileServer.getHp());
        }
        return compoundTag;
    }

    public void load(@NotNull CompoundTag compoundTag) {
        this.removed = compoundTag.getBoolean(REMOVED_TAG);
        if (this.removed) {
            return;
        }
        if (hostileServer == null) {
            return;
        }
        this.hostileServer.setEatenCount(compoundTag.getInt(EATEN_COUNT_TAG));
        this.hostileServer.setHostile(compoundTag.getBoolean(HOSTILE_TAG));
        this.hostileServer.setHp(compoundTag.getFloat(HP_TAG));
    }

    public void remove() {
        this.removed = true;
    }

    public void onSpawn(HostileServer hostileServer) {
        this.removed = false;
        this.hostileServer = hostileServer;
    }
}
