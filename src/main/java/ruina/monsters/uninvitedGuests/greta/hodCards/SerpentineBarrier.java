package ruina.monsters.uninvitedGuests.greta.hodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.greta.Hod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class SerpentineBarrier extends AbstractRuinaCard {
    public final static String ID = makeID(SerpentineBarrier.class.getSimpleName());

    public SerpentineBarrier(Hod parent) {
        super(ID, 3, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BARRIER_BLOCK;
    }

    @Override
    public float getTitleFontSize()
    {
        return 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}