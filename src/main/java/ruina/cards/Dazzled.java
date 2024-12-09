package ruina.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.UnplayableMod;

import static ruina.RuinaMod.makeID;

public class Dazzled extends AbstractRuinaCard {
    public final static String ID = makeID(Dazzled.class.getSimpleName());
    private static final int COST = 1;
    private static final int UP_COST = 2;
    private static final int COST_INCREASE = 1;
    private static final int UNPLAYABLE_THRESHOLD = 4;

    public Dazzled() {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = COST_INCREASE;
        secondMagicNumber = baseSecondMagicNumber = UNPLAYABLE_THRESHOLD;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        this.modifyCostForCombat(magicNumber);
        if (this.costForTurn >= UNPLAYABLE_THRESHOLD) {
            CardModifierManager.addModifier(this, new UnplayableMod());
            this.cost = this.costForTurn = -2;
        }
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}