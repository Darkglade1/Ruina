package ruina.monsters.act2.knight;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act2.Despair;
import ruina.util.DetailedIntent;
import spireTogether.patches.combatsync.ActionPatches;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class KnightOfDespair extends AbstractRuinaMonster
{
    public static final String ID = makeID(KnightOfDespair.class.getSimpleName());

    public static final float MINION_X = -360.0F;

    private static final byte DESPAIR = 0;

    public final int DESPAIR_LOSS = RuinaMod.getMultiplayerEnemyHealthScaling(40);
    private final int STRENGTH = calcAscensionSpecial(5);
    private static final int MAX_STABS = 3;
    private int stabCount = 0;

    private Sword sword;

    public KnightOfDespair() {
        this(0.0f, 0.0f);
    }

    public KnightOfDespair(final float x, final float y) {
        super(ID, ID, 160, -5.0F, 0, 250.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Knight/Spriter/Knight.scml"));
        setHp(calcAscensionTankiness(160));
        addMove(DESPAIR, Intent.UNKNOWN);
    }

    @Override
    public void usePreBattleAction() {
        playSound("KnightChange");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Sword) {
                sword = (Sword) mo;
            }
        }
        applyToTarget(this, this, new Despair(this, DESPAIR_LOSS));
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case DESPAIR: {
                if (sword == null || sword.isDeadOrEscaped()) {
                    Summon();
                } else {
                    specialAnimation();
                    applyToTarget(sword, this, new StrengthPower(sword, STRENGTH));
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(DESPAIR);
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case DESPAIR: {
                if (!firstMove && (sword == null || sword.isDeadOrEscaped())) {
                    DetailedIntent detail = new DetailedIntent(this, DetailedIntent.SUMMON);
                    detailsList.add(detail);
                } else {
                    DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                    detailsList.add(detail);
                }
                break;
            }
        }
        return detailsList;
    }

    public void Summon() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                sword = new Sword(MINION_X, 0.0f, false);
                att(new UsePreBattleActionAction(sword));
                att(new SpawnMonsterAction(sword, true));
                this.isDone = true;
            }
        });
    }

    public void onSwordDeath(boolean wasFullBlocked) {
        int hpLoss = DESPAIR_LOSS;
        if (hasPower(Despair.POWER_ID)) {
            hpLoss = getPower(Despair.POWER_ID).amount;
        }
        DamageAction damage = new DamageAction(this, new DamageInfo(adp(), hpLoss, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.NONE);
//        if (wasFullBlocked && RuinaMod.isMultiplayerConnected()) {
//            ActionPatches.markActionForNoDamageSync(damage);
//            ActionPatches.markActionForNoDataSync(damage);
//        }
        atb(damage);
        stabCount++;
        if (stabCount > MAX_STABS) {
            stabCount = MAX_STABS;
        }
        animationAction("Idle" + stabCount, "KnightAttack", this);
        setDetailedIntents(); // update detailed intent if needed
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Sword) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void specialAnimation() {
        animationAction("Idle" + stabCount, "KnightGaho", this);
    }

}