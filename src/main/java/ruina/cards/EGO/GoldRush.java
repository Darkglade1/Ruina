package ruina.cards.EGO;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class GoldRush extends AbstractEgoCard {
    public final static String ID = makeID(GoldRush.class.getSimpleName());

    public static final int STR = 4;
    public static final int UP_STR = 2;

    public GoldRush() {
        super(ID, 3, CardType.POWER, CardTarget.SELF);
        magicNumber = baseMagicNumber = STR;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        applyToTarget(p, p, new StrengthPower(p, magicNumber));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() >= 2 && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2).type == CardType.ATTACK) {
                    AbstractPower strength = p.getPower(StrengthPower.POWER_ID);
                    if (strength != null) {
                        int strAmt = strength.amount;
                        att(new ApplyPowerAction(p, p, new StrengthPower(p, strAmt)));
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void triggerOnGlowCheck() {
        if (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty() && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1).type == CardType.ATTACK) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_STR);
    }
}