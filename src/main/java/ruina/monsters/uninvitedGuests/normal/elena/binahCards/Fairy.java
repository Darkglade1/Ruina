package ruina.monsters.uninvitedGuests.normal.elena.binahCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Fairy extends AbstractRuinaCard {
    public final static String ID = makeID(Fairy.class.getSimpleName());

    public Fairy(Binah parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.fairyHits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { this.name = cardStrings.EXTENDED_DESCRIPTION[0]; }
}