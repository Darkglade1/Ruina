package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.eventboss.redMist.cards.CHRBOSS_GreaterSplitHorizontal;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_MOOK;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class Mook extends AbstractRuinaCard {
    public final static String ID = makeID(Mook.class.getSimpleName());

    public Mook(BlackSilence1 parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_MOOK.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.MOOK_DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}