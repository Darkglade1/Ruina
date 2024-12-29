package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class Laetitia extends AbstractEgoCard {
    public final static String ID = makeID(Laetitia.class.getSimpleName());

    private static final int COST = 2;
    private static final int VALID_COST_1 = 0;
    private static final int VALID_COST_2 = 1;

    public static final String POWER_ID = makeID("LaetitiaPower");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Laetitia() {
        super(ID, COST, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = VALID_COST_1;
        secondMagicNumber = baseSecondMagicNumber = VALID_COST_2;
        isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), 1) {

            private int cardsDoubledThisTurn = 0;

            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (!card.purgeOnUse && (card.costForTurn == magicNumber || card.costForTurn == secondMagicNumber || card.freeToPlay()) && this.cardsDoubledThisTurn < this.amount) {
                    ++this.cardsDoubledThisTurn;
                    this.flash();
                    AbstractMonster m = null;
                    if (action.target != null) {
                        m = (AbstractMonster)action.target;
                    }

                    AbstractCard tmp = card.makeSameInstanceOf();
                    AbstractDungeon.player.limbo.addToBottom(tmp);
                    tmp.current_x = card.current_x;
                    tmp.current_y = card.current_y;
                    tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                    tmp.target_y = (float)Settings.HEIGHT / 2.0F;
                    if (m != null) {
                        tmp.calculateCardDamage(m);
                    }

                    tmp.purgeOnUse = true;
                    AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
                    updateDescription();
                }
            }

            @Override
            public void atStartOfTurn() {
                this.cardsDoubledThisTurn = 0;
                updateDescription();
            }

            @Override
            public void updateDescription() {
                int amountLeft = amount - cardsDoubledThisTurn;
                if (amountLeft < 0) {
                    amountLeft = 0;
                }
                description = POWER_DESCRIPTIONS[0] + amount + " " + magicNumber + POWER_DESCRIPTIONS[1] + secondMagicNumber + POWER_DESCRIPTIONS[2] + amountLeft + POWER_DESCRIPTIONS[3];
            }
        });
    }

    @Override
    public void upp() {
        isEthereal = false;
    }
}