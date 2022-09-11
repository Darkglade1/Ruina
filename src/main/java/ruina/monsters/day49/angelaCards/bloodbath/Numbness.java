package ruina.monsters.day49.angelaCards.bloodbath;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Numbness extends AbstractRuinaCard {
    public final static String ID = makeID(Numbness.class.getSimpleName());
    private Act1Angela parent;

    public Numbness(Act1Angela parent) {
        super(ID, 1, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = parent.numbnessBlock;
        secondMagicNumber = baseSecondMagicNumber = parent.numbnessParalysis;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Numbness(parent); }
}