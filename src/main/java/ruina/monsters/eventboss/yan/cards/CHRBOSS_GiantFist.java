package ruina.monsters.eventboss.yan.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.eventboss.yan.monster.yanHand;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_GiantFist extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_GiantFist.class.getSimpleName());

    public CHRBOSS_GiantFist(yanDistortion parent) {
        super(ID, 0, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = parent.fistDMG;
        magicNumber = baseMagicNumber = parent.fistPara;
    }

    public CHRBOSS_GiantFist(yanHand parent) {
        super(ID, 0, CardType.ATTACK, CardTarget.SELF);
        damage = baseDamage = parent.fistDMG;
        magicNumber = baseMagicNumber = parent.fistPara;
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}