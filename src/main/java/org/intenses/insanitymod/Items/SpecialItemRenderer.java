package org.intenses.insanitymod.Items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.intenses.insanitymod.Items.SpecialItem;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SpecialItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SpecialItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType transformType,
                             @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLight,
                             int combinedOverlay) {
        // Получаем текущий режим предмета
        int mode = SpecialItem.getMode(stack);

        // Определяем путь к текстуре в зависимости от режима
        ResourceLocation texture;
        switch (mode) {
            case 0:
                texture = new ResourceLocation("insanitymod", "textures/item/special_item_strength.png");
                break;
            case 1:
                texture = new ResourceLocation("insanitymod", "textures/item/special_item_invisibility.png");
                break;
            case 2:
                texture = new ResourceLocation("insanitymod", "textures/item/special_item_speed.png");
                break;
            default:
                texture = new ResourceLocation("insanitymod", "textures/item/special_item_strength.png");
        }

        // Получаем стандартный рендерер предметов
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0); // seed = 0

        // Рендерим предмет с кастомной текстурой
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(texture));
        itemRenderer.renderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
    }

    public static void register(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new SpecialItemRenderer();
            }
        });
    }
}