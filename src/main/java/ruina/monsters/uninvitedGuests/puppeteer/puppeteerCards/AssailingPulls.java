package ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.puppeteer.Puppeteer;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class AssailingPulls extends AbstractRuinaCard {
    public final static String ID = makeID(AssailingPulls.class.getSimpleName());

    public AssailingPulls(Puppeteer parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.VULNERABLE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}