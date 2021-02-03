package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.Bleed;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Hermit extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Hermit.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte HOLD_STILL = 0;
    private static final byte MAKE_WAY = 1;
    private static final byte CRACKLE = 2;
    private static final byte HELLO = 3;

    private final int BLOCK = calcAscensionTankiness(11);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    public ServantOfWrath wrath;
    public HermitStaff staff;

    public Hermit() {
        this(100.0f, 0.0f);
    }

    public Hermit(final float x, final float y) {
        super(NAME, ID, 160, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hermit/Spriter/Hermit.scml"));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(180), calcAscensionTankiness(190));

        addMove(HOLD_STILL, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(MAKE_WAY, Intent.ATTACK, calcAscensionDamage(11));
        addMove(CRACKLE, Intent.DEFEND_BUFF);
        addMove(HELLO, Intent.UNKNOWN);
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ServantOfWrath) {
                wrath = (ServantOfWrath)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        Summon();
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
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
        if (this.firstMove) {
            firstMove = false;
        }
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (EnemyMoveInfo additionalMove : additionalMoves) {
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new IntentFlashAction(this));
            if (wrath.isDead || wrath.isDying) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, wrath);
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
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
            setMoveShortcut(move, MOVES[move]);
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
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, wrath, wrath.allyIcon);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        wrath.onHermitDeath();
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof HermitStaff) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void Summon() {
        float xPosition = -200.0F;
        staff = new HermitStaff(xPosition, 0.0f, this);
        atb(new SpawnMonsterAction(staff, true));
        atb(new UsePreBattleActionAction(staff));
    }

}