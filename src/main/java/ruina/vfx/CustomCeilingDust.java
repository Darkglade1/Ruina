package ruina.vfx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.FallingDustEffect;
import com.megacrit.cardcrawl.vfx.scene.CeilingDustCloudEffect;

public class CustomCeilingDust extends AbstractGameEffect {
    private int count = 20;
    private float x;

    public CustomCeilingDust() {
        setPosition();
    }

    private void setPosition() {
        this.x = MathUtils.random(0.0F, 1870.0F) * Settings.scale;
    }

    public void update() {
        if (this.count != 0) {
            int num = MathUtils.random(0, 8);
            this.count -= num;
            for (int i = 0; i < num; i++) {
                AbstractDungeon.effectsQueue.add(new FallingDustEffect(this.x, AbstractDungeon.floorY + 740.0F * Settings.scale));
                if (MathUtils.randomBoolean(0.8F))
                    AbstractDungeon.effectsQueue.add(new CeilingDustCloudEffect(this.x, AbstractDungeon.floorY + 740.0F * Settings.scale));
            }
            if (this.count <= 0)
                this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {}

    public void dispose() {}
}
