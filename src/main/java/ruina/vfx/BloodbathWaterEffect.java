package ruina.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.RuinaMod;
import ruina.util.TexLoader;

public class BloodbathWaterEffect extends AbstractGameEffect {

    public static final String BACKGROUND = RuinaMod.makeVfxPath("BloodbathWater.png");
    private final TextureRegion BACKGROUND_TEXTURE = new TextureRegion(TexLoader.getTexture(BACKGROUND));

    protected float waterY = 0f;
    protected float pullY = 0f;

    private boolean almostDone = false;

    public BloodbathWaterEffect(){
        duration = 2f;
        color= Color.WHITE.cpy();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.draw(BACKGROUND_TEXTURE, 0.0F, waterY, Settings.WIDTH, Settings.HEIGHT);
    }

    public void update() {
        color.a -= Gdx.graphics.getDeltaTime() / 5;
        if (waterY != 0.0F || pullY != 0.0F) {
            if(pullY != 0.0F){
                pullY = MathUtils.lerp(pullY, Settings.HEIGHT / 2.0F - 540.0F * Settings.scale, Gdx.graphics.getDeltaTime() * 5.0F);
                if (Math.abs(pullY - 0.0F) < 0.5F) {
                    pullY = 0.0F;
                }
            }
            if(waterY != 0.0F){
                waterY = MathUtils.lerp(waterY, Settings.HEIGHT / 2.0F - 540.0F * Settings.scale, Gdx.graphics.getDeltaTime() * 5.0F);
                if (Math.abs(waterY - 0.0F) < 0.5F) { waterY = 0.0F; }
            }
        }
        duration -= Gdx.graphics.getDeltaTime();
        if(duration <= 0F && !almostDone){
            duration += 0.3f;
            almostDone = true;
            CardCrawlGame.fadeIn(0.15f);
        }
        else if (duration <= 0f) { isDone = true; }
    }

    @Override
    public void dispose() {

    }
}
