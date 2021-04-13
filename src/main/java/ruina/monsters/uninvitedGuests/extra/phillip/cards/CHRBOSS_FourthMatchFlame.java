package ruina.monsters.uninvitedGuests.extra.phillip.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.philip.Philip;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FourthMatchFlame extends AbstractRuinaCard {
    public final static String ID = makeID(CHRBOSS_FourthMatchFlame.class.getSimpleName());
    public static final int DAMAGE = 60;
    public static final int BURN = 10;
    public static final int UPG_DAMAGE = 10;
    public static final int UPG_BURN = 10;

    public CHRBOSS_FourthMatchFlame() {
        super(ID, 4, CardType.ATTACK, CardRarity.COMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = BURN;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() {
        upgradeDamage(UPG_DAMAGE);
        upgradeMagicNumber(UPG_BURN);
    }

    @Override
    public AbstractCard makeCopy() { return new CHRBOSS_FourthMatchFlame(); }
}