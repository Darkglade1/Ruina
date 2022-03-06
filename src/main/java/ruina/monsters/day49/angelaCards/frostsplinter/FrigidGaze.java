package ruina.monsters.day49.angelaCards.frostsplinter;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act4Angela;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class FrigidGaze extends AbstractRuinaCard {
    public final static String ID = makeID(FrigidGaze.class.getSimpleName());
    private Act4Angela parent;

    public FrigidGaze(Act4Angela parent) {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        block = baseBlock = parent.frigidGazeBlock;
        magicNumber = baseMagicNumber = parent.frigidGazeStrength;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new FrigidGaze(parent); }
}