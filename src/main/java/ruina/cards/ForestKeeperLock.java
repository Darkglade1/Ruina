package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class ForestKeeperLock extends AbstractRuinaCard {
    public static final String ID = makeID(ForestKeeperLock.class.getSimpleName());
    private static final int STRENGTH_LOSS = 40;
    private static final int UP_STRENGTH_LOSS = 20;

    public ForestKeeperLock() {
        super(ID, 0, CardType.SKILL, AbstractCard.CardRarity.SPECIAL, CardTarget.ENEMY);
        magicNumber = baseMagicNumber = STRENGTH_LOSS;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new ApplyPowerAction(m, p, new StrengthPower(m, -this.magicNumber), -this.magicNumber));
        if (m != null && !m.hasPower(ArtifactPower.POWER_ID)) {
            atb(new ApplyPowerAction(m, p, new GainStrengthPower(m, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(UP_STRENGTH_LOSS);
    }

    @Override
    public float getTitleFontSize()
    {
        return 16;
    }

}