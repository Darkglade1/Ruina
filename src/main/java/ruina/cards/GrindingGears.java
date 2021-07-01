package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.GrindingGearsAction;
import ruina.monsters.act1.singingMachine.SingingMachineMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class GrindingGears extends AbstractRuinaCard {
    public final static String ID = makeID(GrindingGears.class.getSimpleName());
    private static final int COST = 1;
    private static final int HP_LOSS = 5;
    private static final int NUM_CARD = 1;

    private SingingMachineMonster machine;

    public GrindingGears(SingingMachineMonster machine) {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        magicNumber = baseMagicNumber = HP_LOSS;
        secondMagicNumber = baseSecondMagicNumber = NUM_CARD;
        this.machine = machine;
        isEthereal = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new LoseHPAction(p, p, magicNumber));
        atb(new GrindingGearsAction(secondMagicNumber, machine));
    }

    @Override
    public AbstractCard makeCopy() {
        return new GrindingGears(machine);
    }

    @Override
    public void upp() {

    }
}