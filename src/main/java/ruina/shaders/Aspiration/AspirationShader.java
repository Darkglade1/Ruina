package ruina.shaders.Aspiration;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.patches.PostProcessorPatch;

public class AspirationShader extends AbstractGameEffect {

    private ScreenPostProcessor postProcessor = new AspirationPostProcessor();
    private boolean doneFadeIN = false;

    public AspirationShader(){
        duration = 2.5f;
        startingDuration = duration;
    }
    @Override
    public void render(SpriteBatch spriteBatch) {}

    @Override
    public void update() {
        if(duration == startingDuration){
            PostProcessorPatch.addPostProcessor(postProcessor);
        }
        duration -= Gdx.graphics.getDeltaTime();
        if(duration <= 0){
            PostProcessorPatch.removePostProcessor(postProcessor);
            isDone = true;
        }
    }

    @Override
    public void dispose() {

    }
}
