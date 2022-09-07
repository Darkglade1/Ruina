package ruina.actions;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.Act4Angela;
import ruina.vfx.AspirationHeartEffect;
import ruina.vfx.IceQueenIceEffect;

public class AspirationEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AspirationHeartEffect effect = new AspirationHeartEffect();

    public AspirationEffectAction() {
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
