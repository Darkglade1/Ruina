package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight.AnimationActions.plaguedoctor;

import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import ruina.monsters.day49.sephirahMeltdownFlashbacks.AbnormalityContainer;

public class PlagueDoctorBlessAnimation extends AbstractGameAction {
    private boolean firstFrame = true;
    private boolean firstState = true;
    private boolean secondState = false;
    private boolean thirdState = false;

    private float startingDuration = 2.65f;
    private AbstractCreature effect;

    public PlagueDoctorBlessAnimation(AbstractCreature c) {
        duration = startingDuration;
        effect = c;
    }

    @Override
    public void update() {
        if(firstFrame){
            effect.state.setAnimation(0, "1_default_to_Kiss", false);
            effect.state.setTimeScale(1f);
            firstFrame = false;
        }
        else {
            AnimationState.TrackEntry e = effect.state.getCurrent(0);
            if (e != null && e.isComplete() && firstState){
                effect.state.addAnimation(0, "2_Kiss_loop", true, 0f);
                effect.state.setTimeScale(1f);
                firstState = false;
                secondState = true;
            }
            else if(e != null && e.isComplete() && secondState){
                effect.state.addAnimation(0, "3_Kiss_to_Default", false, 0f);
                effect.state.setTimeScale(1f);
                secondState = false;
                thirdState = true;
            }
            else if(e != null && e.isComplete() && thirdState){
                effect.state.addAnimation(0, "0_Default_", true, 0f);
                effect.state.setTimeScale(1f);
                isDone = true;
            }
        }
    }
}
