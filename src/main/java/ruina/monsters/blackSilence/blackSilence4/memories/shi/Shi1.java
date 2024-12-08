package ruina.monsters.blackSilence.blackSilence4.memories.shi;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Shi1 extends Shi {
    public final static String ID = makeID(Shi1.class.getSimpleName());

    public Shi1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(adp(), parent, new StrengthPower(adp(), -magicNumber));
    }

    public AbstractCard makeCopy() {
        return new Shi1(parent);
    }
}