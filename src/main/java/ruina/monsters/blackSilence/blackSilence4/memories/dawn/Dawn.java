package ruina.monsters.blackSilence.blackSilence4.memories.dawn;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.monsters.blackSilence.blackSilence4.memories.yun.Yun;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Dawn extends AbstractRuinaCard {
    public final static String ID = makeID(Dawn.class.getSimpleName());
    BlackSilence4 parent;

    public Dawn(BlackSilence4 parent) {
        super(ID, -2, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        this.parent = parent;
        magicNumber = baseMagicNumber = parent.dawnBurns;
        secondMagicNumber = baseSecondMagicNumber = parent.dawnProtection;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Dawn(parent);
    }
}