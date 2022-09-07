package ruina.shaders.Bloodbath;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.patches.PostProcessorPatch;

public class BloodbathShader extends AbstractGameEffect {

    private ScreenPostProcessor postProcessor = new BloodbathPostProcessor();
    private boolean doneFadeIN = false;

    public BloodbathShader(){
        duration = 2f;
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
