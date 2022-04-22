package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class MagicBullet extends AbstractEgoCard {
    public final static String ID = makeID(MagicBullet.class.getSimpleName());

    public static final String POWER_ID = makeID("MagicBullet");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int UP_STR = 2;

    public MagicBullet() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = UP_STR;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(m, adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.DEBUFF, false, m, 1) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL) {
                    return damage * (1 + amount);
                } else {
                    return damage;
                }
            }

            @Override
            public void atEndOfRound() {
                atb(new RemoveSpecificPowerAction(this.owner, this.owner, this));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + (amount * 100) + POWER_DESCRIPTIONS[1];
            }
        });
        if (upgraded) {
            applyToTarget(adp(), adp(), new StrengthPower(adp(), magicNumber));
            applyToTarget(adp(), adp(), new LoseStrengthPower(adp(), magicNumber));
        }
    }

    @Override
    public void upp() {
        uDesc();
    }
}