package ruina.monsters.blackSilence.blackSilence4.memories.yun;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.intoDrawMo;

@AutoAdd.Ignore
public class Yun1 extends Yun {
    public final static String ID = makeID(Yun1.class.getSimpleName());

    public Yun1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        intoDrawMo(new Dazed(), magicNumber, parent);
    }
}