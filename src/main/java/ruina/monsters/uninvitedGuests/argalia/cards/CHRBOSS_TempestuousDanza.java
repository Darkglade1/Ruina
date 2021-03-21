package ruina.monsters.uninvitedGuests.argalia.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.argalia.monster.Argalia;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_TempestuousDanza extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_TempestuousDanza.class.getSimpleName());

    public CHRBOSS_TempestuousDanza(Argalia parent) {
        super(ID, 7, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.tempestuousDamage;
        magicNumber = baseMagicNumber = parent.tempestuousHits;
        secondMagicNumber = baseSecondMagicNumber = parent.tempestuousVibrationInc;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}