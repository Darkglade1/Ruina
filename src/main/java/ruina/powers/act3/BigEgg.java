package ruina.powers.act3;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.intoDrawMo;

public class BigEgg extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BigEgg.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final AbstractCard status;

    public BigEgg(AbstractCreature owner, int amount, AbstractCard status) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.status = status;
        updateDescription();
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && owner instanceof AbstractMonster) {
            intoDrawMo(status.makeStatEquivalentCopy(), amount, (AbstractMonster) owner);
        }
    }
    @Override
    public void updateDescription() {
        if (status != null) {
            description = POWER_DESCRIPTIONS[0] + amount + " " + FontHelper.colorString(status.name, "y") + POWER_DESCRIPTIONS[1];
        }
    }
}
