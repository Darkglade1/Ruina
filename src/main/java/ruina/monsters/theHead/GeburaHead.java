package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.monsters.uninvitedGuests.normal.tanya.Gebura;
import ruina.powers.act5.GeburaProwess;
import ruina.vfx.WaitEffect;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class GeburaHead extends Gebura {

    private boolean usedPreBattleAction = false;

    public GeburaHead(final float x, final float y) {
        super(x, y);
        this.setHp(calcAscensionTankiness(350));
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
            applyToTarget(this, this, new GeburaProwess(this, 10, 10));
            super.usePreBattleAction();
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Zena) {
                    target = (Zena)mo;
                    ((Zena) target).gebura = this;
                    target.target = this;
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
    }

    public void onEntry() {
        animationAction("Upstanding" + phase, "GeburaArrive", null, this);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CustomDungeon.playTempMusicInstantly("Gebura3");
                isDone = true;
            }
        });
        waitAnimation(1.0f);
    }

    public void dialogue() {
    }

    protected void manifestEGO() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistChange");
                this.isDone = true;
            }
        });
        manifestedEGO = true;
        phase = 2;
        resetIdle(0.0f);
        AbstractPower strength = getPower(StrengthPower.POWER_ID);
        if (strength != null) {
            applyToTarget(this, this, new StrengthPower(this, strength.amount));
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        renderTargetIcon(sb);
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[2]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

}
