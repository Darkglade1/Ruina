package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.shaders.Bloodbath.BloodbathShader;
import ruina.vfx.AlriuneCanopyEffect;
import ruina.vfx.BloodbathWaterEffect;

public class AlriuneCanopyEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private AlriuneCanopyEffect effect = new AlriuneCanopyEffect();

    @Override
    public void update() {
        if(firstFrame){
            AbstractDungeon.effectsQueue.add(effect);
            firstFrame = false;
        }
        isDone = effect.isDone;
    }
}
