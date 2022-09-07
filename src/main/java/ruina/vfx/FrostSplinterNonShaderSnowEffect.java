package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FrostSplinterNonShaderSnowEffect extends AbstractGameEffect {
    private float x;

    private float y;

    private float vY;

    private float vX;

    private float scaleY;

    private int frame = 0;

    private float animTimer = 0.05F;

    private static final int W = 32;

    public FrostSplinterNonShaderSnowEffect() {
        this.x = MathUtils.random(100.0F * Settings.scale, 2420.0F * Settings.scale);
        this.y = Settings.HEIGHT + MathUtils.random(20.0F, 300.0F) * Settings.scale;
        this.frame = MathUtils.random(3);
        this.rotation = MathUtils.random(30.0F, 40.0F);
        this.scale = MathUtils.random(0.05F, 0.25F);
        this.scaleY = MathUtils.random(1.2F, 1.25F);
        if (this.scale < 1.5F)
            this.renderBehind = true;
        this.vY = MathUtils.random(1300.0F, 1600.0F) * this.scale * Settings.scale;
        this.vX = MathUtils.random(-2000.0F, -800.0F) * this.scale * Settings.scale;
        this.scale *= Settings.scale;
        if (MathUtils.randomBoolean())
            this.rotation += 180.0F;
        this.renderBehind = MathUtils.randomBoolean();
        this.color = (new Color(CardHelper.getColor(255, 250, 250))).cpy();
        this.duration = 4.0F;
    }

    public void update() {
        this.y -= this.vY * Gdx.graphics.getDeltaTime() * 2.0F;
        this.x += this.vX * Gdx.graphics.getDeltaTime() * 2.0F;
        this.animTimer -= Gdx.graphics.getDeltaTime() / this.scale;
        if (this.animTimer < 0.0F) {
            this.animTimer += 0.05F;
            this.frame++;
            if (this.frame > 3)
                this.frame = 0;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F)
            this.isDone = true;
    }

    public void render(SpriteBatch sb) {
        switch (this.frame) {
            case 0:
                renderImg(sb, ImageMaster.PETAL_VFX[0], false, false);
                break;
            case 1:
                renderImg(sb, ImageMaster.PETAL_VFX[1], false, false);
                break;
            case 2:
                renderImg(sb, ImageMaster.PETAL_VFX[0], true, true);
                break;
            case 3:
                renderImg(sb, ImageMaster.PETAL_VFX[1], true, true);
                break;
        }
    }

    public void dispose() {}

    private void renderImg(SpriteBatch sb, Texture img, boolean flipH, boolean flipV) {
        sb.setColor(this.color);
        sb.draw(img, this.x, this.y, 16.0F, 16.0F, 32.0F, 32.0F, this.scale, this.scale * this.scaleY, this.rotation, 0, 0, 32, 32, flipH, flipV);
        this.color.a = MathUtils.random(0.6F, 0.9F);
    }
}