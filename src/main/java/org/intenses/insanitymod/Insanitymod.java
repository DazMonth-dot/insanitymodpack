package org.intenses.insanitymod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Items.SpecialItem;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.File;
import java.util.UUID;

@Mod(Insanitymod.MOD_ID)
public class Insanitymod {
    public static final String MOD_ID = "insanitymod";

    public static final Logger LOGGER = LogUtils.getLogger();

    // UUID для модификаторов атрибутов
    private static final UUID MAX_HEALTH_MODIFIER_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC");
    private static final String MAX_HEALTH_MODIFIER_NAME = "insanity_max_health_mod";
    private static final UUID MAX_FEATHERS_MODIFIER_UUID = UUID.fromString("6E7F1CB3-2A92-4F1A-8D39-1123AB5678CD");
    private static final String MAX_FEATHERS_MODIFIER_NAME = "insanity_max_feathers_mod";

    //Key mappings
    public static final KeyMapping ACTIVATE_KEY = new KeyMapping(
            "key.insanitymod.activate", // Уникальный ключ для перевода
            GLFW.GLFW_KEY_G,            // Клавиша G
            "category.insanitymod"      // Категория в настройках управления
    );

    public static final KeyMapping SWITCH_MODE_KEY = new KeyMapping(
            "key.insanitymod.switch_mode", // Уникальный ключ для перевода
            GLFW.GLFW_KEY_H,               // Клавиша H
            "category.insanitymod"         // Категория в настройках управления
    );
    // Регистрация предметов
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("special_item",
            () -> new SpecialItem(new Item.Properties().stacksTo(1)));

    public Insanitymod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ITEMS.register(modEventBus); // Регистрируем предметы
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Insanitymod initialized");
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_KEY);
        event.register(SWITCH_MODE_KEY);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        setPlayerAttributes(event.getEntity());
        processFirstJoin(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        setPlayerAttributes(event.getEntity());
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        SoulLampCheck.ApplyEffect(event);
        // Здесь можно добавить дополнительную логику, если нужно
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            File worldFolder = serverLevel.getServer().getWorldPath(LevelResource.ROOT).toFile();
            File serverConfigFolder = new File(worldFolder, "serverconfig");
            deleteFilesInFolder(serverConfigFolder);
        }
    }

    private static void deleteFilesInFolder(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    private void processFirstJoin(net.minecraft.world.entity.player.Player player) {
        CompoundTag persistentRoot = player.getPersistentData();
        CompoundTag modData = persistentRoot.contains(MOD_ID, CompoundTag.TAG_COMPOUND)
                ? persistentRoot.getCompound(MOD_ID)
                : new CompoundTag();

        if (!modData.getBoolean("first_join")) {
            setPlayerAttributes(player);
            player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
            modData.putBoolean("first_join", true);
            persistentRoot.put(MOD_ID, modData);
        }
    }

    private void setPlayerAttributes(net.minecraft.world.entity.player.Player player) {
        if (player != null && !player.level.isClientSide()) {
            // Модификатор максимального здоровья
            applyModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                    MAX_HEALTH_MODIFIER_UUID, MAX_HEALTH_MODIFIER_NAME, -14.0f);

            // Модификатор максимальной выносливости (feathers)
            Attribute featherAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("feathers", "max_feathers"));
            if (featherAttr != null) {
                applyModifier(player, featherAttr, MAX_FEATHERS_MODIFIER_UUID, MAX_FEATHERS_MODIFIER_NAME, -18.0f);
            } else {
                LOGGER.warn("[INSANITY] Attribute 'feathers:max_feathers' not found for player {}", player.getScoreboardName());
            }
        }
    }

    private static void applyModifier(net.minecraft.world.entity.player.Player player, Attribute attribute, UUID modifierUUID, String modifierName, float amount) {
        AttributeInstance attrInstance = player.getAttribute(attribute);
        if (attrInstance == null) {
            LOGGER.warn("[INSANITY] Attribute {} not found for player {}", attribute.getDescriptionId(), player.getScoreboardName());
            return;
        }

        AttributeModifier existingModifier = attrInstance.getModifier(modifierUUID);
        if (existingModifier != null) {
            attrInstance.removeModifier(modifierUUID);
            LOGGER.info("[INSANITY] Removed existing modifier {} for player {}", modifierName, player.getScoreboardName());
        }

        AttributeModifier modifier = new AttributeModifier(modifierUUID, modifierName, amount, AttributeModifier.Operation.ADDITION);
        attrInstance.addPermanentModifier(modifier);
        LOGGER.info("[INSANITY] Applied modifier {} ({} {}) for player {}", modifierName, amount, attribute.getDescriptionId(), player.getScoreboardName());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Insanitymod client setup completed");
        }
    }
}