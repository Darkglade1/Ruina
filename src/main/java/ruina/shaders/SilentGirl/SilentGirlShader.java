package ruina.shaders.SilentGirl;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.patches.PostProcessorPatch;

public class SilentGirlShader extends AbstractGameEffect {

    private ScreenPostProcessor postProcessor = new SilentGirlPostProcessor();
    private ScreenPostProcessor fixedPositionPostProcessor = new SilentGirlPostProcessorFixedPosition();

    private boolean doneFadeIN = false;
    private boolean remove = false;
    private boolean fixedPosition = false;

    public SilentGirlShader(boolean remove, boolean fixedPosition){
        duration = 1.5f;
        startingDuration = duration;
        this.remove = remove;
        this.fixedPosition = fixedPosition;
    }

    public SilentGirlShader(){
        duration = 1.5f;
        startingDuration = duration;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {}

    @Override
    public void update() {
        if(duration == startingDuration){
            if(fixedPosition){ PostProcessorPatch.addPostProcessor(fixedPositionPostProcessor);}
            else { PostProcessorPatch.addPostProcessor(postProcessor); }
        }
        duration -= Gdx.graphics.getDeltaTime();
        if(duration <= 0){
            if(remove){
                if(fixedPosition){ PostProcessorPatch.removePostProcessor(fixedPositionPostProcessor);}
                else { PostProcessorPatch.removePostProcessor(postProcessor); }
            }
            isDone = true;
        }
    }

    @Override
    public void dispose() {

    }
}
