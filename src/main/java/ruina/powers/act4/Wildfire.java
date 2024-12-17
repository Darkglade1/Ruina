package ruina.powers.act4;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.monsterList;

public class Wildfire extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Wildfire.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Wildfire(AbstractCreature owner, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void atEndOfRound() {
        for (AbstractMonster mo : monsterList()) {
            if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                applyToTarget(mo, owner, new VulnerablePower(mo, amount, true));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
    }
}
