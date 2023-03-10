package ruina.monsters.blackSilence.blackSilence4.memories.blue;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Blue1 extends Blue {
    public final static String ID = makeID(Blue1.class.getSimpleName());

    public Blue1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        parent.increaseNumIntents();
    }

    public AbstractCard makeCopy() {
        return new Blue1(parent);
    }
}