package ruina.monsters.angela.Abnormalities.WhiteNight.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.angela.Abnormalities.WhiteNight.WhiteNight;

public class WhiteNightAura extends AbstractGameEffect {
    private float x;
    private float y;
    private float lineTimer;
    private float rotation2;
    private float rotation3;

    public WhiteNightAura(Hitbox hb) {
        this.color = Color.WHITE.cpy();
        this.color.a = 0.8F;
        this.renderBehind = true;
        this.rotation2 = 0.0F;
        this.rotation3 = 90.0F;
        this.scale = 0.6666667F;
        this.x = hb.cX + 10.0F;
        this.y = hb.cY + 35.0F;
        this.lineTimer = 0.6666667F;
    }

    public void update() {
        this.lineTimer -= Gdx.graphics.getDeltaTime();
        this.rotation2 -= Gdx.graphics.getDeltaTime() * 60.0F;
        this.rotation3 += Gdx.graphics.getDeltaTime() * 60.0F;
        if (this.lineTimer <= 0.0F) {
            this.lineTimer += 6.0F;
            for (int i = 0; i < 7; i++) { AbstractDungeon.effectsQueue.add(new WhiteNightAuraPin(this.x - 10.0F, this.y, i)); }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(WhiteNight.Ring, this.x - WhiteNight.Ring.packedWidth / 2.0F, this.y - WhiteNight.Ring.packedHeight / 2.0F, WhiteNight.Ring.packedWidth / 2.0F, WhiteNight.Ring.packedHeight / 2.0F, WhiteNight.Ring.packedWidth, WhiteNight.Ring.packedHeight, this.scale / 1.1F, this.scale / 1.1F, this.rotation);
        sb.draw(WhiteNight.LightAura, this.x - WhiteNight.LightAura.packedWidth / 2.0F, this.y - WhiteNight.LightAura.packedHeight / 2.0F, WhiteNight.LightAura.packedWidth / 2.0F, WhiteNight.LightAura.packedHeight / 2.0F, WhiteNight.LightAura.packedWidth, WhiteNight.LightAura.packedHeight, this.scale, this.scale, this.rotation2);
        sb.draw(WhiteNight.LightAura, this.x - WhiteNight.LightAura.packedWidth / 2.0F, this.y - WhiteNight.LightAura.packedHeight / 2.0F, WhiteNight.LightAura.packedWidth / 2.0F, WhiteNight.LightAura.packedHeight / 2.0F, WhiteNight.LightAura.packedWidth, WhiteNight.LightAura.packedHeight, this.scale, this.scale, this.rotation3);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() { }
}