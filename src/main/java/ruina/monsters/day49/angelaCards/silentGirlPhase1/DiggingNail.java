package ruina.monsters.day49.angelaCards.silentGirlPhase1;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act2Angela;
import ruina.monsters.day49.Act5Angela;

import static ruina.RuinaMod.makeID;

@AutoAdd.Ignore
public class DiggingNail extends AbstractRuinaCard {
    public final static String ID = makeID(DiggingNail.class.getSimpleName());
    private Act5Angela parent;

    public DiggingNail(Act5Angela parent) {
        super(ID, 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO);
        damage = baseDamage = parent.diggingNailDamage;
        magicNumber = baseMagicNumber = parent.diggingNailFrail;
        this.parent = parent;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new DiggingNail(parent); }
}