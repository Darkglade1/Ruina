package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class QueenOfHatredCastingToAttackAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredCastingToAttackAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "3_Casting_to_Casting_attack", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){
                effect.state.addAnimation(0, "4_Casting_attack_loop", true, 0f);
                effect.state.setTimeScale(1f);
                isDone = true;
            }
        }
    }
}
