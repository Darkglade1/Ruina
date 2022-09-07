package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.shaders.Aspiration.AspirationShader;
import ruina.util.TexLoader;

public class AspirationHeartEffect extends AbstractGameEffect {

    public static final String BACKGROUND = RuinaMod.makeVfxPath("Aspiration_Heart_BG.png");
    public static final String HEART = RuinaMod.makeVfxPath("Aspiration_Heart.png");

    private final TextureRegion BACKGROUND_TEXTURE = new TextureRegion(TexLoader.getTexture(BACKGROUND));
    private final TextureRegion HEART_TEXTURE = new TextureRegion(TexLoader.getTexture(HEART));

    private Color RED = Color.WHITE.cpy();
    private Color BLACK = Color.BLACK.cpy();
    private Color WHITE = Color.WHITE.cpy();
    private boolean usedShader = false;
    private AspirationShader shader = new AspirationShader();
    private float trueduration = 0f;
    private boolean renderBG = false;

    public AspirationHeartEffect() {
        color = WHITE.cpy();
        color.a = 0f;
        scale = 1f;
        duration = 2f;
    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setColor(Color.BLACK.cpy());
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.setColor(color);
        sb.draw(BACKGROUND_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.setColor(Color.WHITE.cpy());
        if(renderBG){
            sb.draw(BACKGROUND_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        }
        sb.draw(HEART_TEXTURE, Settings.WIDTH / 2.0F - 128.0F, Settings.HEIGHT / 2.0F - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, scale * Settings.scale, scale * Settings.scale, 0.0F);
        sb.setBlendFunction(770, 1);
        sb.setColor(WHITE);
        sb.draw(BACKGROUND_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.draw(HEART_TEXTURE, Settings.WIDTH / 2.0F - 128.0F, Settings.HEIGHT / 2.0F - 128.0F, 128.0F, 128.0F, 256.0F, 256.0F, scale * Settings.scale, scale * Settings.scale, 0.0F);
        sb.setBlendFunction(770, 771);
        sb.setColor(BLACK);
        sb.draw(BACKGROUND_TEXTURE, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);

    }

    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        trueduration += Gdx.graphics.getDeltaTime();
        if (!usedShader && trueduration >= 0.5) {
            usedShader = true;
            AbstractDungeon.effectsQueue.add(shader);
        }
        if (trueduration < 0.5) {
            color.a = trueduration * 2.0F;
        } else {
            color.a = 1.0F;
            if (trueduration < 1.5f) {
                WHITE.a = trueduration - 0.5F;
                BLACK.a = WHITE.a;
            } else {
                renderBG = true;
                color.a = 0.0F;
                WHITE.a = 0.0F;
                if (trueduration <= 2.5) {
                    BLACK.a = 2.5F - trueduration;
                    RED.a = (2.5F - trueduration) / 1.5F;
                } else {
                    if(duration <= 0 && shader.isDone){
                        System.out.println("done");
                        isDone = true; }
                }
            }
        }
    }

    @Override
    public void dispose() {

    }
}
