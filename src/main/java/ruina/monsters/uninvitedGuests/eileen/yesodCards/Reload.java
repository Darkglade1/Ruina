package ruina.monsters.uninvitedGuests.eileen.yesodCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.eileen.Yesod;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Reload extends AbstractRuinaCard {
    public final static String ID = makeID(Reload.class.getSimpleName());

    public Reload(Yesod parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        baseBlock = parent.BLOCK;
        magicNumber = baseMagicNumber = parent.DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}