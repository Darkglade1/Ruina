package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class QueenOfHatredDefaultToCastingAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredDefaultToCastingAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "1_Default_to_Casting", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){
                effect.state.addAnimation(0, "2_Casting_loop", true, 0f);
                effect.state.setTimeScale(1f);
                isDone = true;
            }
        }
    }
}
