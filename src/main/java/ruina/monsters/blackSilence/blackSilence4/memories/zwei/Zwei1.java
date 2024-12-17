package ruina.monsters.blackSilence.blackSilence4.memories.zwei;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.powers.RuinaMetallicize;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Zwei1 extends Zwei {
    public final static String ID = makeID(Zwei1.class.getSimpleName());

    public Zwei1(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(parent, adp(), new RuinaMetallicize(parent, magicNumber));
    }

    public AbstractCard makeCopy() {
        return new Zwei1(parent);
    }
}