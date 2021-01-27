package ruina.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.util.Wiz;

import static ruina.RuinaMod.makeID;

public class FragmentOfBliss extends AbstractRuinaCard {
    public final static String ID = makeID(FragmentOfBliss.class.getSimpleName());
    private static final int BLOCK = 30;

    public FragmentOfBliss() {
        super(ID, 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        magicNumber = baseMagicNumber = BLOCK;
        isEthereal = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Wiz.block(p, magicNumber);
    }

    public void upp() {

    }

    @Override
    public boolean canUpgrade() {
        return false;
    }
}