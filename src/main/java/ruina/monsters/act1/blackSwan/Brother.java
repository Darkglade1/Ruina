package ruina.monsters.act1.blackSwan;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.BrotherPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Brother extends AbstractRuinaMonster
{
    public static final String ID = makeID(Brother.class.getSimpleName());

    private static final byte GREEN_WASTE = 0;
    private static final byte NONE = 1;

    private final int PLATED_ARMOR = RuinaMod.getMultiplayerEnemyHealthScaling(2);
    private final int STATUS = 1;
    private final int ARTIFACT = 1;
    private final int VULNERABLE = 1;

    private final int brotherNum;
    public final int ACTIVE_PHASE = 2;
    private BlackSwan parent;

    public Brother() {
        this(0.0f, 0.0f, 1);
    }

    public Brother(final float x, final float y, int brotherNum) {
        super(ID, ID, 22, 0.0F, 0, 80.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Brother/Spriter/Brother.scml"));
        int baseHP = 20 - ((brotherNum - 1) * 3);
        setHp(calcAscensionTankiness(baseHP));
        addMove(GREEN_WASTE, Intent.ATTACK, calcAscensionSpecial(2));
        addMove(NONE, Intent.NONE);
        this.brotherNum = brotherNum;
        this.name = DIALOG[brotherNum - 1];
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof BlackSwan) {
                parent = (BlackSwan) mo;
            }
        }
        addPower(new MinionPower(this));
        int brotherPowerAmount = 0;
        switch (brotherNum) {
            case 1: {
                brotherPowerAmount = PLATED_ARMOR;
                break;
            }
            case 2: {
                brotherPowerAmount = ARTIFACT;
                break;
            }
            case 3: {
                brotherPowerAmount = STATUS;
                break;
            }
            case 4: {
                brotherPowerAmount = VULNERABLE;
                break;
            }
        }
        applyToTarget(this, this, new BrotherPower(this, brotherPowerAmount, brotherNum, parent));
        if (brotherNum == 1) {
            setPhase(ACTIVE_PHASE);
        }
        if (brotherNum > 1 && this.phase == DEFAULT_PHASE) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    hideHealthBar();
                    halfDead = true;
                    currentHealth = 0;
                    healthBarUpdatedEvent();
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case GREEN_WASTE: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (phase == DEFAULT_PHASE && brotherNum > 1) {
            setMoveShortcut(NONE);
        } else {
            setMoveShortcut(GREEN_WASTE);
        }
    }

    public void revive() {
        showHealthBar();
        setPhase(ACTIVE_PHASE);
        atb(new HealAction(this, this, maxHealth));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("SwanRevive");
                halfDead = false;
                rollMove();
                createIntent();
                this.isDone = true;
            }
        });
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntHori", enemy, this);
    }

}