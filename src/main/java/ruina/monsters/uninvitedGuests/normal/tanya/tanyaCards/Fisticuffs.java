package ruina.monsters.uninvitedGuests.normal.tanya.tanyaCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.uninvitedGuests.normal.tanya.Tanya;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class Fisticuffs extends AbstractRuinaCard {
    public final static String ID = makeID(Fisticuffs.class.getSimpleName());
    Tanya parent;

    public Fisticuffs(Tanya parent) {
        super(ID, 0, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        this.parent = parent;
        magicNumber = baseMagicNumber = parent.WEAK;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() {
        return new Fisticuffs(parent);
    }
}