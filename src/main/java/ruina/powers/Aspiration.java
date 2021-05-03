package ruina.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.applyToTargetNextTurn;

public class Aspiration extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Aspiration.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Aspiration(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    public void onVictory() { if (owner.currentHealth > 0) owner.heal(this.amount); }
    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }
}