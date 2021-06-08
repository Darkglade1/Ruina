package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import static ruina.RuinaMod.makeVfxPath;

public class BurrowingHeavenEffect extends AbstractGameEffect {
    private Texture img;

    private float animTimer;

    private float lastTimer;

    private Color[] color2 = new Color[5];

    private int stage;

    public BurrowingHeavenEffect() {
        img = ImageMaster.loadImage(makeVfxPath("BurrowingHeavenEffect.png"));
        this.color = Color.RED.cpy();
        this.color.a = 0.0F;
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
        if (this.startingDuration > 0.5F) {
            this.color.a = 1.0F - this.startingDuration;
        } else if (this.startingDuration > 0.0F) {
            this.color.a = this.startingDuration;
        } else if (this.duration > 0.0F) {
            this.duration -= Gdx.graphics.getDeltaTime();
            if (this.stage > 1) {
                if (this.animTimer == 0.1F)
                    for (int i = 0; i < 10; i++)
                        //AbstractDungeon.effectsQueue.add(new BloodSplatter());
                this.animTimer -= Gdx.graphics.getDeltaTime();
                if (this.animTimer < 0.0F) {
                    this.animTimer += 0.1F;
                    //AbstractDungeon.effectsQueue.add(new BloodSplatter());
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
        sb.setColor(this.color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        for (int i = 0; i < 5; i++) {
            sb.setColor(this.color2[i]);
            sb.draw(this.img, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        }
    }

    public void dispose() {}
}