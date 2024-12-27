package ruina.powers.multiplayer;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.roadHome.RoadHome;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.*;

public class EasilyDistractedMultiplayer extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(EasilyDistractedMultiplayer.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int threshold;

    public EasilyDistractedMultiplayer(AbstractCreature owner, int threshold, int amount) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        this.threshold = threshold;
        setPowerImage("EasilyDistracted");
        updateDescription();
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
            applyToTarget(owner, adp(), new EasilyDistractedMultiplayer(adp(), threshold, 1));
        }
        return damageAmount;
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        if (this.amount >= threshold) {
            this.amount = 0;
            trigger();
        }
    }

    private void trigger() {
        this.flash();
        att(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner instanceof RoadHome) {
                    if (!owner.hasPower(StunMonsterPower.POWER_ID)) {
                        if (((RoadHome) owner).additionalIntents.size() > 0) {
                            ((RoadHome) owner).additionalIntents.remove(0);
                            ((RoadHome) owner).additionalMoves.remove(0);
                        } else {
                            ((RoadHome) owner).setMoveShortcut(RoadHome.NONE);
                            ((RoadHome) owner).createIntent();
                        }
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void atEndOfRound() {
        amount = 0;
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0] + threshold + POWER_DESCRIPTIONS[1];
    }
}
