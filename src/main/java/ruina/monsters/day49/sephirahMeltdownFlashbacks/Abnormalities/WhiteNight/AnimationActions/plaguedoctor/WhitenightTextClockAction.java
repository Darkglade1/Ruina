package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.vfx.WhitenightClockEffect;

public class WhitenightTextClockAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private WhitenightClockEffect effect;

    public WhitenightTextClockAction(int amount) {
        duration = startingDuration;
        effect = new WhitenightClockEffect(amount);
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
