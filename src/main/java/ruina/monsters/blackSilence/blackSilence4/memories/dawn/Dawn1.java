package ruina.monsters.blackSilence.blackSilence4.memories.dawn;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.intoDrawMo;

@AutoAdd.Ignore
public class Dawn1 extends Dawn {
    public final static String ID = makeID(Dawn1.class.getSimpleName());

    public Dawn1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
        this.cardsToPreview = new Burn();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        intoDrawMo(new Burn(), magicNumber, parent);
    }

    public AbstractCard makeCopy() {
        return new Dawn1(parent);
    }
}