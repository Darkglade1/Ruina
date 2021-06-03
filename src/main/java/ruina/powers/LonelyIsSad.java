package ruina.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;

import static ruina.util.Wiz.monsterList;

public class LonelyIsSad extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(LonelyIsSad.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int DAMAGE_INCREASE = 100;

    public LonelyIsSad(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, DAMAGE_INCREASE);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL && monsterList().size() == 1) {
            return damage * (1 + (float)DAMAGE_INCREASE / 100);
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}