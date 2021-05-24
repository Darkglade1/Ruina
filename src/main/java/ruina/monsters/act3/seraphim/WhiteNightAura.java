package ruina.monsters.act3.seraphim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class WhiteNightAura extends AbstractGameEffect {
    private float x;
    private float y;
    private float lineTimer;
    private float rotation2;
    private float rotation3;

    public WhiteNightAura(float hbx, float hby) {
        this.color = Color.WHITE.cpy();
        this.color.a = 0.8F;
        this.renderBehind = true;
        this.rotation2 = 0.0F;
        this.rotation3 = 90.0F;
        this.scale = 0.6666667F;
        this.x = hbx;
        this.y = hby;
        this.lineTimer = 0.6666667F;
    }

    public void update() {
        this.lineTimer -= Gdx.graphics.getDeltaTime();
        this.rotation2 -= Gdx.graphics.getDeltaTime() * 60.0F;
        this.rotation3 += Gdx.graphics.getDeltaTime() * 60.0F;
        if (this.lineTimer <= 0.0F) {
            this.lineTimer += 6.0F;
            for (int i = 0; i < 7; i++) { AbstractDungeon.effectsQueue.add(new WhiteNightAuraPin(this.x - 10.0F * Settings.scale, this.y, i)); }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(Seraphim.Ring, this.x - Seraphim.Ring.packedWidth / 2.0F, this.y - Seraphim.Ring.packedHeight / 2.0F, Seraphim.Ring.packedWidth / 2.0F, Seraphim.Ring.packedHeight / 2.0F, Seraphim.Ring.packedWidth, Seraphim.Ring.packedHeight, this.scale / 1.1F * Settings.scale, this.scale / 1.1F * Settings.scale, this.rotation);
        sb.draw(Seraphim.LightAura, this.x - Seraphim.LightAura.packedWidth / 2.0F, this.y - Seraphim.LightAura.packedHeight / 2.0F, Seraphim.LightAura.packedWidth / 2.0F, Seraphim.LightAura.packedHeight / 2.0F, Seraphim.LightAura.packedWidth, Seraphim.LightAura.packedHeight, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation2);
        sb.draw(Seraphim.LightAura, this.x - Seraphim.LightAura.packedWidth / 2.0F, this.y - Seraphim.LightAura.packedHeight / 2.0F, Seraphim.LightAura.packedWidth / 2.0F, Seraphim.LightAura.packedHeight / 2.0F, Seraphim.LightAura.packedWidth, Seraphim.LightAura.packedHeight, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation3);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() { }
}