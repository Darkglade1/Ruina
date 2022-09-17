package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class QueenOfHatredPassiveAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredPassiveAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            switch (MathUtils.random(3)) {
                case 0:
                    effect.state.setAnimation(0, "0_Default_2", false);
                    effect.state.setTimeScale(1f);
                    break;
                case 1:
                    effect.state.setAnimation(0, "0_Default_3", false);
                    effect.state.setTimeScale(1f);
                    break;
                case 2: {
                    effect.state.setAnimation(0, "0_Default_4_special", false);
                    effect.state.setTimeScale(1f);
                    break;
                }
            }
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){
                System.out.println("h");
                effect.state.addAnimation(0, "0_Default_escape_too", true, 0f);
                effect.state.setTimeScale(1f);
                isDone = true;
            }
        }
    }
}
