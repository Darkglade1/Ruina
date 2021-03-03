package ruina.monsters.eventboss.redMist.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class CHRBOSS_FocusSpirit extends AbstractEgoCard {
    public final static String ID = makeID(CHRBOSS_FocusSpirit.class.getSimpleName());

    public static final int BLOCK = 12;
    public static final int STR = 1;

    public CHRBOSS_FocusSpirit() {
        super(ID, 2, CardType.SKILL, CardTarget.SELF);
        block = baseBlock = 12;
        magicNumber = baseMagicNumber = STR;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}