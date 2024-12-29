package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class FourthMatchFlame extends AbstractEgoCard {
    public final static String ID = makeID(FourthMatchFlame.class.getSimpleName());

    public static final String POWER_ID = makeID("FourthMatchFlame");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final int POWER_DAMAGE = 7;
    private static final int UP_POWER_DAMAGE = 3;
    private static final int BURNS = 1;

    public FourthMatchFlame() {
        super(ID, 1, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = POWER_DAMAGE;
        secondMagicNumber = baseSecondMagicNumber = BURNS;
        cardsToPreview = new Burn();
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        intoDiscard(new Burn(), secondMagicNumber);
        applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), magicNumber) {

            @Override
            public void atEndOfTurn(boolean isPlayer) {
                if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                    this.flash();
                    atb(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_POWER_DAMAGE);
    }
}