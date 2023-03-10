package ruina.monsters.blackSilence.blackSilence4.memories.yun;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.intoDiscardMo;

@AutoAdd.Ignore
public class Yun2 extends Yun {
    public final static String ID = makeID(Yun2.class.getSimpleName());

    public Yun2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        cardsToPreview = new Wound();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        intoDiscardMo(new Wound(), secondMagicNumber, parent);
    }

    public AbstractCard makeCopy() {
        return new Yun2(parent);
    }
}