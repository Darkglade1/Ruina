package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.atb;

public class Bliss extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Bliss.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    AbstractCard fragment;

    public Bliss(AbstractCreature owner, AbstractCard fragment) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        this.fragment = fragment;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (fragment != null) {
            description = POWER_DESCRIPTIONS[0] + FontHelper.colorString(fragment.name, "y") + POWER_DESCRIPTIONS[1];
        }
    }

    @Override
    public void onDeath() {
        atb(new MakeTempCardInHandAction(fragment, 1));
    }
}
