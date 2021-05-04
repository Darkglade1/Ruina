package ruina.monsters.theHead.baralCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.theHead.Baral;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class SerumW extends AbstractRuinaCard {
    public final static String ID = makeID(SerumW.class.getSimpleName());
    private Baral parent;

    public SerumW(Baral parent) {
        super(ID, -2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.SERUM_W_DAMAGE;
        magicNumber = baseMagicNumber = parent.KILL_THRESHOLD;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new SerumW(parent); }
}