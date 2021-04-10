package ruina.monsters.uninvitedGuests.puppeteer.chesedCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.argalia.monster.Roland;
import ruina.monsters.uninvitedGuests.puppeteer.Chesed;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class BattleCommand extends AbstractRuinaCard {
    public final static String ID = makeID(BattleCommand.class.getSimpleName());

    public BattleCommand(Chesed parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.STRENGTH;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}