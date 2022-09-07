package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.shaders.Bloodbath.BloodbathShader;
import ruina.vfx.BloodbathEffect;
import ruina.vfx.BloodbathWaterEffect;

public class BloodbathEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private BloodbathEffect effect = new BloodbathEffect();

    public BloodbathEffectAction() {
        duration = startingDuration;
    }

    @Override
    public void update() {
        if(firstFrame){
            AbstractDungeon.effectsQueue.add(effect);
            firstFrame = false;
        }
        isDone = effect.isDone;
    }
}
