package ruina.monsters.uninvitedGuests.tanya.tanyaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.tanya.Tanya;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Fisticuffs extends AbstractRuinaCard {
    public final static String ID = makeID(Fisticuffs.class.getSimpleName());

    public Fisticuffs(Tanya parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        magicNumber = baseMagicNumber = parent.WEAK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }
}