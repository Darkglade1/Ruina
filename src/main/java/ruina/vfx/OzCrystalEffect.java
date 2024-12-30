
package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeVfxPath;


public class OzCrystalEffect extends AbstractGameEffect {
    private Texture img = TexLoader.getTexture(makeVfxPath("OzCrystalFall.png"));

    private float x = Settings.WIDTH * 0.75F - this.img.getWidth() / 2.0F;
    private float y = Settings.HEIGHT;

    private boolean shock = true;


    @Override
    public void update() {
        if (this.y > AbstractDungeon.floorY - 100.0F * Settings.scale) {
            this.y -= Gdx.graphics.getDeltaTime() * 2500.0F * Settings.scale;
        } else {
            if (this.shock) {
                this.shock = false;
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
                this.img = TexLoader.getTexture(makeVfxPath("OzCrystalHit.png"));
                AbstractRuinaMonster.playSound("OzStrongAtkDown");
            }
            this.y = AbstractDungeon.floorY - 100.0F * Settings.scale;
            this.duration -= Gdx.graphics.getDeltaTime();
            if (this.duration < 0.0F) {
                this.isDone = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(this.img, this.x, this.y, 256.0F, 0.0F, 512.0F, 512.0F, 2.0F * Settings.scale, 2.0F * Settings.scale, 0.0F, 0, 0, 512, 512, false, false);
    }

    public void dispose() {
    }
}