package ruina.monsters.uninvitedGuests.normal.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class SnakeSlit extends AbstractRuinaCard {
    public final static String ID = makeID(SnakeSlit.class.getSimpleName());

    public SnakeSlit(Hod parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.snakeHits;
        secondMagicNumber = baseSecondMagicNumber = parent.STRENGTH;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}