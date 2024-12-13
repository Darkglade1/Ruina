package ruina.powers.act2;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.actions.ExhaustTopCardAction;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Search extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Search.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Search(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            atb(new ExhaustTopCardAction(owner));
        }
    }
    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
