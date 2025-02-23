package org.intenses.insanitymod.Items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

import static org.intenses.insanitymod.Insanitymod.*;

@Mod.EventBusSubscriber(modid = "insanitymod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            ItemStack curiousStack = ItemStack.EMPTY;

            // Проверка слота Curios
            boolean inCuriousSlot = false;
            try {
                Optional<ItemStack> foundStack = CuriosApi.getCuriosHelper()
                        .findFirstCurio(player, SPECIAL_ITEM.get())
                        .map(slotResult -> slotResult.stack());
                inCuriousSlot = foundStack.isPresent();
                if (inCuriousSlot) curiousStack = foundStack.get();
            } catch (NoClassDefFoundError e) {
                LOGGER.debug("Curios API not found, skipping Curious slot check.");
            }

            // Обработка активации (клавиша G)
            if (ACTIVATE_KEY.isDown() && (mainHand.getItem() instanceof SpecialItem
                    || offHand.getItem() instanceof SpecialItem
                    || inCuriousSlot)) {
                ItemStack stack = mainHand.getItem() instanceof SpecialItem ? mainHand
                        : offHand.getItem() instanceof SpecialItem ? offHand
                        : curiousStack;
                SpecialItem.setActive(stack, !SpecialItem.isActive(stack));
            }

            // Обработка переключения режима (клавиша H)
            if (SWITCH_MODE_KEY.isDown() && (mainHand.getItem() instanceof SpecialItem
                    || offHand.getItem() instanceof SpecialItem
                    || inCuriousSlot)) {
                ItemStack stack = mainHand.getItem() instanceof SpecialItem ? mainHand
                        : offHand.getItem() instanceof SpecialItem ? offHand
                        : curiousStack;
                int currentMode = SpecialItem.getMode(stack);
                SpecialItem.setMode(stack, (currentMode + 1) % 3);
            }
        }
    }
}
