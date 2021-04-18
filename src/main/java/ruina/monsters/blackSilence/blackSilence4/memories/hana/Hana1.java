package ruina.monsters.blackSilence.blackSilence4.memories.hana;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.makeInHand;

@AutoAdd.Ignore
public class Hana1 extends Hana {
    public final static String ID = makeID(Hana1.class.getSimpleName());

    public Hana1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        makeInHand(new Regret(), magicNumber);
    }
}