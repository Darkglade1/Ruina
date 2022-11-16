package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.monster;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.QueenOfHatredMonster;

public class QueenOfHatredStunToNormalStateAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredStunToNormalStateAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "7_Delay_to_Default", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){
                if(effect instanceof QueenOfHatredMonster){
                    ((QueenOfHatredMonster) effect).resetAnimationData();
                }
                isDone = true;
            }
        }
    }
}
