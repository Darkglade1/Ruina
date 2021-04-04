package ruina.monsters.uninvitedGuests.philip.malkuthCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.philip.Malkuth;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class RagingStorm extends AbstractRuinaCard {
    public final static String ID = makeID(RagingStorm.class.getSimpleName());

    public RagingStorm(Malkuth parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.stormHits;
        secondMagicNumber = baseSecondMagicNumber = parent.VULNERABLE;
    }

    @Override
    public float getTitleFontSize()
    {
        return 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}