package ruina.monsters.uninvitedGuests.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.uninvitedGuests.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_Largo extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_Largo.class.getSimpleName());

    public CHRBOSS_Largo(Argalia parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.largoDamage;
        block = baseBlock = parent.largoBlock;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}