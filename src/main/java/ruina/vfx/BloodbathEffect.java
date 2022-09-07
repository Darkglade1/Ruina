package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeVfxPath;

public class BloodbathEffect extends AbstractGameEffect {
    private final Texture[] img = new Texture[5];

    private float animTimer;

    private float lastTimer;

    private final Color[] color2 = new Color[5];

    private int stage;
    private boolean playedBloodSound = false;

    public BloodbathEffect() {
        this.img[0] = TexLoader.getTexture(makeVfxPath("Lobotomy.png"));

        for (int i = 0; i < 5; i++) {
            this.color2[i] = Color.WHITE.cpy();
            (this.color2[i]).a = 0.0F;
        }
        this.animTimer = 0.1F;
        this.duration = 2.0F;
        this.startingDuration = 1.0F;
        this.lastTimer = 0.5F;
        this.stage = 1;
    }

    public void update() {
        this.startingDuration -= Gdx.graphics.getDeltaTime();
        if (this.duration > 0.0F) {
            this.duration -= Gdx.graphics.getDeltaTime();
            if (this.stage > 1) {
                if (this.animTimer == 0.1F) {
                    if (!playedBloodSound) {
                        AbstractRuinaMonster.playSound("NosSpecialEye");
                        playedBloodSound = true;
                    }
                    for (int i = 0; i < 10; i++) {
                        AbstractDungeon.effectsQueue.add(new BloodSplatter());
                    }
                }
                this.animTimer -= Gdx.graphics.getDeltaTime();
                if (this.animTimer < 0.0F) {
                    this.animTimer += 0.1F;
                    AbstractDungeon.effectsQueue.add(new BloodSplatter());
                }
            }
            if (this.stage <= 5) {
                (this.color2[this.stage - 1]).a += Gdx.graphics.getDeltaTime() * 2.5F;
                if ((this.color2[this.stage - 1]).a > 1.0F) {
                    (this.color2[this.stage - 1]).a = 1.0F;
                    this.stage++;
                }
            }
        } else {
            this.lastTimer -= Gdx.graphics.getDeltaTime();
            if (this.lastTimer > 0.0F) {
                for (int i = 0; i < 5; i++)
                    (this.color2[i]).a = this.lastTimer * 2.0F;
            } else {
                this.isDone = true;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(this.img[0], 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    }

    public void dispose() {
    }
}