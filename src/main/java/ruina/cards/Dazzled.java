package ruina.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;

public class Dazzled extends AbstractRuinaCard {
    public final static String ID = makeID(Dazzled.class.getSimpleName());
    private static final int COST = 1;
    private static final int UP_COST = 2;
    private static final int COST_INCREASE = 1;

    public Dazzled() {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = COST_INCREASE;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onRetained() {
        this.modifyCostForCombat(magicNumber);
    }

    @Override
    public void upp() {
        upgradeBaseCost(UP_COST);
    }
}