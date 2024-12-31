package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import ruina.actions.CallbackExhaustAction;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class Wingbeat extends AbstractEgoCard {
    public final static String ID = makeID(Wingbeat.class.getSimpleName());
    public static final int EXHAUST = 2;
    public static final int COST = 1;
    public static final int UP_COST = 0;

    public Wingbeat() {
        super(ID, COST, CardType.SKILL, CardTarget.SELF);
        magicNumber = baseMagicNumber = EXHAUST;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Consumer<ArrayList<AbstractCard>> consumer = abstractCards -> {
            int totalCost = 0;
            for (AbstractCard card : abstractCards) {
                if (card.costForTurn >= 0) {
                    totalCost += card.costForTurn;
                }
            }
            if (totalCost > 0) {
                atb(new HealAction(p, p, totalCost));
                applyToTarget(p, p, new DexterityPower(p, totalCost));
            }
        };
        atb(new CallbackExhaustAction(magicNumber, false, true, true, consumer));
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}