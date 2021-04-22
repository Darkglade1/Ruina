package ruina.monsters.blackSilence.blackSilence1.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.blackSilence.blackSilence1.BlackSilence1;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_OLDBOYS;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class OldBoys extends AbstractRuinaCard {
    public final static String ID = makeID(OldBoys.class.getSimpleName());

    public OldBoys(BlackSilence1 parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + CHRALLY_OLDBOYS.class.getSimpleName() + ".png"));
        damage = baseDamage = parent.OLD_BOY_DAMAGE;
        block = baseBlock = parent.OLD_BOY_BLOCK;
    }

    @Override
    public float getTitleFontSize()
    {
        return 16;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}