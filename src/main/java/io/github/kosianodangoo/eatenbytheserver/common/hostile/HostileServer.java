package io.github.kosianodangoo.eatenbytheserver.common.hostile;


import io.github.kosianodangoo.eatenbytheserver.Config;
import io.github.kosianodangoo.eatenbytheserver.common.entity.IHostileServerSrave;
import io.github.kosianodangoo.eatenbytheserver.common.entity.SystemInterface;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.skill.AbstractSkill;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.skill.BurningSkill;
import io.github.kosianodangoo.eatenbytheserver.common.hostile.skill.DisconnectionSweepSkill;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSDamageTypes;
import io.github.kosianodangoo.eatenbytheserver.common.init.EBTSEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HostileServer {
    public static final Component EAT_THE_SERVER_MESSAGE = Component.translatable("boss.eaten_by_the_server.eat_the_server");
    public WeightedRandomList<AbstractSkill> skills = null;
    public List<ServerProjectile> projectiles = new ArrayList<>();
    public List<ServerProjectile> projectilesToRemove = new ArrayList<>();
    public List<ServerProjectile> projectilesToAdd = new ArrayList<>();
    public ServerBossEvent bossEvent;
    public Predicate<Entity> ignorePredicate = (entity -> entity == null || entity instanceof IHostileServerSrave);
    public SystemInterface systemInterface;
    public boolean shouldEatCreative = Config.SHOULD_EAT_CREATIVE.get();
    public Level level;
    public boolean dead = false;
    public int deathTick = 0;
    public float maxHp = 100;
    public float hp = maxHp;
    public int eatenCount = 0;
    public int cooltime = 0;
    public boolean hostile = false;
    public boolean isTicking = false;


    public HostileServer(Level level) {
        this.level = level;
        if (!shouldEatCreative) {
            ignorePredicate = ignorePredicate.or((entity -> entity instanceof ServerPlayer serverPlayer && (!serverPlayer.gameMode.isSurvival() || serverPlayer.tickCount < 30)));
        }
        if (level instanceof ServerLevel serverLevel) {
            this.bossEvent = new ServerBossEvent(Component.translatable("boss.eaten_by_the_server.eaten_by_the_server"), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);

            HostileServerSavedData savedData = HostileServerSavedData.getOrCreate(serverLevel, this);
            savedData.onSpawn(this);
            savedData.setDirty();
        }
    }

    public void initBoss() {
        if (level instanceof ServerLevel serverLevel) {
            bossEvent.removeAllPlayers();
            for (ServerPlayer player : serverLevel.getPlayers((ignored) -> true)) {
                bossEvent.addPlayer(player);
            }
            skills = WeightedRandomList.create(new DisconnectionSweepSkill(), new BurningSkill());
        }
        HostileServerHandler.addTickingHostileServer(this.level, this);
    }

    public void onChanged() {
        if (level instanceof ServerLevel serverLevel) {
            HostileServerSavedData savedData = HostileServerSavedData.getOrCreate(serverLevel, this);
            savedData.setDirty();
        }
    }

    public void damage(float damage) {
        this.setHp(this.getHp() - damage);
    }

    public float getHp() {
        return this.hp;
    }

    public void setHp(float hp) {
        if (this.hp == hp) {
            return;
        }
        this.hp = hp;
        if (hp <= 0) {
            this.dead = true;
        }
        this.onChanged();
    }

    public float getMaxHp() {
        return this.maxHp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void handleEatTheServer() {
        eatenCount++;
        if (eatenCount >= 10) {
            this.setHostile(true);
            onChanged();
        }
    }

    public int getEatenCount() {
        return this.eatenCount;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        if (!this.hostile && hostile) {
            initBoss();
        }
        this.hostile = hostile;
    }

    public void remove() {
        bossEvent.removeAllPlayers();
        projectilesToRemove.clear();
        projectiles.clear();
        skills = null;
        onChanged();
        HostileServerHandler.removeHostileServer(this.level);
    }

    public void die() {
        if (this.level instanceof ServerLevel serverLevel) {
            HostileServerSavedData savedData = HostileServerSavedData.getOrCreate(serverLevel, this);
            savedData.remove();
            serverLevel.players().forEach(serverPlayer -> {
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 0.4f, 1);
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.HOSTILE);
                FoodData foodData = serverPlayer.getFoodData();
                foodData.setFoodLevel(255);
                foodData.setSaturation(255);
                serverPlayer.setHealth(Float.MAX_VALUE);
            });
        }
        this.remove();

    }

    public void onPlayerJoined(Player player) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer) || !isHostile()) return;
        bossEvent.addPlayer(serverPlayer);
    }

    public void onPlayerLeaved(Player player) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer) || !isHostile()) return;
        bossEvent.removePlayer(serverPlayer);
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (dead) {
            if (deathTick >= 200) {
                die();
                return;
            }
            if (++deathTick < 100 && deathTick % 10 == 0) {
                bossEvent.setProgress(1);
                BossEvent.BossBarColor[] colors = BossEvent.BossBarColor.values();
                bossEvent.setColor(colors[level.random.nextInt(colors.length)]);
                serverLevel.players().forEach(serverPlayer ->
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 1, 1)
                );
            } else if (deathTick >= 100) {
                bossEvent.setName(EAT_THE_SERVER_MESSAGE);
                if (deathTick % 4 == 0) {
                    serverLevel.players().forEach(serverPlayer ->
                        serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.HOSTILE, 0.1f, 1)
                    );
                }
                bossEvent.setProgress(level.random.nextFloat());
                BossEvent.BossBarColor[] colors = BossEvent.BossBarColor.values();
                bossEvent.setColor(colors[level.random.nextInt(colors.length)]);
            }
            return;
        }
        if (isHostile()) {
            BossEvent.BossBarColor[] colors = BossEvent.BossBarColor.values();
            bossEvent.setColor(colors[level.random.nextInt(colors.length)]);
            bossEvent.setProgress(getHp() / getMaxHp());
            if (serverLevel.getGameTime() % 20 == 0) {
                for (LivingEntity livingEntity : serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), (entity) -> (!ignorePredicate.test(entity)))) {
                    livingEntity.hurt(EBTSDamageTypes.eating(serverLevel), 1);
                    serverLevel.playSound(null, livingEntity, SoundEvents.GENERIC_EAT, SoundSource.HOSTILE, 1, 1);
                }
            }
            if (--cooltime <= 0) {
                Optional<AbstractSkill> skillOptional = skills.getRandom(serverLevel.random);
                skillOptional.ifPresent((skill) -> {
                    skill.activate(this);
                    cooltime = skill.getCoolTime();
                });
            }
            if (systemInterface == null) {
                ServerPlayer serverPlayer = serverLevel.getRandomPlayer();
                if (serverPlayer != null) {
                    SystemInterface systemInterface = new SystemInterface(EBTSEntities.SYSTEM_INTERFACE.get(), serverLevel);
                    RandomSource randomSource = serverLevel.random;
                    Vec3 direction = Vec3.directionFromRotation(randomSource.nextFloat() * -45f, randomSource.nextFloat() * 360f);
                    Vec3 pos = serverPlayer.position().add(direction.scale(20));
                    systemInterface.setPos(pos);
                    while (!serverLevel.noCollision(systemInterface) && systemInterface.getY() < (double) serverLevel.getMaxBuildHeight()) {
                        systemInterface.setPos(systemInterface.getX(), systemInterface.getY() + (double) 1.0F, systemInterface.getZ());
                    }
                    serverLevel.addFreshEntity(systemInterface);
                    this.systemInterface = systemInterface;
                }
            }
            isTicking = false;
            this.projectilesToRemove.forEach(this::removeProjectile);
            this.projectilesToRemove.clear();
            this.projectilesToAdd.forEach(this::addProjectile);
            this.projectilesToAdd.clear();
            isTicking = true;
            this.projectiles.forEach((ServerProjectile::tick));
            isTicking = false;
        }
    }

    public void removeProjectile(ServerProjectile projectile) {
        if (isTicking) {
            projectilesToRemove.add(projectile);
            return;
        }
        projectiles.remove(projectile);
    }

    public void addProjectile(ServerProjectile projectile) {
        if (isTicking) {
            projectilesToAdd.add(projectile);
            return;
        }
        projectiles.add(projectile);
    }
}
