package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeVfxPath;
import static ruina.util.Wiz.adp;

public class LaetitiaGiftEffect extends AbstractGameEffect {

    public static final String GIFT_1 = RuinaMod.makeVfxPath("Laetitia_Gift1.png");
    public static final String GIFT_2 = RuinaMod.makeVfxPath("Laetitia_Gift2.png");
    public static final String GIFT_EXPLOSION = RuinaMod.makeVfxPath("Laetitia_Gift3.png");

    private final Texture Gift = TexLoader.getTexture(GIFT_1);
    private final Texture Gift2 = TexLoader.getTexture(GIFT_2);
    private final Texture Gift3 = TexLoader.getTexture(GIFT_EXPLOSION);

    private float x, y, vX, vY;
    private boolean rightSideUp;
    private boolean detonated;
    private float animTimer = 0.1f;

    public LaetitiaGiftEffect(AbstractCreature source) {
        color = Color.WHITE.cpy();
        color.a = 0.0F;
        scale = 0.5F;
        x = source.hb.x + source.hb.width;
        y = source.hb.y + source.hb.height;
        vX = (Settings.WIDTH / 2.0F - x) / 3.0F;
        vY = (Settings.HEIGHT / 1.4F - y) / 3.0F;
        duration = 3.4F;
        startingDuration = duration;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (rightSideUp) {
            rotation += 120.0F * Gdx.graphics.getDeltaTime();
            if (rotation > 30.0F) {
                rightSideUp = false;
            }
        } else {
            rotation -= 120.0F * Gdx.graphics.getDeltaTime();
            if (rotation < -30.0F) {
                rightSideUp = true;
            }
        }
        if (duration > 0.4F) {
            color.a = (startingDuration - duration) / 3.0F;
            x += vX * Gdx.graphics.getDeltaTime();
            y += vY * Gdx.graphics.getDeltaTime();
            scale = (startingDuration - duration) / 3.0F + 0.5F;
        } else {
            if(!detonated) {
                scale = 1.5F;
                color.a = 1.0F;
                detonated = true;
            }
        }
        if(detonated){
            for (int i = 0; i < 2; i++) {
                AbstractDungeon.effectsQueue.add(new LaetitiaSplatter());
            }
        }
        if(duration <= 0){
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!detonated) {
            sb.setColor(Color.WHITE);
            sb.draw(Gift, this.x - 256.0F * Settings.scale, this.y - 256.0F * Settings.scale, 256.0F, 256.0F, 512.0F, 512.0F, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation, 0, 0, 512, 512, false, false);
            sb.setColor(color);
            sb.draw(Gift2, this.x - 256.0F * Settings.scale, this.y - 256.0F * Settings.scale, 256.0F, 256.0F, 512.0F, 512.0F, this.scale * Settings.scale, this.scale * Settings.scale, this.rotation, 0, 0, 512, 512, false, false);
        }
        else {
            sb.setColor(Color.WHITE);
            sb.draw(Gift3, this.x - 256.0F * Settings.scale, this.y - 256.0F * Settings.scale, 256.0F, 256.0F, 512.0F, 512.0F, this.scale * Settings.scale, this.scale * Settings.scale, 0.0F, 0, 0, 512, 512, false, false);
        }
    }

    public void dispose() {}
}