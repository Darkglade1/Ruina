
package ruina.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;


public class FlexibleFlashAtkImgEffect extends AbstractGameEffect {
    public Texture img;
    private float x;
    private float y;

    public FlexibleFlashAtkImgEffect(float x, float y, Texture img) {
        this.duration = 0.6F;
        this.startingDuration = 0.6F;
        this.img = img;
        if (this.img != null) {
            this.x = x - this.img.getWidth() / 2.0F;
            y -= this.img.getHeight() / 2.0F;
        }

        this.color = Color.WHITE.cpy();
        this.scale = Settings.scale;

        this.y = y;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.img != null) {
            sb.setColor(this.color);
            sb.draw(this.img, this.x, this.y);
        }
    }

    public void dispose() {
    }
}