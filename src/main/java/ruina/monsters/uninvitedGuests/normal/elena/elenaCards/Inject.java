package ruina.monsters.uninvitedGuests.normal.elena.elenaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Elena;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Inject extends AbstractRuinaCard {
    public final static String ID = makeID(Inject.class.getSimpleName());
    Elena parent;

    public Inject(Elena parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO, makeImagePath("cards/" + Circulation.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.BLEED;
        secondMagicNumber = baseSecondMagicNumber = parent.INJECT_STR;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upp() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new Inject(parent);
    }
}