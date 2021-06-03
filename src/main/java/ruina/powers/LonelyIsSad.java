package ruina.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class LonelyIsSad extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(LonelyIsSad.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public boolean doubleDamage = false;

    public LonelyIsSad(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL && doubleDamage) {
            return damage * 2;
        }
        else if(type == DamageInfo.DamageType.NORMAL){
            return damage * (1.0f - ((float) 50 / 100));
        } else {
            return damage;
        }
    }

    @Override
    public void updateDescription() {
        description = doubleDamage ? DESCRIPTIONS[1] : DESCRIPTIONS[0];
    }
}