package ruina.powers;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class BrotherPower extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("BrotherPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private int brotherNum;
    AbstractMonster target;

    public BrotherPower(AbstractCreature owner, int amount, int brotherNum, AbstractMonster target) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.brotherNum = brotherNum;
        this.target = target;
        updateDescription();
    }

    @Override
    public void atEndOfRound() {
        if (!owner.halfDead) {
            switch (brotherNum) {
                case 1: {
                    flash();
                    applyToTarget(target, owner, new PlatedArmorPower(target, amount));
                    break;
                }
                case 2: {
                    flash();
                    intoDiscard(new Slimed(), amount);
                    break;
                }
                case 3: {
                    if (!target.hasPower(ArtifactPower.POWER_ID)) {
                        flash();
                        applyToTarget(target, owner, new ArtifactPower(target, amount));
                    }
                    break;
                }
                case 4: {
                    flash();
                    applyToTarget(adp(), owner, new WeakPower(adp(), amount, false));
                    break;
                }
                case 5: {
                    flash();
                    atb(new HealAction(target, owner, amount));
                    break;
                }
                case 6: {
                    flash();
                    applyToTarget(adp(), owner, new VulnerablePower(adp(), amount, false));
                    break;
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESCRIPTIONS[0] + DESCRIPTIONS[brotherNum], amount);
    }
}
