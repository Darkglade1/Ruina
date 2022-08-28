package ruina.monsters.blackSilence.blackSilence4.memories.Shi;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Shi extends AbstractRuinaCard {
    public final static String ID = makeID(Shi.class.getSimpleName());
    BlackSilence4 parent;

    public Shi(BlackSilence4 parent) {
        super(ID, -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        if (RuinaMod.isHumility()) {
            this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[2] + " NL " + cardStrings.EXTENDED_DESCRIPTION[3];
            magicNumber = baseMagicNumber = parent.shiStrLoss;
            secondMagicNumber = baseSecondMagicNumber = parent.shiDexLoss;
        } else {
            this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0] + " NL " + cardStrings.EXTENDED_DESCRIPTION[1];
            magicNumber = baseMagicNumber = parent.shiWeak;
            secondMagicNumber = baseSecondMagicNumber = parent.shiFrail;
        }
        this.initializeDescription();
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