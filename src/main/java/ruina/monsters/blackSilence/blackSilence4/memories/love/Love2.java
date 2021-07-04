package ruina.monsters.blackSilence.blackSilence4.memories.love;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.intoDiscardMo;

@AutoAdd.Ignore
public class Love2 extends Love {
    public final static String ID = makeID(Love2.class.getSimpleName());

    public Love2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        cardsToPreview = new Slimed();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        intoDiscardMo(new Slimed(), secondMagicNumber, parent);
    }
}