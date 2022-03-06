package ruina.monsters.day49.angelaCards.frostsplinter;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.day49.Act4Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class AbsoluteZero extends AbstractRuinaCard {
    public final static String ID = makeID(AbsoluteZero.class.getSimpleName());
    private Act4Angela parent;

    public AbsoluteZero(Act4Angela parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + FrostSplinterAngela.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.absoluteZeroFreeze;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new AbsoluteZero(parent); }
}