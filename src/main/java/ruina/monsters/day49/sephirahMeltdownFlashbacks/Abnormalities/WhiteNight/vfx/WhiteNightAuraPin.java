package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.WhiteNight;

public class WhiteNightAuraPin extends AbstractGameEffect {
    private float x;
    private float y;
    private float scaX;
    private float scaY;

    public WhiteNightAuraPin(float x, float y, int i) {
        this.duration = 2.0F;
        this.startingDuration = this.duration;
        this.color = Color.WHITE.cpy();
        this.color.a = 0.0F;
        this.renderBehind = true;
        this.rotation = (30 * i);
        this.scale = 0.6666667F;
        this.scaX = 1.0F;
        this.scaY = 1.2F;
        this.x = x;
        this.y = y;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > this.startingDuration - 0.33333334F) { this.color.a = (this.startingDuration - this.duration) * 3.0F; }
        else if (this.duration > 0.75F) { this.color.a = 1.0F; }
        else if (this.duration > 0.0F) { this.color.a = this.duration / 0.75F;
        } else { this.isDone = true; }
        this.scaX = 1.4F - 0.2F * this.duration;
        this.scaY = 0.8F + 0.2F * this.duration;
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(WhiteNight.AuraPin, this.x, this.y - WhiteNight.AuraPin.packedHeight / 2.0F, 0.0F, WhiteNight.AuraPin.packedHeight / 2.0F, WhiteNight.AuraPin.packedWidth, WhiteNight.AuraPin.packedHeight, this.scale * this.scaX, this.scale * this.scaY, this.rotation);
        sb.setBlendFunction(770, 771);
    }
    
    public void dispose() { }
}