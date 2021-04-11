package ruina.monsters.uninvitedGuests.normal.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Duel extends AbstractRuinaCard {
    public final static String ID = makeID(Duel.class.getSimpleName());

    public Duel(Hod parent) {
        super(ID, 3, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.DUEL_BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}