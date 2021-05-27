package ruina.cards.performance;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Conductor extends AbstractPerformanceCard {
    public final static String ID = makeID(Conductor.class.getSimpleName());
    private static final int COST = -2;
    private static final int COST_UP = 1;
    private static final int UP_COST_UP = 1;
    private static final int NUM_PERFORMER_CARDS = 4;

    public Conductor() {
        super(ID, COST);
        magicNumber = baseMagicNumber = COST_UP;
        secondMagicNumber = baseSecondMagicNumber = NUM_PERFORMER_CARDS;
        selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int numPerformerCards = 0;
                ArrayList<AbstractCard> performerCards = new ArrayList<>();
                System.out.println(adp().hand.group);
                for (AbstractCard card : adp().hand.group) {
                    if (card instanceof AbstractPerformanceCard && card.costForTurn >= 0) {
                        card.modifyCostForCombat(magicNumber);
                        card.flash();
                        numPerformerCards++;
                        performerCards.add(card);
                        System.out.println(card);
                    }
                }
                if (numPerformerCards >= secondMagicNumber) {
                    flash();
                    for (AbstractCard performerCard : performerCards) {
                        performerCard.onRetained();
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_COST_UP);
    }
}