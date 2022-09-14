package ruina.monsters.day49.angelaCards.aspiration;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_ALLAS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Inclination extends AbstractRuinaCard {
    public final static String ID = makeID(Inclination.class.getSimpleName());
    private Act2Angela parent;

    public Inclination(Act2Angela parent) {
        super(ID, 2, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.inclinationVuln;
        secondMagicNumber = baseSecondMagicNumber = parent.inclinationCards;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new Inclination(parent); }
}