package ruina.monsters.day49.angelaCards.frostsplinter;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.day49.Act1Angela;
import ruina.monsters.day49.Act4Angela;
import ruina.monsters.day49.angelaCards.bloodbath.Numbness;
import ruina.monsters.eventboss.redMist.monster.RedMist;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeImagePath;

@AutoAdd.Ignore
public class BitterCold extends AbstractRuinaCard {
    public final static String ID = makeID(BitterCold.class.getSimpleName());
    private Act4Angela parent;

    public BitterCold(Act4Angela parent) {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY, RuinaMod.Enums.EGO, makeImagePath("cards/" + "SnowQueenIce" + ".png"));
        damage = baseDamage = parent.bitterColdDamage;
        magicNumber = baseMagicNumber = parent.bitterColdChill;
        secondMagicNumber = baseSecondMagicNumber = parent.bitterColdThreshold;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    @Override
    public AbstractCard makeCopy() { return new BitterCold(parent); }
}