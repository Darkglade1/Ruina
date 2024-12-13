package ruina.powers.act1;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act1.spiderBud.SpiderBud;
import ruina.powers.AbstractUnremovablePower;

public class Hunt extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Hunt.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Hunt(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onSpecificTrigger() {
        if (owner instanceof SpiderBud) {
            if (!((SpiderBud) owner).enraged) {
                flash();
                ((SpiderBud) owner).enraged = true;
            }
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
