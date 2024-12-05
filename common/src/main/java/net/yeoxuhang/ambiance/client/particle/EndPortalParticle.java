package net.yeoxuhang.ambiance.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EndPortalParticle extends TextureSheetParticle {

    EndPortalParticle(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f, 0.0, 0.0, 0.0);
        this.lifetime = 100;
        this.gravity = 0.0F;
        this.quadSize = 0.5F;
        this.setSize(1F, 1F);

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
    }

    @Override
    public void render(VertexConsumer vertexBuilder, Camera camera, float partialTicks) {
        this.alpha = 1.0F - Mth.clamp(((float) this.age + partialTicks) / (float) this.lifetime, 0.0F, 1.0F);
        Quaternion y = new Quaternion(new Vector3f(0, 1, 0), 0, true);
        this.renderParticle(vertexBuilder, camera, y, partialTicks, 0, 0.26F);
        this.renderParticle(vertexBuilder, camera, y, partialTicks, 0, -0.26F);
        Quaternion y1 = new Quaternion(new Vector3f(0, 1, 0), 180, true);
        this.renderParticle(vertexBuilder, camera, y1, partialTicks, 0, 0.26F);
        this.renderParticle(vertexBuilder, camera, y1, partialTicks, 0, -0.26F);
        Quaternion y2 = new Quaternion(new Vector3f(0, 1, 0), 90, true);
        this.renderParticle(vertexBuilder, camera, y2, partialTicks, 0.26F, 0);
        this.renderParticle(vertexBuilder, camera, y2, partialTicks, -0.26F, 0);
        Quaternion y3 = new Quaternion(new Vector3f(0, 1, 0), -90, true);
        this.renderParticle(vertexBuilder, camera, y3, partialTicks, 0.26F, 0);
        this.renderParticle(vertexBuilder, camera, y3, partialTicks, -0.26F, 0);
    }

    protected void renderParticle(VertexConsumer vertexBuilder, Camera camera, Quaternion quaternion, float partialTicks, float offsetX, float offsetZ) {
        renderRotatedQuad(vertexBuilder, camera, quaternion, partialTicks, offsetX, offsetZ);
    }

    protected void renderRotatedQuad(VertexConsumer vertexBuilder, Camera camera, Quaternion quaternion, float partialTicks, float offsetX, float offsetZ) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x + offsetX);
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y);
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z + offsetZ);
        this.renderQuad(vertexBuilder, quaternion, x, y, z, partialTicks);
    }

    protected void renderQuad(VertexConsumer buffer, Quaternion quaternion, float x, float y, float z, float partialTicks) {
        float quadSize = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        for (Vector3f vertex : vertices) {
            vertex.mul(quadSize);
            vertex.transform(quaternion);
            vertex.add(x, y, z);
        }

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .uv(u1, v1).color(rCol, gCol, bCol, alpha)
                .uv2(light).endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .uv(u1, v0).color(rCol, gCol, bCol, alpha)
                .uv2(light).endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .uv(u0, v0).color(rCol, gCol, bCol, alpha)
                .uv2(light).endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .uv(u0, v1).color(rCol, gCol, bCol, alpha)
                .uv2(light).endVertex();
    }


    public int getLightColor(float f) {
        float g = ((float)this.age + f) / (float)this.lifetime;
        g = Mth.clamp(g, 0.0F, 1.0F);
        int i = 240;
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(g * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            EndPortalParticle eyePlaceParticle = new EndPortalParticle(clientLevel, d, e, f);
            eyePlaceParticle.pickSprite(this.sprite);
            eyePlaceParticle.setAlpha(0.3F);
            return eyePlaceParticle;
        }
    }
}
