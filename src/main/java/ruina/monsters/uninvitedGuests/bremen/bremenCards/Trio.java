package ruina.monsters.uninvitedGuests.bremen.bremenCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Bremen;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Trio extends AbstractRuinaCard {
    public final static String ID = makeID(Trio.class.getSimpleName());
    Bremen parent;

    public Trio(Bremen parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + Tendon.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.trioHits;
        secondMagicNumber = baseSecondMagicNumber = parent.MELODY_LENGTH_INCREASE;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Trio(parent);
    }
}