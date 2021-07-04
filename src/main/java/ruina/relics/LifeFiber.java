package ruina.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnPlayerDeathRelic;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import static ruina.RuinaMod.makeID;

public class LifeFiber extends AbstractEasyRelic implements OnPlayerDeathRelic {
    public static final String ID = makeID(LifeFiber.class.getSimpleName());

    private static final int HEAL = 4;
    private static final int SCALING_HEAL = 4;

    public LifeFiber() {
        super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
        this.counter = HEAL;
        fixDescription();
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + counter + DESCRIPTIONS[1] + SCALING_HEAL + DESCRIPTIONS[2];
    }

    @Override
    public void onVictory() {
        if (counter != -2) {
            counter += SCALING_HEAL;
            fixDescription();
        }
    }

    public void setCounter(int setCounter) {
        super.setCounter(setCounter);
        fixDescription();
        if (setCounter == -2) {
            this.usedUp();
            this.counter = -2;
        }
    }

    @Override
    public boolean onPlayerDeath(AbstractPlayer abstractPlayer, DamageInfo damageInfo) {
        if (counter == -2) {
            return true;
        } else {
            flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            AbstractDungeon.player.heal(counter, true);
            this.setCounter(-2);
            return false;
        }
    }

    private void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }
}
