package ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class ThinStrings extends AbstractRuinaCard {
    public final static String ID = makeID(ThinStrings.class.getSimpleName());

    public ThinStrings(Puppeteer parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.WEAK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}