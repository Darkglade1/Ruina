package ruina.monsters.uninvitedGuests.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.uninvitedGuests.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_ResonantScythe extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_ResonantScythe.class.getSimpleName());

    public static final int DAMAGE = 35;

    public CHRBOSS_ResonantScythe(Argalia parent) {
        super(ID, 2, CardType.ATTACK, CardTarget.ENEMY);
        damage = baseDamage = parent.scytheDamage;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}