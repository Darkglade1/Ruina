package ruina.monsters.blackSilence.blackSilence4.memories.zwei;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Zwei2 extends Zwei {
    public final static String ID = makeID(Zwei2.class.getSimpleName());

    public Zwei2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(parent, parent, new PlatedArmorPower(parent, secondMagicNumber));
    }

    public AbstractCard makeCopy() {
        return new Zwei2(parent);
    }
}