package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.QueenOfHatred.AnimationActions.normal;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class QueenOfHatredDefaultToPanicAnimationAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public QueenOfHatredDefaultToPanicAnimationAction(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "1_Default_to_Panic_Default", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete()){
                System.out.println("h");
                effect.state.addAnimation(0, "2_Panic_Default", true, 0f);
                effect.state.setTimeScale(1f);
                isDone = true;
            }
        }
    }
}
