package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

 public class QueenOfHatredLaserEffect extends AbstractGameEffect {
    private float sX;
    private float sY;
    private static TextureAtlas.AtlasRegion img;
    public QueenOfHatredLaserEffect(Skeleton skeleton) {
        Bone bone = skeleton.findBone("Magic_Razor");
        if (img == null) {
            img = ImageMaster.vfxAtlas.findRegion("combat/laserThin");
        }
        color = Color.WHITE.cpy();
        color.a = 0.0F;
        sX = skeleton.getX() + bone.getWorldX() + 50.0F;
        sY = skeleton.getY() + bone.getWorldY() - 200.0F;
        rotation = 180.0F;
        duration = 3f;
    }
    
    public void update() {
        if (!(duration <= 0f)) {
            if (color.a < 1.0F) {
                color.a += Gdx.graphics.getDeltaTime() * 4.0F;
            } else { color.a = 1.0F;}
        }
        else if (color.a > 0.0F) { color.a -= Gdx.graphics.getDeltaTime() * 4.0F;
        } else { color.a = 0.0F;}
        duration -= Gdx.graphics.getDeltaTime();
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(color);
        sb.draw(img, sX, sY - 20.0F * Settings.scale, 0.0F, img.packedHeight / 2.0F, Settings.WIDTH * 1.5F, 50.0F, scale + MathUtils.random(-0.01F, 0.01F), scale * 8.0F, rotation);
        sb.setColor(new Color(1.0F, 0.4F, 0.7F, color.a));
        sb.draw(img, sX, sY, 0.0F, img.packedHeight / 2.0F, Settings.WIDTH * 1.5F, MathUtils.random(50.0F, 60.0F), scale + MathUtils.random(-0.02F, 0.02F), scale * 8.0F, rotation);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {}
}