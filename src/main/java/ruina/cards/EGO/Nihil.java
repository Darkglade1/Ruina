package ruina.cards.EGO;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class Nihil extends AbstractEgoCard {
    public final static String ID = makeID(Nihil.class.getSimpleName());

    public static final int STR_LOSS = 2;
    public static final int UP_LOSS = 1;

    public Nihil() {
        super(ID, 1, CardType.SKILL, CardTarget.ALL_ENEMY);
        magicNumber = baseMagicNumber = STR_LOSS;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int strLoss = magicNumber;
        int handsize = p.hand.size() - 1; //excludes itself
        if (handsize <= 0) {
            strLoss *= 2;
        }
        for (AbstractMonster mo : monsterList()) {
            applyToTarget(mo, p, new StrengthPower(mo, -strLoss));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        if (adp().hand.size() == 1) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_LOSS);
    }
}