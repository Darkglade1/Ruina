package ruina.monsters.uninvitedGuests.normal.bremen.netzachCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.yan.cards.CHRBOSS_BalefulBrand;
import ruina.monsters.uninvitedGuests.normal.bremen.Netzach;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class BalefulBrand extends AbstractRuinaCard {
    public final static String ID = makeID(BalefulBrand.class.getSimpleName());

    public BalefulBrand(Netzach parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_BalefulBrand.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.EROSION;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}