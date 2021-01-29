package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Oblivion;
import ruina.powers.Paralysis;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Ozma extends AbstractRuinaMonster
{
    public static final String ID = makeID(Ozma.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final float MINION_1_X_POSITION = -300.0f;
    private static final float MINION_2_X_POSITION = -550.0f;

    private static final byte FADING_MEMORIES = 0;
    private static final byte POWDER_OF_LIFE = 1;
    private static final byte HINDER = 2;
    private static final byte SQUASH = 3;

    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(11);
    private final int DRAW_DEBUFF = 1;

    private final int DRAW_DEBUFF_COOLDOWN = 3;
    private final int MAX_DRAW_DEBUFF = calcAscensionSpecial(2);
    private int cooldown = 0;

    public Ozma() {
        this(0.0f, 0.0f);
    }

    public Ozma(final float x, final float y) {
        super(NAME, ID, 300, -5.0F, 0, 250.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Ozma/Spriter/Ozma.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(FADING_MEMORIES, Intent.STRONG_DEBUFF);
        addMove(POWDER_OF_LIFE, Intent.DEFEND_BUFF);
        addMove(HINDER, Intent.ATTACK_DEBUFF, calcAscensionDamage(13));
        addMove(SQUASH, Intent.ATTACK, calcAscensionDamage(19));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland2");
        AbstractMonster jack1 = new Jack(MINION_1_X_POSITION, 0.0f, true);
        AbstractMonster jack2 = new Jack(MINION_2_X_POSITION, 0.0f, false);
        atb(new SpawnMonsterAction(jack1, true));
        atb(new UsePreBattleActionAction(jack1));
        atb(new SpawnMonsterAction(jack2, true));
        atb(new UsePreBattleActionAction(jack2));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (this.firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case FADING_MEMORIES: {
                debuffAnimation();
                applyToTarget(adp(), this, new Oblivion(adp(), DRAW_DEBUFF));
                resetIdle(1.0f);
                cooldown = DRAW_DEBUFF_COOLDOWN + 1;
                break;
            }
            case POWDER_OF_LIFE: {
                buffAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
            case HINDER: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle(1.0f);
                break;
            }
            case SQUASH: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
        cooldown--;
    }

    @Override
    protected void getMove(final int num) {
        int drawDebuffAmt = 0;
        AbstractPower oblivion = adp().getPower(Oblivion.POWER_ID);
        if (oblivion != null) {
            drawDebuffAmt = oblivion.amount;
        }
        if (cooldown <= 0 && drawDebuffAmt < MAX_DRAW_DEBUFF) {
            setMoveShortcut(FADING_MEMORIES, MOVES[FADING_MEMORIES]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(SQUASH)) {
                possibilities.add(SQUASH);
            }
            if (!this.lastMove(HINDER)) {
                possibilities.add(HINDER);
            }
            if (!this.lastMove(POWDER_OF_LIFE) && !this.lastMove(POWDER_OF_LIFE)) {
                possibilities.add(POWDER_OF_LIFE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Jack) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Slam", "GreedSlam", enemy, this);
    }

    private void buffAnimation() {
        animationAction("Raise", "OzmaGuard", this);
    }

    private void debuffAnimation() {
        animationAction("Thud", "OzmaFin", this);
    }

}