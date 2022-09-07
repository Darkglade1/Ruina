package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeVfxPath;

public class LaetitiaSplatter extends AbstractGameEffect {
    private Texture img;

    private float x;

    private float y;

    private float rotation;

    public LaetitiaSplatter() {
        this.img = TexLoader.getTexture(makeVfxPath("Laetitia_Gift_Explosion.png"));
        this.color = Color.WHITE.cpy();
        this.color.a = 0.6F;
        this.x = MathUtils.random(Settings.WIDTH);
        this.y = MathUtils.random(Settings.HEIGHT);
        this.rotation = MathUtils.random(360);
        this.scale = 0.0F;
        this.duration = 0.2F;
        this.startingDuration = 2.0F;
    }

    public LaetitiaSplatter(float duration) {
        this.img = TexLoader.getTexture(makeVfxPath("Laetitia_Gift_Explosion.png"));
        this.color = Color.WHITE.cpy();
        this.color.a = 0.6F;
        this.x = MathUtils.random(Settings.WIDTH);
        this.y = MathUtils.random(Settings.HEIGHT);
        this.rotation = MathUtils.random(360);
        this.scale = 0.0F;
        this.duration = 0.2F;
        this.startingDuration = duration;
    }

    public void update() {
        if (this.duration > 0.0F) {
            this.duration -= Gdx.graphics.getDeltaTime();
            this.scale = (0.2F - this.duration) / 0.2F * Settings.scale;
        } else {
            this.scale = Settings.scale;
            this.startingDuration -= Gdx.graphics.getDeltaTime();
            if (this.startingDuration < 0.5F)
                this.color.a = this.startingDuration * 1.2F;
            if (this.startingDuration < 0.0F)
                this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        float width = this.img.getWidth();
        float height = this.img.getHeight();
        sb.draw(this.img, this.x - width / 2.0F, this.y - height / 2.0F, width / 2.0F, height / 2.0F, width, height, scale * 1.6F, scale * 1.6F, this.rotation, 0, 0, (int)width, (int)height, false, false);
    }

    public void dispose() {}
}