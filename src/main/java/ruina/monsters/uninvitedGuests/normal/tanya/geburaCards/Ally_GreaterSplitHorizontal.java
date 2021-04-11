package ruina.monsters.uninvitedGuests.normal.tanya.geburaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitHorizontal;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Ally_GreaterSplitHorizontal extends AbstractRuinaCard {
    public final static String ID = makeID(Ally_GreaterSplitHorizontal.class.getSimpleName());

    public Ally_GreaterSplitHorizontal(Gebura parent) {
        super(ID, 6, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_GreaterSplitHorizontal.class.getSimpleName() + ".png"));
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