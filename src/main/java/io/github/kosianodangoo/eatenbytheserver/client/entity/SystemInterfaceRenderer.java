package io.github.kosianodangoo.eatenbytheserver.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.kosianodangoo.eatenbytheserver.client.helper.RenderHelper;
import io.github.kosianodangoo.eatenbytheserver.common.entity.SystemInterface;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SystemInterfaceRenderer extends EntityRenderer<SystemInterface> {
    public SystemInterfaceRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull SystemInterface entity, float yaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packagedLight) {
        super.render(entity, yaw, partialTick, poseStack, bufferSource, packagedLight);

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2, 0);
        poseStack.scale(4, 4, 4);

        long millis = Util.getMillis();
        float progress = (float) Math.sin(millis / 5000f);
        float progressSquared = progress * progress;
        float rotation = progressSquared * 10;
        poseStack.mulPose(Axis.YP.rotation(rotation * 2));
        poseStack.mulPose(Axis.ZN.rotation(rotation));
        poseStack.mulPose(Axis.XP.rotation(millis / 300f));

        if (entity.deathTime > 0) {
            float scale = (20f - entity.deathTime - partialTick) / 20f;
            poseStack.scale(scale, scale, scale);
        }

        float brightnessProgress = (float) Math.sin(2.4 + millis / 283f);
        float brightness = brightnessProgress * brightnessProgress * 0.5f;
        RenderHelper.renderBox(poseStack, bufferSource.getBuffer(RenderType.debugQuads()), false, (vertexConsumer -> vertexConsumer.color(brightness, brightness, brightness, 1f)));

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SystemInterface systemInterface) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
