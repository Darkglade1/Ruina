package ruina.monsters.eventboss.yan.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.eventboss.yan.monster.yanHand;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_BalefulBrand extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_BalefulBrand.class.getSimpleName());

    public CHRBOSS_BalefulBrand(yanDistortion parent) {
        super(ID, 0, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = parent.brandDmg;
        magicNumber = baseMagicNumber = parent.brandErosion;
    }

    public CHRBOSS_BalefulBrand(yanHand parent) {
        super(ID, 0, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = parent.brandDmg;
        magicNumber = baseMagicNumber = parent.brandErosion;
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}