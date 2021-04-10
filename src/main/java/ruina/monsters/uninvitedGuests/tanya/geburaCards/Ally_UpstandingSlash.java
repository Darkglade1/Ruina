package ruina.monsters.uninvitedGuests.tanya.geburaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitHorizontal;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_UpstandingSlash;
import ruina.monsters.uninvitedGuests.tanya.Gebura;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Ally_UpstandingSlash extends AbstractRuinaCard {
    public final static String ID = makeID(Ally_UpstandingSlash.class.getSimpleName());

    public Ally_UpstandingSlash(Gebura parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRBOSS_UpstandingSlash.class.getSimpleName() + ".png"));
        magicNumber = baseMagicNumber = parent.VULNERABLE;
        secondMagicNumber = baseSecondMagicNumber = parent.upstanding_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}