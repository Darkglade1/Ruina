package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.day49.Act4Angela;
import ruina.vfx.IceQueenIceEffect;

public class FrostSplinterIceEffectAction extends AbstractGameAction {
    Act4Angela owner;
    private boolean firstFrame = true;

    private float startingDuration = 2.65f;

    public FrostSplinterIceEffectAction(Act4Angela owner) {
        this.owner = owner;
        duration = startingDuration;
    }

    @Override
    public void update() {
        if(firstFrame){
            owner.runAnim("Special");
            owner.playSound("SnowBlizzard");
            AbstractDungeon.effectsQueue.add(new IceQueenIceEffect());
            firstFrame = false;
        }
        this.tickDuration();
    }
}

