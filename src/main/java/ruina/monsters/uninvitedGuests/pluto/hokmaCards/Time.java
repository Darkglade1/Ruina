package ruina.monsters.uninvitedGuests.pluto.hokmaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.pluto.monster.Hokma;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Time extends AbstractRuinaCard {
    public final static String ID = makeID(Time.class.getSimpleName());

    public Time(Hokma parent) {
        super(ID, 3, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}