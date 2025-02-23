package org.intenses.insanitymod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class SoulLampCheck {

    private static final ResourceLocation VEILED_EFFECT_ID = new ResourceLocation("engulfingdarkness", "veiled");

    private static boolean check(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            boolean hasLamp = CuriosApi.getCuriosHelper()
                    .findFirstCurio(player, Items.SOUL_LANTERN)
                    .isPresent();

            return hasLamp && !player.isInWater();
        }
        return false;
    }

    public static void ApplyEffect(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (check(event)) {
            MobEffect veiledEffect = Registry.MOB_EFFECT.get(VEILED_EFFECT_ID);
            if (veiledEffect != null) {
                MobEffectInstance effectInstance = new MobEffectInstance(veiledEffect, Integer.MAX_VALUE, 0, false, false);
                player.addEffect(effectInstance);
            }
        } else {
            player.removeEffect(Registry.MOB_EFFECT.get(VEILED_EFFECT_ID));
        }
    }
}
