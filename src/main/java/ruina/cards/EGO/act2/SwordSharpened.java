package ruina.cards.EGO.act2;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class SwordSharpened extends AbstractEgoCard {
    public final static String ID = makeID(SwordSharpened.class.getSimpleName());

    public static final int DEBUFF = 1;
    public static final int PLAY_THRESHOLD = 3;

    public static final String POWER_ID = makeID("SwordCounter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public SwordSharpened() {
        super(ID, 0, CardType.SKILL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = DEBUFF;
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
            applyToTarget(m, p, new VulnerablePower(m, magicNumber, false));
            applyToTarget(m, p, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, m, 1) {
                @Override
                public void onUseCard(AbstractCard card, UseCardAction action) {
                    if (card instanceof SwordSharpened && action.target == owner && amount >= PLAY_THRESHOLD - 1) {
                        this.flash();
                        makePowerRemovable(this);
                        atb(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
                        if (!card.purgeOnUse) {
                            action.exhaustCard = true;
                        } else {
                            atb(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    for (AbstractCard c : adp().discardPile.group) {
                                        if (c.uuid.equals(card.uuid)) {
                                            att(new ExhaustSpecificCardAction(c, adp().discardPile));
                                            this.isDone = true;
                                            return;
                                        }
                                    }
                                    for (AbstractCard c : adp().drawPile.group) {
                                        if (c.uuid.equals(card.uuid)) {
                                            att(new ExhaustSpecificCardAction(c, adp().drawPile));
                                            this.isDone = true;
                                            return;
                                        }
                                    }
                                    for (AbstractCard c : adp().hand.group) {
                                        if (c.uuid.equals(card.uuid)) {
                                            att(new ExhaustSpecificCardAction(c, adp().hand));
                                            this.isDone = true;
                                            return;
                                        }
                                    }
                                    for (AbstractCard c : adp().limbo.group) {
                                        if (c.uuid.equals(card.uuid)) {
                                            att(new ExhaustSpecificCardAction(c, adp().limbo));
                                            this.isDone = true;
                                            return;
                                        }
                                    }
                                    this.isDone = true;
                                }
                            });
                        }
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