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
public class EnergyShield extends AbstractRuinaCard {
    public final static String ID = makeID(EnergyShield.class.getSimpleName());

    public EnergyShield(Chesed parent) {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        block = baseBlock = parent.ENERGY_SHIELD_BLOCK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}