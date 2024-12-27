package ruina.monsters.uninvitedGuests.normal.clown.oswaldCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.clown.Oswald;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Climax extends AbstractRuinaCard {
    public final static String ID = makeID(Climax.class.getSimpleName());
    Oswald parent;

    public Climax(Oswald parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.getClimaxHits();
        secondMagicNumber = baseSecondMagicNumber = parent.climaxHitIncrease;
        if (magicNumber != parent.initialClimaxHits) {
            isMagicNumberModified = true;
        }
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Climax(parent);
    }
}