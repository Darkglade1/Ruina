package ruina.cards.EGO.act3;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.cardmods.ExhaustMod;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Beak extends AbstractEgoCard {
    public final static String ID = makeID(Beak.class.getSimpleName());

    public static final String POWER_ID = makeID("Beak");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Beak() {
        super(ID, 1, CardType.POWER, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), 1) {
            @Override
            public void atStartOfTurnPostDraw() {
                for (int i = 0; i < amount; i++) {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (!adp().hand.isEmpty()) {
                                AbstractCard mostExpensiveCard = adp().hand.group.get(0);
                                int highestCost = mostExpensiveCard.costForTurn;
                                for (AbstractCard card : adp().hand.group) {
                                    if (card.costForTurn > highestCost) {
                                        mostExpensiveCard = card;
                                        highestCost = card.costForTurn;
                                    }
                                }
                                flash();
                                mostExpensiveCard.cost = 0;
                                mostExpensiveCard.costForTurn = 0;
                                mostExpensiveCard.isCostModified = true;
                                mostExpensiveCard.superFlash(Color.GOLD.cpy());
                                CardModifierManager.addModifier(mostExpensiveCard, new ExhaustMod());
                            }
                            this.isDone = true;
                        }
                    });
                }
            }

            @Override
            public void updateDescription() {
                if (amount == 1) {
                    description = POWER_DESCRIPTIONS[0];
                } else {
                    description = POWER_DESCRIPTIONS[1] + amount + POWER_DESCRIPTIONS[2];
                }
            }
        });
    }

    @Override
    public void upp() {
        isInnate = true;
    }
}