package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.shaders.Bloodbath.BloodbathShader;
import ruina.vfx.AspirationHeartEffect;
import ruina.vfx.BloodbathWaterEffect;

public class BloodbathWaterEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private BloodbathWaterEffect effect = new BloodbathWaterEffect();
    private BloodbathShader shader = new BloodbathShader();

    public BloodbathWaterEffectAction() {
        duration = startingDuration;
    }

    @Override
    public void update() {
        if(firstFrame){
            AbstractDungeon.effectsQueue.add(effect);
            AbstractDungeon.effectsQueue.add(shader);
            firstFrame = false;
        }
        isDone = effect.isDone = shader.isDone;
    }
}
