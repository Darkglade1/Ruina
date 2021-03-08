package ruina.monsters.eventboss.yan.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.eventboss.yan.monster.yanHand;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_DistortedBlade extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_DistortedBlade.class.getSimpleName());

    public CHRBOSS_DistortedBlade(yanDistortion parent) {
        super(ID, 7, CardType.ATTACK, CardTarget.SELF);
        upgrade();
        damage = baseDamage = parent.bladeDMG;
        magicNumber = baseMagicNumber = parent.bladeErosion;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}