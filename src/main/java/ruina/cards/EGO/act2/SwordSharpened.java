package ruina.cards.EGO.act2;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class SwordSharpened extends AbstractEgoCard {
    public final static String ID = makeID(SwordSharpened.class.getSimpleName());

    public static final int WEAK = 2;
    public static final int PLAY_THRESHOLD = 3;

    public static final String POWER_ID = makeID("SwordCounter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SwordSharpened() {
        super(ID, 0, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = WEAK;
        secondMagicNumber = baseSecondMagicNumber = PLAY_THRESHOLD;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card = this;
        AbstractPower power = m.getPower(POWER_ID);
        if (power != null && power.amount >= PLAY_THRESHOLD - 1) {
            applyToTarget(m, p, new StunMonsterPower(m, 1));
            atb(new RemoveSpecificPowerAction(m, p, POWER_ID));
        } else {
            applyToTarget(m, p, new WeakPower(m, magicNumber, false));
            applyToTarget(m, p, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, m, 1) {
                @Override
                public void onUseCard(AbstractCard card, UseCardAction action) {
                    if (card instanceof SwordSharpened && action.target == owner && amount >= PLAY_THRESHOLD - 1) {
                        this.flash();
                        action.exhaustCard = true;
                    }
                }

                @Override
                public void updateDescription() {
                    if (amount == 1) {
                        description = FontHelper.colorString(card.name, "y") + POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[2];
                    } else {
                        description = FontHelper.colorString(card.name, "y") + POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                }
            });
        }
    }

    @Override
    public float getTitleFontSize()
    {
        return 11;
    }

    @Override
    public void upp() {
        shuffleBackIntoDrawPile = true;
        rawDescription = languagePack.getCardStrings(cardID).UPGRADE_DESCRIPTION;
        initializeDescription();
    }
}