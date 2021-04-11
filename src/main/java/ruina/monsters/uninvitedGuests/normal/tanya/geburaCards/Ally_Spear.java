package ruina.monsters.uninvitedGuests.normal.tanya.geburaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_Spear;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Ally_Spear extends AbstractRuinaCard {
    public final static String ID = makeID(Ally_Spear.class.getSimpleName());

    public Ally_Spear(Gebura parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_Spear.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.spearHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}