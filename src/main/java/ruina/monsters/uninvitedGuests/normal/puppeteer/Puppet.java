package ruina.monsters.uninvitedGuests.normal.puppeteer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyAttackingMinion;
import ruina.powers.RuinaPlatedArmor;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act4.PuppetStrings;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Puppet extends AbstractAllyAttackingMinion
{
    public static final String ID = makeID(Puppet.class.getSimpleName());

    public static final byte FORCEFUL_GESTURE = 0;
    public static final byte REPRESSED_FLESH = 1;
    public static final byte REVIVING = 2;
    public static final byte REVIVE = 3;

    private final int BLOCK = calcAscensionTankiness(12);
    private final int PLATED_ARMOR = calcAscensionSpecial(11);
    public final int maxHPIncrease = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(20));

    public Puppet(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 250.0f, 395.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppet/Spriter/Puppet.scml"));
        setHp(calcAscensionTankiness(90), calcAscensionTankiness(98));
        addMove(FORCEFUL_GESTURE, Intent.ATTACK_DEFEND, calcAscensionDamage(15));
        addMove(REPRESSED_FLESH, Intent.ATTACK, calcAscensionDamage(9), 2);
        addMove(REVIVING, Intent.UNKNOWN);
        addMove(REVIVE, Intent.BUFF);

        attackingAlly = AbstractDungeon.monsterRng.randomBoolean();
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Chesed) {
                target = (Chesed) mo;
            }
        }
        addPower(new MinionPower(this));
        block(this, PLATED_ARMOR);
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new RuinaPlatedArmor(this, PLATED_ARMOR));
        applyToTarget(this, this, new PuppetStrings(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RemoveAllBlockAction(this, this));

        AbstractCreature target;
        if (!this.target.isDead && !this.target.isDying && attackingAlly) {
            target = this.target;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case FORCEFUL_GESTURE: {
                bluntAnimation(target);
                block(this, BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case REPRESSED_FLESH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case REVIVING: {
                break;
            }
            case REVIVE: {
                atb(new HealAction(this, this, this.maxHealth));
                atb(new IncreaseMaxHpAction(this, ((float)maxHPIncrease / 100), true));
                this.halfDead = false;
                applyToTarget(this, this, new RuinaPlatedArmor(this, PLATED_ARMOR));
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onSpawnMonster(this);
                }
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                attackingAlly = !attackingAlly;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
        }
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(REVIVING)) {
            setMoveShortcut(REVIVE);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(FORCEFUL_GESTURE)) {
                possibilities.add(FORCEFUL_GESTURE);
            }
            if (!this.lastTwoMoves(REPRESSED_FLESH)) {
                possibilities.add(REPRESSED_FLESH);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            if (this.nextMove != REVIVING) {
                setMoveShortcut(REVIVING);
                this.createIntent();
                atb(new SetMoveAction(this, REVIVING, Intent.UNKNOWN));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(MinionPower.POWER_ID)) && !(power.ID.equals(StrengthPower.POWER_ID)) && !(power.ID.equals(GainStrengthPower.POWER_ID)) && !(power.ID.equals(PuppetStrings.POWER_ID)) && !(power.ID.equals(PlatedArmorPower.POWER_ID)) && !(power.ID.equals(BarricadePower.POWER_ID))) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

}