package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
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

    private static final byte FADING_MEMORIES = 0;
    private static final byte POWDER_OF_LIFE = 1;
    private static final byte HINDER = 2;
    private static final byte SQUASH = 3;

    private final int STRENGTH = calcAscensionSpecial(4);
    private final int PARALYSIS = calcAscensionSpecial(2);
    private final int BLOCK = calcAscensionTankiness(12);
    private final int DRAW_DEBUFF = 1;

    private final int DRAW_DEBUFF_COOLDOWN = 3;
    private final int MAX_DRAW_DEBUFF = calcAscensionSpecial(2);
    private int cooldown = 0;

    public static final String POWER_ID = makeID("Agony");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Ozma() {
        this(0.0f, 0.0f);
    }

    public Ozma(final float x, final float y) {
        super(NAME, ID, 500, -5.0F, 0, 250.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Ozma/Spriter/Ozma.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(500));
        addMove(FADING_MEMORIES, Intent.STRONG_DEBUFF);
        addMove(POWDER_OF_LIFE, Intent.DEFEND_BUFF);
        addMove(HINDER, Intent.ATTACK_DEBUFF, calcAscensionDamage(17));
        addMove(SQUASH, Intent.ATTACK, calcAscensionDamage(22));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland2");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
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
                if (AbstractDungeon.ascensionLevel >= 19) {
                    applyToTarget(adp(), this, new DrawReductionPower(adp(), 1));
                }
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        cooldown = DRAW_DEBUFF_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
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
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                cooldown--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        int drawDebuffAmt = 0;
        AbstractPower oblivion = adp().getPower(Oblivion.POWER_ID);
        if (oblivion != null) {
            drawDebuffAmt = oblivion.amount;
        }
        if (cooldown <= 0 && drawDebuffAmt < MAX_DRAW_DEBUFF) {
            setMoveShortcut(FADING_MEMORIES);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(SQUASH)) {
                possibilities.add(SQUASH);
            }
            if (!this.lastMove(HINDER)) {
                possibilities.add(HINDER);
            }
            if (!this.lastMove(POWDER_OF_LIFE) && !this.lastMoveBefore(POWDER_OF_LIFE)) {
                possibilities.add(POWDER_OF_LIFE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
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
        onBossVictoryLogic();
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