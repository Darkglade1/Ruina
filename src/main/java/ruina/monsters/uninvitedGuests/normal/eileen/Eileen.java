package ruina.monsters.uninvitedGuests.normal.eileen;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Accelerate;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Preach;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.Propagate;
import ruina.monsters.uninvitedGuests.normal.eileen.eileenCards.ThoughtGearBrainwash;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act4.Gears;
import spireTogether.patches.combatsync.ActionPatches;
import spireTogether.util.helpers.GameplayHelpers;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Eileen extends AbstractCardMonster
{
    public static final String ID = makeID(Eileen.class.getSimpleName());

    public static final float MINION_X_1 = -175.0f;
    public static final float MINION_X_2 = 0F;
    private static final byte PREACH = 0;
    private static final byte ACCELERATE = 1;
    private static final byte PROPAGATE = 2;
    private static final byte BRAINWASH = 3;

    public final int BLOCK = calcAscensionTankiness(22);
    public final int STRENGTH = calcAscensionSpecial(4);
    public final int VULNERABLE = 1;
    public final int HP_LOSS = RuinaMod.getMultiplayerEnemyHealthScaling(100);

    public AbstractMonster[] minions = new AbstractMonster[2];

    public Eileen() {
        this(0.0f, 0.0f);
    }

    public Eileen(final float x, final float y) {
        super(ID, ID, 1000, -5.0F, 0, 160.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Eileen/Spriter/Eileen.scml"));
        setNumAdditionalMoves(1);
        numAdditionalMoves = 1;
        this.setHp(calcAscensionTankiness(1000));

        addMove(PREACH, Intent.BUFF);
        addMove(ACCELERATE, Intent.DEFEND);
        addMove(PROPAGATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BRAINWASH, Intent.ATTACK, calcAscensionDamage(17));

        cardList.add(new Preach(this));
        cardList.add(new Accelerate(this));
        cardList.add(new Propagate(this));
        cardList.add(new ThoughtGearBrainwash(this));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Yesod) {
                target = (Yesod)mo;
            }
        }
        int i = 0;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof GearsWorshipper && !mo.isDeadOrEscaped()) {
                minions[i] = mo;
                i++;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new Gears(this, HP_LOSS));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case PREACH: {
                buffAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        applyToTargetNextTurn(mo, new StrengthPower(this, STRENGTH));
                    }
                }
                resetIdle();
                break;
            }
            case ACCELERATE: {
                blockAnimation();
                for (AbstractMonster mo : monsterList()) {
                    if (!(mo instanceof AbstractAllyMonster)) {
                        block(mo, BLOCK);
                    }
                }
                resetIdle();
                break;
            }
            case PROPAGATE: {
                rangeAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                break;
            }
            case BRAINWASH: {
                strongAttackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void strongAttackAnimation(AbstractCreature enemy) {
        animationAction("Attack2", "GearStrongAtk", enemy, this);
    }

    private void rangeAnimation(AbstractCreature enemy) {
        animationAction("Attack1", "GearFar", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Buff", "GearStrongStart", this);
    }

    private void blockAnimation() {
        animationAction("Block", "GearStrongStart", this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(PREACH)) {
            setMoveShortcut(ACCELERATE);
        } else {
            setMoveShortcut(PREACH);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (this.lastMove(PROPAGATE, moveHistory)) {
            setAdditionalMoveShortcut(BRAINWASH, moveHistory);
        } else {
            setAdditionalMoveShortcut(PROPAGATE, moveHistory);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof GearsWorshipper) {
                atb(new SuicideAction(mo));
            }
        }
        if (target instanceof Yesod) {
            ((Yesod) target).onBossDeath();
        }
    }

    public void Summon() {
        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster minion;
                if (i == 0) {
                    minion = new GearsWorshipper(MINION_X_1, 0.0f);
                } else {
                    minion = new GearsWorshipper(MINION_X_2, 0.0f);
                }
                atb(new SpawnMonsterAction(minion, true));
                atb(new UsePreBattleActionAction(minion));
                minions[i] = minion;
            }
        }
    }

    public void onMinionDeath() {
//        if (RuinaMod.isMultiplayerConnected() && GameplayHelpers.isPlayerTurn()) {
//            DamageAction damage = new DamageAction(this, new DamageInfo(adp(), HP_LOSS, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.NONE);
//            atb(damage);
//        } else {
//            atb(new LoseHPAction(this, this, HP_LOSS));
//        }
        atb(new LoseHPAction(this, this, HP_LOSS));
    }

}