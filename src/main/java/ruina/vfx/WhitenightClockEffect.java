package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

public class WhitenightClockEffect extends AbstractGameEffect {
    public static final String CLOCK = RuinaMod.makeVfxPath("WhitenightClock.png");
    public static final String HAND = RuinaMod.makeVfxPath("WhitenightHourHand.png");

    private final Texture CLOCK_TEXTURE = TexLoader.getTexture(CLOCK);
    private final Texture HAND_TEXTURE = TexLoader.getTexture(HAND);
    private int amount;
    private boolean notplayedSound = true;

    public WhitenightClockEffect(int blessingAmount) {
        duration = 3F;
        amount = blessingAmount;
        color = Color.WHITE.cpy();
        color.a = 0f;
        rotation = (30f * amount) - 30f;
        startingDuration = duration;
    }

    public void update() {
        if (duration == startingDuration) {
            AbstractRuinaMonster.playSound("WhiteNightBell");
        }
        duration -= Gdx.graphics.getDeltaTime();
        if (duration > 2.5F) { color.a = (3.0F - duration) * 2.0F;
        } else { color.a = 1.0F;}
        if (duration > 1.5F && duration < 2.0F) {
            if (notplayedSound) {
                notplayedSound = false;
                AbstractRuinaMonster.playSound("WhiteNightClock");
            }
            if (rotation < 30.0F * amount) { rotation += (2.0F - duration) * 60.0F;
            } else { rotation = 30.0F * amount; }
        } else if (duration < 1.5F) { rotation = 30.0F * amount; }
        if (duration < 1.0F) { color.a = duration;}
        if (color.a < 0.0F) { color.a = 0.0F; }
        if (duration <= 0.0F) { isDone = true;}
    }
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(CLOCK_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.draw(HAND_TEXTURE, (960 * Settings.WIDTH) / 1920.0F - 960.0F, (540 * Settings.WIDTH) / 1920.0F - 540.0F, 960.0F, 540.0F, 1920.0F, 1080.0F, Settings.WIDTH / 1920.0F, Settings.HEIGHT / 1080.0F, -rotation, 0, 0, 1920, 1080, false, false);

    }

    public void dispose() {}
}

