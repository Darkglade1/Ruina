package ruina.powers.act1;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.applyToTarget;

public class Song extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Song.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int SELF_DEBUFF;

    public Song(AbstractCreature owner, int amount, int amount2) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.SELF_DEBUFF = amount2;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        flash();
        applyToTarget(owner, owner, new StrengthPower(owner, amount));
        applyToTarget(owner, owner, new VulnerablePower(owner, SELF_DEBUFF, false));
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + SELF_DEBUFF + POWER_DESCRIPTIONS[2];
    }
}
