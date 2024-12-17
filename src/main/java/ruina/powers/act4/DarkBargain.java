package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;

public class DarkBargain extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(DarkBargain.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private Yesod yesod;

    public DarkBargain(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        if (owner instanceof Yesod) {
            yesod = (Yesod)owner;
        }
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target == adp() && damageAmount <= 0 && info.type == DamageInfo.DamageType.NORMAL) {
            flash();
            if (yesod != null) {
                yesod.currentDamageBonus += yesod.damageGrowth;
            }
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        if (yesod != null) {
            description = POWER_DESCRIPTIONS[0] + yesod.currentDamageBonus + POWER_DESCRIPTIONS[1] + yesod.damageGrowth + POWER_DESCRIPTIONS[2];
        }
    }
}
