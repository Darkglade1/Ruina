package ruina.monsters.theHead;

import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class GeburaHead extends Gebura {

    public GeburaHead() {
        this(0.0f, 0.0f);
    }

    public GeburaHead(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Zena) {
                enemyBoss = mo;
                ((Zena)enemyBoss).gebura = this;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPower power = getPower(POWER_ID);
                if (power != null) {
                    if (power instanceof TwoAmountPower) {
                        ((TwoAmountPower) power).amount2++;
                        power.updateDescription(); //stop her power from ticking down too early
                    }
                }
                this.isDone = true;
            }
        });
    }

    public void onEntry() {
        animationAction("Upstanding" + phase, "GeburaArrive", null, this);
        waitAnimation(1.0f);
    }

    public void dialogue() {
    }

    protected void manifestEGO() {
        playSound("RedMistChange");
        manifestedEGO = true;
        phase = 2;
        resetIdle(0.0f);
        AbstractPower strength = getPower(StrengthPower.POWER_ID);
        if (strength != null) {
            applyToTarget(this, this, new StrengthPower(this, strength.amount));
        }
    }

}
