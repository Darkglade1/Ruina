package ruina.monsters.uninvitedGuests.tanya.geburaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_LevelSlash;
import ruina.monsters.uninvitedGuests.tanya.Gebura;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Ally_LevelSlash extends AbstractRuinaCard {
    public final static String ID = makeID(Ally_LevelSlash.class.getSimpleName());

    public Ally_LevelSlash(Gebura parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, CHRBOSS_LevelSlash.class.getSimpleName());
        magicNumber = baseMagicNumber = parent.STRENGTH;
        secondMagicNumber = baseSecondMagicNumber = parent.level_threshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}