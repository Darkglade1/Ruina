package ruina.monsters.blackSilence.blackSilence4.memories.Shi;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import ruina.RuinaMod;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Shi2 extends Shi {
    public final static String ID = makeID(Shi2.class.getSimpleName());

    public Shi2(BlackSilence4 parent) {
        super(parent);
        if (RuinaMod.isHumility()) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[3];
        } else {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        }
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        if (RuinaMod.isHumility()) {
            applyToTarget(adp(), parent, new DexterityPower(adp(), -secondMagicNumber));
        } else {
            applyToTarget(adp(), parent, new FrailPower(adp(), secondMagicNumber, true));
        }
    }
}