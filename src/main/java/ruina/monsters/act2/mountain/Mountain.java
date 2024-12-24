package ruina.monsters.act2.mountain;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.CenterOfAttention;
import ruina.powers.act2.Absorption;
import ruina.powers.act2.Bodies;
import ruina.powers.multiplayer.MultiplayerEnemyBuff;
import ruina.util.AdditionalIntent;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Mountain extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Mountain.class.getSimpleName());

    public static final float MINION_X = -480.0F;
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

    private final int STAGE1_HP;
    private final int STAGE2_HP;
    private final int STAGE3_HP;
    private final int INITIAL_HP = calcAscensionTankiness(125);

    public static final int STAGE3 = 3;
    public static final int STAGE2 = 2;
    public static final int STAGE1 = 1;
    private static final float REVIVE_PERCENT = 0.50f;
    private static final float STARTING_PERCENT = 0.50f;
    private AbstractPower stagePower;

    public Mountain(final float x, final float y) {
        super(ID, ID, 100, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mountain/Spriter/Mountain.scml"));
        setNumAdditionalMoves(2);
        this.setHp(INITIAL_HP);
        STAGE3_HP = this.maxHealth;
        STAGE2_HP = Math.round(this.maxHealth * 0.8f);
        STAGE1_HP = Math.round(this.maxHealth * 0.4f);
        this.currentHealth = (int)(this.maxHealth * STARTING_PERCENT);
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
        playSound("Spawn", 0.7f);
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof MeltedCorpses) {
                target = (MeltedCorpses)mo;
            }
        }
        stagePower = new Absorption(this, convertPhaseToStage());
        applyToTarget(this, this, stagePower);
        applyToTarget(this, this, new Bodies(this));
        applyToTarget(this, this, new CenterOfAttention(this));
        if (RuinaMod.isMultiplayerConnected()) {
            applyToTarget(this, this, new MultiplayerEnemyBuff(this));

            updateValues();
            updateNumAdditionalMoves();
            updateCannotLose();
            runAnim("Idle" + convertPhaseToStage());
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        if (this.halfDead && move.nextMove != REVIVE) {
            return;
        }
        super.takeCustomTurn(move, target, whichMove);
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
                Shrink();
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
        animationAction("Attack" + convertPhaseToStage(), "Bite", enemy, this);
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
                runAnim("Idle" + convertPhaseToStage());
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.halfDead) {
            setMoveShortcut(REVIVE);
        } else if (convertPhaseToStage() == STAGE1) {
            setMoveShortcut(DEVOUR);
        } else if (convertPhaseToStage() == STAGE2) {
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
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(RAM)) {
                possibilities.add(RAM);
            }
            if (!this.lastTwoMoves(BITE)) {
                possibilities.add(BITE);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (this.halfDead) {
            return;
        }
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            if (convertPhaseToStage() == STAGE2) {
                setAdditionalMoveShortcut(DEVOUR, moveHistory);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastTwoMoves(RAM, moveHistory)) {
                    possibilities.add(RAM);
                }
                if (!this.lastTwoMoves(BITE, moveHistory)) {
                    possibilities.add(BITE);
                }
                byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
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
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case DEVOUR: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.LIFESTEAL);
                detailsList.add(detail);
                break;
            }
            case BITE: {
                DetailedIntent detail = new DetailedIntent(this, ATTACK_DEBUFF_AMT, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case HORRID_SCREECH: {
                DetailedIntent detail = new DetailedIntent(this, DAZES, DetailedIntent.DAZED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
            case VOMIT: {
                DetailedIntent detail = new DetailedIntent(this, NORMAL_DEBUFF_AMT, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, SLIMES, DetailedIntent.SLIMED_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case REVIVE: {
                DetailedIntent detail = new DetailedIntent(this, 1, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void applyPowers() {
        if (convertPhaseToStage() == STAGE1 && !target.isDeadOrEscaped()) {
            attackingMonsterWithPrimaryIntent = true;
        } else {
            attackingMonsterWithPrimaryIntent = false;
        }
        super.applyPowers();
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (convertPhaseToStage() == STAGE3) {
            if (index == 1 && additionalIntent.baseDamage >= 0) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
            } else {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
            }
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, target, target.icon, index);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead && AbstractDungeon.getCurrRoom().cannotLose) {
            this.halfDead = true;
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            if (this.nextMove != REVIVE) {
                rollMove();
                createIntent();
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(Absorption.POWER_ID)) && !(power.ID.equals(Bodies.POWER_ID)) && !power.ID.equals(CenterOfAttention.POWER_ID) && !power.ID.equals(MultiplayerEnemyBuff.POWER_ID)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die();
            for (AbstractMonster mo : monsterList()) {
                if (mo instanceof MeltedCorpses) {
                    atb(new SuicideAction(mo));
                }
            }
        }
        if (this.maxHealth <= 0) {
            setMaxHP();
            AbstractDungeon.actionManager.addToBottom(new InstantKillAction(this));
        }
    }

    public void Summon() {
        playSound("Spawn", 0.7f);
        target = new MeltedCorpses(MINION_X, 0.0f);
        atb(new SpawnMonsterAction(target, true));
        atb(new UsePreBattleActionAction(target));
    }

    public void Grow() {
        if (convertPhaseToStage() < STAGE3) {
            setPhase(phase - 1);
            updateNumAdditionalMoves();
            updateCannotLose();
            animationAction("Idle" + convertPhaseToStage(), "Grow", 0.7f, this);
            updateValues();
            rollMove();
            createIntent();
        }
    }

    public void Shrink() {
        if (convertPhaseToStage() > STAGE1) {
            setPhase(phase + 1);
            updateNumAdditionalMoves();
            updateCannotLose();
            animationAction("Idle" + convertPhaseToStage(), "Shrink", 0.7f, this);
            updateValues();
        }
    }

    private void updateNumAdditionalMoves() {
        this.numAdditionalMoves = convertPhaseToStage() - 1;
    }

    private void updateCannotLose() {
        if (convertPhaseToStage() == STAGE1) {
            AbstractDungeon.getCurrRoom().cannotLose = false;
        } else {
            AbstractDungeon.getCurrRoom().cannotLose = true;
        }
    }

    private void updateValues() {
        setMaxHP();
        stagePower.amount = convertPhaseToStage();
        stagePower.updateDescription();
    }

    private void setMaxHP() {
        if (convertPhaseToStage() == STAGE1) {
            this.maxHealth = STAGE1_HP;
        }
        if (convertPhaseToStage() == STAGE2) {
            this.maxHealth = STAGE2_HP;
        }
        if (convertPhaseToStage() == STAGE3) {
            this.maxHealth = STAGE3_HP;
        }
        healthBarUpdatedEvent();
    }

    private int convertPhaseToStage() {
        if (phase == 1) {
            return STAGE3;
        } else if (phase == 3) {
            return STAGE1;
        } else {
            return STAGE2;
        }
    }

}