package ruina.monsters.blackSilence.blackSilence4.memories.liu;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun1;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Liu2 extends Liu {
    public final static String ID = makeID(Liu2.class.getSimpleName());

    public Liu2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(adp(), parent, new VulnerablePower(adp(), secondMagicNumber, true));
    }

    public AbstractCard makeCopy() {
        return new Liu2(parent);
    }
}