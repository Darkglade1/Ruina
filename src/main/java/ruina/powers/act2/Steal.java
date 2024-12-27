package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.actions.JackStealAction;
import ruina.monsters.act2.ozma.Jack;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class Steal extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Steal.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Steal(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (owner instanceof Jack && target == adp()) {
            atb(new JackStealAction((Jack) owner));
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (owner instanceof Jack) {
            if (damageAmount > 0 && ((Jack) owner).stolenCards.size() > 0) {
                flash();
                atb(new MakeTempCardInHandAction(((Jack) owner).stolenCards.remove(0)));
            }
        }
        return damageAmount;
    }
    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
