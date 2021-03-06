package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FocusSpirit extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_FocusSpirit.class.getSimpleName());

    public CHRBOSS_FocusSpirit(RedMist parent) {
        super(ID, 2, CardType.SKILL, CardTarget.SELF);
        block = baseBlock = parent.focusSpiritBlock;
        magicNumber = baseMagicNumber = parent.focusSpiritStr;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}