package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.shaders.Bloodbath.BloodbathShader;
import ruina.shaders.SilentGirl.SilentGirlShader;
import ruina.vfx.BloodbathWaterEffect;

public class SilentGirlEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private float startingDuration = 0.5f;
    private SilentGirlShader shader;

    public SilentGirlEffectAction() {
        duration = startingDuration;
        shader = new SilentGirlShader();
    }

    public SilentGirlEffectAction(boolean removeShader, boolean fixedPosition) {
        duration = startingDuration;
        shader = new SilentGirlShader(removeShader, fixedPosition);
    }

    public SilentGirlEffectAction(boolean fixedPosition) {
        duration = startingDuration;
        shader = new SilentGirlShader(false, fixedPosition);
    }

    @Override
    public void update() {
        if(firstFrame){
            AbstractDungeon.effectsQueue.add(shader);
            firstFrame = false;
        }
        else { tickDuration(); }
    }
}
