package ruina.monsters.theHead;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.monsters.uninvitedGuests.normal.argalia.monster.Roland;
import ruina.powers.Bleed;

import static ruina.util.Wiz.*;

public class RolandHead extends Roland {

    public RolandHead() {
        this(0.0f, 0.0f);
    }

    public RolandHead(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Baral) { enemyBoss = mo; }
        }
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        DamageInfo info;
        int multiplier = 0;
        if (moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else { info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL); }
        AbstractCreature target;
        target = enemyBoss;
        if (info.base > -1) { info.applyPowers(this, target); }
        switch (this.nextMove) {
            case CRYSTAL: {
                block(this, crystalBlock);
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        slashAnimation(target);
                    } else {
                        sword1Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case WHEELS: {
                wheelsAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new StrengthPower(target, -wheelsStrDown));
                resetIdle(1.0f);
                break;
            }
            case DURANDAL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        sword2Animation(target);
                    } else {
                        sword3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, durandalStrength));
                break;
            }
            case ALLAS: {
                pierceAnimation(target);
                dmg(target, info);
                resetIdle();
                applyToTarget(target, this, new WeakPower(target, ALLAS_DEBUFF, false));
                break;
            }
            case GUN: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        gun1Animation(target);
                    } else if (i == 1) {
                        gun2Animation(target);
                    } else {
                        gun3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case MOOK: {
                mook1Animation(target);
                waitAnimation(0.25f);
                mook2Animation(target);
                dmg(target, info);
                resetIdle(1.0f);
                applyToTarget(target, this, new VulnerablePower(target, MOOK_DEBUFF, true));
                break;
            }
            case OLD_BOY: {
                attackAnimation(target);
                block(this, OLD_BOY_BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case RANGA: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        claw1Animation(target);
                    } else if (i == 1) {
                        claw2Animation(target);
                    } else {
                        knifeAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(target, this, new Bleed(target, RANGA_DEBUFF));
                break;
            }
            case MACE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        club1Animation(target);
                    } else {
                        club2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case FURIOSO: {
                float initialX = drawX;
                float targetBehind = target.drawX + 150.0f * Settings.scale;
                float targetFront = target.drawX - 200.0f * Settings.scale;
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getCurrRoom().cannotLose = true;
                        this.isDone = true;
                    }
                });
                gun1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                gun2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetBehind, target);
                pierceAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(true, target);
                attackAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetFront, target);
                knifeAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(false, target);
                mook1Animation(target);
                waitAnimation(0.15f, target);
                mook2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetBehind, target);
                claw1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                moveAnimation(targetFront, target);
                setFlipAnimation(true, target);
                claw2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                setFlipAnimation(false, target);
                club1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                club2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                wheelsAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                slashAnimation(target);
                dmg(target, info);
                waitAnimation(target);
                gun3Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword1Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword2Animation(target);
                dmg(target, info);
                waitAnimation(target);
                sword3Animation(target);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractDungeon.getCurrRoom().cannotLose = false;
                        this.isDone = true;
                    }
                });
                dmg(target, info);
                resetIdle();
                moveAnimation(initialX, null);
                setFlipAnimation(false, null);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        furiosoCount = furiosoCap + 1;
                        isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                furiosoCount--;
                updatePower();
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

}
