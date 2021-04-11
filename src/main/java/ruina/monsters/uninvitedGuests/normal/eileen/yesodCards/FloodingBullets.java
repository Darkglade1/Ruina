package ruina.monsters.uninvitedGuests.normal.eileen.yesodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class FloodingBullets extends AbstractRuinaCard {
    public final static String ID = makeID(FloodingBullets.class.getSimpleName());

    public FloodingBullets(Yesod parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.bulletHits;
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