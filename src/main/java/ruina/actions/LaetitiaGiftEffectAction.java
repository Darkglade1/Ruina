package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.vfx.AspirationHeartEffect;
import ruina.vfx.LaetitiaGiftEffect;

import static ruina.util.Wiz.adp;

public class LaetitiaGiftEffectAction extends AbstractGameAction {
    private boolean firstFrame = true;

    private LaetitiaGiftEffect effect;

    public LaetitiaGiftEffectAction(AbstractCreature s) {
        effect = new LaetitiaGiftEffect(s);
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
