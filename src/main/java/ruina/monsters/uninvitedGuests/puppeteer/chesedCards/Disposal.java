package ruina.monsters.uninvitedGuests.puppeteer.chesedCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.puppeteer.Chesed;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Disposal extends AbstractRuinaCard {
    public final static String ID = makeID(Disposal.class.getSimpleName());

    public Disposal(Chesed parent) {
        super(ID, 5, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.DISPOSAL_HITS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}