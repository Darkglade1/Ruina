package ruina.monsters.day49;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.act2.MeltedCorpses;
import ruina.powers.AbstractLambdaPower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class AngelaD49 extends AbstractCardMonster
{
    public static final String ID = makeID(AngelaD49.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public enum PHASE {
        WRISTCUTTER,
        ASPIRATION,
        PINOCCHIO,
        SNOWQUEEN,
        SILENTGIRL
    }
    public PHASE currentPhase = PHASE.WRISTCUTTER;


    private static final byte DEVOUR = 0;
    private static final byte BITE = 1;
    private static final byte HORRID_SCREECH = 2;
    private static final byte RAM = 3;
    private static final byte VOMIT = 4;
    private static final byte REVIVE = 5;

    private final int NORMAL_DEBUFF_AMT = calcAscensionSpecial(1);
    private final int ATTACK_DEBUFF_AMT = calcAscensionSpecial(1);
    private final int DAZES = calcAscensionSpecial(3);
    private final int SLIMES = calcAscensionSpecial(1);

    private final int STAGE1_HP = calcAscensionTankiness(50);
    private final int STAGE2_HP = calcAscensionTankiness(100);
    private final int STAGE3_HP = calcAscensionTankiness(125);

    public static final int STAGE3 = 3;
    public static final int STAGE2 = 2;
    public static final int STAGE1 = 1;
    public int currentStage = STAGE3;
    private static final float REVIVE_PERCENT = 0.50f;
    private static final float STARTING_PERCENT = 0.50f;
    private AbstractLambdaPower stagePower;

    public AngelaD49() {
        this(-100.0f, 0.0f);
    }
    public AngelaD49(final float x, final float y) {
        super(NAME, ID, 100, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mountain/Spriter/Mountain.scml"));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 2;
        maxAdditionalMoves = 2;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(STAGE3_HP);
        this.currentHealth = (int)(STAGE3_HP * STARTING_PERCENT);
        updateHealthBar();
        runAnim("Idle3");

        addMove(DEVOUR, Intent.ATTACK_BUFF, calcAscensionDamage(14));
        addMove(BITE, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
        addMove(HORRID_SCREECH, Intent.DEBUFF);
        addMove(RAM, Intent.ATTACK, calcAscensionDamage(16));
        addMove(VOMIT, Intent.STRONG_DEBUFF);
        addMove(REVIVE, Intent.NONE);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        AbstractDungeon.getCurrRoom().cannotLose = true;
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        if (this.halfDead && move.nextMove != REVIVE) {
            return;
        }
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }
        if (target.isDeadOrEscaped()) {
            target = adp();
        }
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case DEVOUR: {
                attackAnimation(target);
                atb(new VampireDamageActionButItCanFizzle(target, info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case BITE: {
                attackAnimation(target);
                dmg(target, info);
                resetIdle();
                applyToTarget(target, this, new WeakPower(target, ATTACK_DEBUFF_AMT, true));
                break;
            }
            case HORRID_SCREECH: {
                screechAnimation();
                intoDiscardMo(new Dazed(), DAZES, this);
                resetIdle(1.0f);
                break;
            }
            case RAM: {
                ramAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case VOMIT: {
                vomitAnimation();
                applyToTarget(target, this, new FrailPower(target, NORMAL_DEBUFF_AMT, true));
                intoDiscardMo(new Slimed(), SLIMES, this);
                resetIdle(1.0f);
                break;
            }
            case REVIVE: {
                atb(new HealAction(this, this, (int)(this.maxHealth * REVIVE_PERCENT)));
                this.halfDead = false;
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onSpawnMonster(this);
                }
                break;
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack" + currentStage, "Bite", enemy, this);
    }

    private void screechAnimation() {
        animationAction("Screech", "Screech", 0.5f, this);
    }

    private void ramAnimation(AbstractCreature enemy) {
        animationAction("Ram", "Ram", enemy, this);
    }

    private void vomitAnimation() {
        animationAction("Vomit", "Vomit", 0.5f, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + currentStage);
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        AbstractMonster mo = this;
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            if (!mo.halfDead) {
                atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
                atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            }
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
       atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.halfDead) {
            setMoveShortcut(REVIVE);
        } else if (this.currentStage == STAGE1) {
            setMoveShortcut(DEVOUR, MOVES[DEVOUR]);
        } else if (this.currentStage == STAGE2) {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(DEVOUR)) {
                possibilities.add(DEVOUR);
            }
            if (!this.lastMove(BITE)) {
                possibilities.add(BITE);
            }
            if (!this.lastMove(HORRID_SCREECH) && !this.lastMoveBefore(HORRID_SCREECH)) {
                possibilities.add(HORRID_SCREECH);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(RAM)) {
                possibilities.add(RAM);
            }
            if (!this.lastTwoMoves(BITE)) {
                possibilities.add(BITE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (this.halfDead) {
            return;
        }
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            if (this.currentStage == STAGE2) {
                setAdditionalMoveShortcut(DEVOUR, moveHistory);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastTwoMoves(RAM, moveHistory)) {
                    possibilities.add(RAM);
                }
                if (!this.lastTwoMoves(BITE, moveHistory)) {
                    possibilities.add(BITE);
                }
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setAdditionalMoveShortcut(move, moveHistory);
            }
        } else {
            if (this.lastMove(DEVOUR, moveHistory) || this.lastMove(RAM, moveHistory)) {
                setAdditionalMoveShortcut(VOMIT, moveHistory);
            } else {
                if (AbstractDungeon.ascensionLevel >= 18) {
                    setAdditionalMoveShortcut(DEVOUR, moveHistory);
                } else {
                    setAdditionalMoveShortcut(RAM, moveHistory);
                }
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) { additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) { applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null); }
        }
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
            if (this.nextMove != REVIVE) {
                setMoveShortcut(REVIVE);
                this.createIntent();
                atb(new SetMoveAction(this, REVIVE, Intent.NONE));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) { powersToRemove.add(power); }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
            additionalIntents.clear();
            additionalMoves.clear();
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) { super.die(); }

    }

}