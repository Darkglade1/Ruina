package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class OrchestraCurtainEffect extends AbstractGameEffect {
    public static final String CURTAIN_STRING = RuinaMod.makeMonsterPath("Orchestra/Curtain.png");
    public static final String CURTAIN_LEFT = RuinaMod.makeMonsterPath("Orchestra/CurtainUpperLeft.png");
    public static final String CURTAIN_RIGHT = RuinaMod.makeMonsterPath("Orchestra/CurtainUpperRight.png");

    private final Texture ImgLeft = TexLoader.getTexture(CURTAIN_LEFT);
    private final Texture ImgRight = TexLoader.getTexture(CURTAIN_RIGHT);
    private final Texture ImgMid = TexLoader.getTexture(CURTAIN_STRING);

    private float outCurtainX = -Settings.WIDTH / 2.0F;
    private float outCurtainX2 = -Settings.WIDTH / 2.0F * 0.7F;

    public OrchestraCurtainEffect(float duration) {
        this.duration = duration;
    }

    public void update() {
        if (this.duration > 3.0F) {
            this.outCurtainX += Settings.WIDTH * 0.25F * Gdx.graphics.getDeltaTime();
            this.outCurtainX2 += Settings.WIDTH * 0.25F * 0.7F * Gdx.graphics.getDeltaTime();
        } else if (this.duration > 0.0F && this.duration < 2.0f) {
            this.outCurtainX -= Settings.WIDTH * 0.25F * Gdx.graphics.getDeltaTime();
            this.outCurtainX2 -= Settings.WIDTH * 0.25F * 0.7F * Gdx.graphics.getDeltaTime();
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
    }


    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(this.ImgMid, this.outCurtainX, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.draw(this.ImgMid, -this.outCurtainX, 0.0F, Settings.WIDTH, Settings.HEIGHT, 0, 0, 512, 288, true, false);

        sb.draw(this.ImgLeft, this.outCurtainX2, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.draw(this.ImgRight, -this.outCurtainX2, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    }

    public void dispose() {
    }
}


