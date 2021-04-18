package ruina.monsters.blackSilence.blackSilence4.memories.hana;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class Hana2 extends Hana {
    public final static String ID = makeID(Hana2.class.getSimpleName());

    public Hana2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        atb(new LoseHPAction(adp(), adp(), secondMagicNumber));
    }
}