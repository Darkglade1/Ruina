package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class FalsePresent extends AbstractRuinaCard {
    public final static String ID = makeID(FalsePresent.class.getSimpleName());
    private static final int COST = 0;

    public FalsePresent() {
        super(ID, COST, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
        isEthereal = true;
        exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    public void upp() {

    }
}