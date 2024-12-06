package ruina.monsters.act2.mountain;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.BetterSpriterAnimation;
import ruina.actions.UsePreBattleActionAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.CenterOfAttention;
import ruina.util.AdditionalIntent;
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

    public static final String ABSORPTION_POWER_ID = makeID("Absorption");
    public static final PowerStrings absorptionPowerStrings = CardCrawlGame.languagePack.getPowerStrings(ABSORPTION_POWER_ID);
    public static final String ABSORPTION_POWER_NAME = absorptionPowerStrings.NAME;
    public static final String[] ABSORPTION_POWER_DESCRIPTIONS = absorptionPowerStrings.DESCRIPTIONS;

    public static final String BODIES_POWER_ID = makeID("Bodies");
    public static final PowerStrings BODIESPowerStrings = CardCrawlGame.languagePack.getPowerStrings(BODIES_POWER_ID);
    public static final String BODIES_POWER_NAME = BODIESPowerStrings.NAME;
    public static final String[] BODIES_POWER_DESCRIPTIONS = BODIESPowerStrings.DESCRIPTIONS;

    public Mountain(final float x, final float y) {
        super(ID, ID, 100, -5.0F, 0, 330.0f, 285.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Mountain/Spriter/Mountain.scml"));
        setNumAdditionalMoves(2);
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
        playSound("Spawn", 0.7f);
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof MeltedCorpses) {
                target = (MeltedCorpses)mo;
            }
        }
        stagePower = new AbstractLambdaPower(ABSORPTION_POWER_NAME, ABSORPTION_POWER_ID, AbstractPower.PowerType.BUFF, false, this, currentStage) {
            @Override
            public void updateDescription() {
                if (amount == STAGE1) {
                    description = ABSORPTION_POWER_DESCRIPTIONS[2] + (amount + 1) + ABSORPTION_POWER_DESCRIPTIONS[3];
                } else if (amount == STAGE2) {
                    description = ABSORPTION_POWER_DESCRIPTIONS[0] + (amount - 1) + ABSORPTION_POWER_DESCRIPTIONS[1] + " " + ABSORPTION_POWER_DESCRIPTIONS[2] + (amount + 1) + ABSORPTION_POWER_DESCRIPTIONS[3];
                } else {
                    description = ABSORPTION_POWER_DESCRIPTIONS[0] + (amount - 1) + ABSORPTION_POWER_DESCRIPTIONS[1];
                }
            }
            @Override
            public void atEndOfRound() {
                if (owner.currentHealth == owner.maxHealth) {
                    if (owner instanceof Mountain) {
                        ((Mountain) owner).Grow();
                    }
                }
            }
        };
        applyToTarget(this, this, stagePower);
        applyToTarget(this, this, new AbstractLambdaPower(BODIES_POWER_NAME, BODIES_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = BODIES_POWER_DESCRIPTIONS[0];
            }
            @Override
            public void atEndOfRound() {
                boolean foundCorpse = false;
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof MeltedCorpses) {
                        foundCorpse = true;
                        break;
                    }
                }
                if (!foundCorpse) {
                    Summon();
                }
            }
        });
        applyToTarget(this, this, new CenterOfAttention(this));
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
        } else if (this.currentStage == STAGE1) {
            setMoveShortcut(DEVOUR);
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
            setMoveShortcut(move);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(RAM)) {
                possibilities.add(RAM);
            }
            if (!this.lastTwoMoves(BITE)) {
                possibilities.add(BITE);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
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
        if (currentStage == STAGE1 && !target.isDeadOrEscaped()) {
            attackingMonsterWithPrimaryIntent = true;
        } else {
            attackingMonsterWithPrimaryIntent = false;
        }
        super.applyPowers();
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (currentStage == STAGE3) {
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
                setMoveShortcut(REVIVE);
                this.createIntent();
                atb(new SetMoveAction(this, REVIVE, Intent.NONE));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(ABSORPTION_POWER_ID)) && !(power.ID.equals(BODIES_POWER_ID)) && !power.ID.equals(CenterOfAttention.POWER_ID)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }
            additionalIntents.clear();
            additionalMoves.clear();
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

    private void Summon() {
        playSound("Spawn", 0.7f);
        target = new MeltedCorpses(MINION_X, 0.0f);
        atb(new SpawnMonsterAction(target, true));
        atb(new UsePreBattleActionAction(target));
    }

    private void Grow() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        if (currentStage < STAGE3) {
            currentStage++;
            numAdditionalMoves++;
            animationAction("Idle" + currentStage, "Grow", 0.7f, this);
            updateValues();
            rollMove();
            createIntent();
        }
    }

    private void Shrink() {
        if (currentStage > STAGE1) {
            currentStage--;
            if (currentStage == STAGE1) {
                AbstractDungeon.getCurrRoom().cannotLose = false;
            }
            if (numAdditionalMoves > 0) {
                numAdditionalMoves--;
            }
            animationAction("Idle" + currentStage, "Shrink", 0.7f, this);
            updateValues();
        }
    }

    private void updateValues() {
        setMaxHP();
        stagePower.amount = currentStage;
        stagePower.updateDescription();
    }

    private void setMaxHP() {
        if (currentStage == STAGE1) {
            this.maxHealth = STAGE1_HP;
        }
        if (currentStage == STAGE2) {
            this.maxHealth = STAGE2_HP;
        }
        if (currentStage == STAGE3) {
            this.maxHealth = STAGE3_HP;
        }
        healthBarUpdatedEvent();
    }

}