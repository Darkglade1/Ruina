package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Melody extends AbstractRuinaCard {
    public final static String ID = makeID(Melody.class.getSimpleName());

    public Melody() {
        super(ID, -2, CardType.CURSE, CardRarity.CURSE, CardTarget.NONE, CardColor.CURSE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    public void upp() {

    }
}