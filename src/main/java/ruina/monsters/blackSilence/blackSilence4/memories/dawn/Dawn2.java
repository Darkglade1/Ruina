package ruina.monsters.blackSilence.blackSilence4.memories.dawn;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.powers.Protection;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Dawn2 extends Dawn {
    public final static String ID = makeID(Dawn2.class.getSimpleName());

    public Dawn2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(parent, parent, new Protection(parent, secondMagicNumber));
    }
}