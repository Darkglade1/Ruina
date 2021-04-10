package ruina.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DarkSmokePuffEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokingEmberEffect;

public class ExplosionEffect extends AbstractGameEffect {
    private float x;
    private float y;

    public ExplosionEffect(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        AbstractDungeon.effectsQueue.add(new DarkSmokePuffEffect(this.x, this.y));

        for(int i = 0; i < 75; ++i) {
            AbstractDungeon.effectsQueue.add(new SmokingEmberEffect(this.x + MathUtils.random(-250.0F, 250.0F) * Settings.scale, this.y + MathUtils.random(-250.0F, 250.0F) * Settings.scale));
        }
        this.isDone = true;
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
