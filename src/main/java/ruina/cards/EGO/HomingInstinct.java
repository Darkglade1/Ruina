package ruina.cards.EGO;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.actions.CallbackExhaustAction;
import ruina.powers.AbstractLambdaPower;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class HomingInstinct extends AbstractEgoCard {
    public final static String ID = makeID(HomingInstinct.class.getSimpleName());

    public static final int EXHAUST = 1;
    public static final int COST = 2;
    public static final int UP_COST = 1;

    public static final String POWER_ID = makeID("HomingInstinct");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HomingInstinct() {
        super(ID, COST, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = EXHAUST;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
            for (AbstractCard card : abstractCards) {
                int energy = card.costForTurn;
                if (energy > 0) {
                    applyToTarget(adp(), adp(), new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), energy) {
                        @Override
                        public void onEnergyRecharge() {
                            this.flash();
                            adp().gainEnergy(this.amount);
                        }
                        @Override
                        public void updateDescription() {
                            description = POWER_DESCRIPTIONS[0];
                            for (int i = 0; i < amount; i++) {
                                description += "[E] ";
                            }
                            description += POWER_DESCRIPTIONS[1];
                        }
                    });
                }
            }
        };
        atb(new CallbackExhaustAction(magicNumber, false, false, false, consumer));
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}