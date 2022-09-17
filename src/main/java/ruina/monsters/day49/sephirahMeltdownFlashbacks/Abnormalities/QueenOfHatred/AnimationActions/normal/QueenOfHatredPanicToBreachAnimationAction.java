package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.AbnormalityContainer;

public class QueenOfHatredPanicToBreachAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredPanicToBreachAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "4_transform", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){ isDone = true; }
        }
    }
}
