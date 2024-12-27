package ruina.powers.act4;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Chesed;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class FinishingTouch extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(FinishingTouch.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    float markBoost;

    public FinishingTouch(AbstractCreature owner, int markDuration, float markBoost) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, markDuration);
        this.markBoost = markBoost;
        updateDescription();
    }

    boolean appliedThisTurn = false;
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.type == DamageInfo.DamageType.NORMAL && target != owner && target != adp() && !appliedThisTurn) {
            appliedThisTurn = true;
            applyToTarget(target, owner, new Mark(target, amount, markBoost));
        }
    }

    @Override
    public void atEndOfRound() {
        appliedThisTurn = false;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner instanceof Chesed) {
                    ((Chesed) owner).checkDisposalCanUse();
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void stackPower(int stackAmount) {
        // doesn't stack
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + (int) (markBoost * 100) + POWER_DESCRIPTIONS[2];
    }
}
