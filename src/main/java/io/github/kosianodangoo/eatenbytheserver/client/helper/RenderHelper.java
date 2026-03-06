package io.github.kosianodangoo.eatenbytheserver.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

import java.util.function.Function;

public class RenderHelper {
    public static void renderBox(PoseStack poseStack, VertexConsumer vertexConsumer, boolean renderInside, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        poseStack.pushPose();

        for (int i = 0; i < 4; i++) {
            Function<VertexConsumer, VertexConsumer> elementFiller = extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 1, 0));

            Matrix4f matrix4f = poseStack.last().pose();
            quad(matrix4f, vertexConsumer, -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    elementFiller);
            if (renderInside) {
                quad(matrix4f, vertexConsumer, -0.5f, 0.5f, -0.5f,
                        0.5f, 0.5f, -0.5f,
                        0.5f, 0.5f, 0.5f,
                        -0.5f, 0.5f, 0.5f,
                        elementFiller);
            }
            poseStack.mulPose(Axis.ZN.rotationDegrees(90));
        }
        for (int i = 0; i < 2; i++) {
            Matrix4f matrix4f = poseStack.last().pose();
            quad(matrix4f, vertexConsumer, -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 0, -1)));
            if (renderInside) {
                quad(matrix4f, vertexConsumer, -0.5f, 0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f,
                        0.5f, -0.5f, 0.5f,
                        -0.5f, -0.5f, 0.5f,
                        extraElementFiller.andThen(vertexConsumer1 -> vertexConsumer1.normal(0, 0, -1)));
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }

        poseStack.popPose();
    }

    public static void quad(Matrix4f matrix4f, VertexConsumer vertexConsumer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Function<VertexConsumer, VertexConsumer> extraElementFiller) {
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x0, y0, z0)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x1, y1, z1)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x2, y2, z2)).endVertex();
        extraElementFiller.apply(vertexConsumer.vertex(matrix4f, x3, y3, z3)).endVertex();
    }
}
