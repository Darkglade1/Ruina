package ruina.monsters.blackSilence.blackSilence4.memories.zwei;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.intoDrawMo;

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
        applyToTarget(parent, parent, new MetallicizePower(parent, magicNumber));
    }
}