package ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class PullingStrings extends AbstractRuinaCard {
    public final static String ID = makeID(PullingStrings.class.getSimpleName());

    public PullingStrings(Puppeteer parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
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