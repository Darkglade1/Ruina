package ruina.monsters.eventboss.yan.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.yan.monster.yanDistortion;
import ruina.monsters.eventboss.yan.monster.yanHand;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_Compress extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_Compress.class.getSimpleName());

    public CHRBOSS_Compress(yanDistortion parent) {
        super(ID, 0, CardType.SKILL, CardTarget.SELF);
        block = baseBlock = parent.compressBlock;
    }

    public CHRBOSS_Compress(yanHand parent) {
        super(ID, 0, CardType.SKILL, CardTarget.SELF);
        block = baseBlock = parent.compressBlock;
    }


    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}