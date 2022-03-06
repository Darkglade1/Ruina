package ruina.monsters.day49.angelaCards.frostsplinter;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act4Angela;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class SwordOfFrost extends AbstractRuinaCard {
    public final static String ID = makeID(SwordOfFrost.class.getSimpleName());
    private Act4Angela parent;

    public SwordOfFrost(Act4Angela parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.swordOfFrost_damage;
        magicNumber = baseMagicNumber = parent.swordOfFrost_hits;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new SwordOfFrost(parent); }
}