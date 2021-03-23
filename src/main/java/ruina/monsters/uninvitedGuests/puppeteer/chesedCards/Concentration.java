package ruina.monsters.uninvitedGuests.puppeteer.chesedCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.puppeteer.Chesed;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Concentration extends AbstractRuinaCard {
    public final static String ID = makeID(Concentration.class.getSimpleName());

    public Concentration(Chesed parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.CONCENTRATE_HITS;
        block = baseBlock = parent.CONCENTRATE_BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}