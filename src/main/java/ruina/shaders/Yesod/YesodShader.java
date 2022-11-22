package ruina.shaders.Yesod;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.patches.PostProcessorPatch;

public class YesodShader extends AbstractGameEffect {

    private ScreenPostProcessor postProcessor = new YesodPostProcessor();
    private boolean doneFadeIN = false;

    public YesodShader(){
        duration = 5f;
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
        if(duration <= -2.5f){
            PostProcessorPatch.removePostProcessor(postProcessor);
            isDone = true;
        }
    }

    @Override
    public void dispose() {

    }
}
