package ruina.monsters.act1.blackSwan;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.BrotherPower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Brother extends AbstractRuinaMonster
{
    public static final String ID = makeID(Brother.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte GREEN_WASTE = 0;
    private static final byte NONE = 1;

    private final int PLATED_ARMOR = calcAscensionSpecial(2);
    private final int STATUS = calcAscensionSpecial(1);
    private final int ARTIFACT = calcAscensionSpecial(1);
    private final int VULNERABLE = 1;

    private final int brotherNum;
    private BlackSwan parent;

    public Brother() {
        this(0.0f, 0.0f, 1);
    }

    public Brother(final float x, final float y, int brotherNum) {
        super(NAME, ID, 22, 0.0F, 0, 80.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Brother/Spriter/Brother.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(GREEN_WASTE, Intent.ATTACK, calcAscensionSpecial(2));
        addMove(NONE, Intent.NONE);
        this.brotherNum = brotherNum;
        this.name = DIALOG[brotherNum - 1];
        if (brotherNum == 1) {
            firstMove = false;
        }
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
        this.powers.add(new MinionPower(this));
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
        this.powers.add(new BrotherPower(this, brotherPowerAmount, brotherNum, parent));
        if (brotherNum > 1) {
            hideHealthBar();
            halfDead = true;
            currentHealth = 0;
            healthBarUpdatedEvent();
        }
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

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
        if (halfDead || firstMove) {
            setMoveShortcut(NONE);
        } else {
            setMoveShortcut(GREEN_WASTE, MOVES[GREEN_WASTE]);
        }
    }

    public void revive() {
        showHealthBar();
        atb(new HealAction(this, this, maxHealth));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("SwanRevive");
                halfDead = false;
                firstMove = false;
                rollMove();
                createIntent();
                this.isDone = true;
            }
        });

    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (parent != null) {
            parent.onBrotherDeath();
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntHori", enemy, this);
    }

}