package ruina.monsters.blackSilence.blackSilence4.memories.liu;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Liu extends AbstractRuinaCard {
    public final static String ID = makeID(Liu.class.getSimpleName());
    BlackSilence4 parent;

    public Liu(BlackSilence4 parent) {
        super(ID, -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        this.parent = parent;
        magicNumber = baseMagicNumber = parent.liuStrength;
        secondMagicNumber = baseSecondMagicNumber = parent.liuVulnerable;
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
}