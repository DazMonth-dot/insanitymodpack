package org.intenses.insanitymod.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.intenses.insanitymod.Items.SpecialItemRenderer;

import java.util.function.Consumer;

public class SpecialItem extends Item {
    public SpecialItem(Properties properties) {
        super(properties);
    }

    public static void setActive(ItemStack stack, boolean active) {
        stack.getOrCreateTag().putBoolean("isActive", active);
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt("mode", mode);
    }

    public static boolean isActive(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("isActive");
    }

    public static int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        SpecialItemRenderer.register(consumer);
    }
}