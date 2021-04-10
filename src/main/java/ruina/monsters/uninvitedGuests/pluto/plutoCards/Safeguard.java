package ruina.monsters.uninvitedGuests.pluto.plutoCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.bremen.Bremen;
import ruina.monsters.uninvitedGuests.pluto.monster.Pluto;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Safeguard extends AbstractRuinaCard {
    public final static String ID = makeID(Safeguard.class.getSimpleName());
    Pluto parent;

    public Safeguard(Pluto parent) {
        super(ID, 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        baseBlock = parent.magicSafeguardBlock;
        magicNumber = baseMagicNumber = parent.magicSafeguardStr;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Safeguard(parent);
    }
}