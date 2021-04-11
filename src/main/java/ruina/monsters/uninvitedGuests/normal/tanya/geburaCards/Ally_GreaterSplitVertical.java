package ruina.monsters.uninvitedGuests.normal.tanya.geburaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitVertical;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Ally_GreaterSplitVertical extends AbstractRuinaCard {
    public final static String ID = makeID(Ally_GreaterSplitVertical.class.getSimpleName());

    public Ally_GreaterSplitVertical(Gebura parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_GreaterSplitVertical.class.getSimpleName() + ".png"));
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}