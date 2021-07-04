package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class OrchestraMusicEffect extends AbstractGameEffect {
    private final float x;
    private final float y;
    private final TextureRegion musicTexture;
    private final boolean clockwise;
    private final float targetScale;
    private float graphicsAnimation;
    private boolean ending = false;
    private final float endingScale = 10.0f;

    public OrchestraMusicEffect(float hbx, float hby, Texture musicTexture, boolean clockwise, float targetScale) {
        this.color = Color.WHITE.cpy();
        this.color.a = 0.8F;
        this.renderBehind = true;
        this.scale = 0.0f;
        this.targetScale = targetScale;
        this.x = hbx;
        this.y = hby;
        this.musicTexture = new TextureRegion(musicTexture);
        this.clockwise = clockwise;
    }

    public void update() {
        this.graphicsAnimation += Gdx.graphics.getDeltaTime();
        if (ending) {
            if (scale < endingScale) {
                scale = Interpolation.linear.apply(targetScale, endingScale, graphicsAnimation * 1.5F);
            }
            if (scale > endingScale) {
                scale = endingScale;
                isDone = true;
            }
        } else {
            if (scale < targetScale) {
                scale = Interpolation.linear.apply(0.0f, targetScale, graphicsAnimation * 1.0F);
            }
            if (scale > targetScale) {
                scale = targetScale;
            }
        }

        if (clockwise) {
            this.rotation += Gdx.graphics.getDeltaTime() * 30.0F;
        } else {
            this.rotation -= Gdx.graphics.getDeltaTime() * 30.0F;
        }
    }

    public void end() {
        graphicsAnimation = 0.0f;
        ending = true;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(musicTexture, this.x - (float)musicTexture.getRegionWidth() / 2, this.y - (float)musicTexture.getRegionHeight() / 2, (float)musicTexture.getRegionWidth() / 2, (float)musicTexture.getRegionHeight() / 2, musicTexture.getRegionWidth(), musicTexture.getRegionHeight(), Settings.scale * scale, Settings.scale * scale, rotation);
    }

    public void dispose() { }
}