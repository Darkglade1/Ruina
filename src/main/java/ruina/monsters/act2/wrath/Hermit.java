package ruina.monsters.act2.wrath;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Hermit extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Hermit.class.getSimpleName());

    public static final float MINION_X = -200.0F;
    private static final byte HOLD_STILL = 0;
    private static final byte MAKE_WAY = 1;
    private static final byte CRACKLE = 2;
    private static final byte HELLO = 3;

    private final int BLOCK = calcAscensionTankiness(11);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    public HermitStaff staff;

    public Hermit() {
        this(100.0f, 0.0f);
    }

    public Hermit(final float x, final float y) {
        super(ID, ID, 160, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hermit/Spriter/Hermit.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(190), calcAscensionTankiness(200));

        addMove(HOLD_STILL, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(MAKE_WAY, Intent.ATTACK, calcAscensionDamage(12));
        addMove(CRACKLE, Intent.DEFEND_BUFF);
        addMove(HELLO, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ServantOfWrath) {
                target = (ServantOfWrath)mo;
            }
            if (mo instanceof HermitStaff) {
                staff = (HermitStaff) mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case HOLD_STILL: {
                attack1Animation(target);
                dmg(target, info);
                resetIdle();
                if (target == adp()) {
                    applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                } else {
                    applyToTarget(target, this, new VulnerablePower(target, DEBUFF, true));
                }
                break;
            }
            case MAKE_WAY: {
                attack2Animation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case CRACKLE: {
                specialAnimation();
                buff();
                resetIdle(1.0f);
                break;
            }
            case HELLO: {
                specialAnimation();
                if (staff == null) {
                    Summon();
                } else {
                    buff(); //in case someone forces this enemy to use this intent twice in a row :)
                }
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void buff() {
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Hermit || mo instanceof HermitStaff) {
                block(mo, BLOCK);
                applyToTargetNextTurn(mo, new StrengthPower(this, STRENGTH));
            }
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "HermitAtk", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "HermitStrongAtk", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "HermitWand", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (staff == null && !firstMove) {
            setMoveShortcut(HELLO);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(HOLD_STILL)) {
                possibilities.add(HOLD_STILL);
            }
            if (!this.lastMove(MAKE_WAY)) {
                possibilities.add(MAKE_WAY);
            }
            if (!this.lastMove(CRACKLE) && !this.lastMoveBefore(CRACKLE)) {
                possibilities.add(CRACKLE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(HOLD_STILL, moveHistory)) {
            possibilities.add(HOLD_STILL);
        }
        if (!this.lastTwoMoves(MAKE_WAY, moveHistory)) {
            possibilities.add(MAKE_WAY);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (target instanceof ServantOfWrath) {
            ((ServantOfWrath) target).onHermitDeath();
        }
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof HermitStaff) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void Summon() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                staff = new HermitStaff(MINION_X, 0.0f);
                att(new UsePreBattleActionAction(staff));
                att(new SpawnMonsterAction(staff, true));
                this.isDone = true;
            }
        });
    }

}