package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
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
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;
import java.util.Iterator;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Mountain extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Mountain.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

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
    private AbstractMonster corpse;
    private Texture targetTexture = TexLoader.getTexture(makeUIPath("CorpseIcon.png"));

    public static final String ABSORPTION_POWER_ID = makeID("Absorption");
    public static final PowerStrings absorptionPowerStrings = CardCrawlGame.languagePack.getPowerStrings(ABSORPTION_POWER_ID);
    public static final String ABSORPTION_POWER_NAME = absorptionPowerStrings.NAME;
    public static final String[] ABSORPTION_POWER_DESCRIPTIONS = absorptionPowerStrings.DESCRIPTIONS;

    public static final String BODIES_POWER_ID = makeID("Bodies");
    public static final PowerStrings BODIESPowerStrings = CardCrawlGame.languagePack.getPowerStrings(BODIES_POWER_ID);
    public static final String BODIES_POWER_NAME = BODIESPowerStrings.NAME;
    public static final String[] BODIES_POWER_DESCRIPTIONS = BODIESPowerStrings.DESCRIPTIONS;

    public Mountain() {
        this(-100.0f, 0.0f);
    }

    public Mountain(final float x, final float y) {
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
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        Summon();
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
    protected void resetIdle(float duration) {
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
        if (currentStage == STAGE1) {
            takeCustomTurn(this.moves.get(nextMove), corpse);
        } else {
            takeCustomTurn(this.moves.get(nextMove), adp());
        }
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            if (!mo.halfDead) {
                atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
                atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            }
            if (additionalIntent.targetTexture != null) {
                takeCustomTurn(additionalMove, corpse);
            } else {
                takeCustomTurn(additionalMove, adp());
            }
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
        if (currentStage == STAGE1 && !corpse.isDeadOrEscaped()) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = corpse;
            if (info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = (PowerTip) ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                int multiplier = moves.get(this.nextMove).multiplier;
                if (multiplier > 0) {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[3] + multiplier + TEXT[4];
                } else {
                    intentTip.body = TEXT[0] + FontHelper.colorString(target.name, "y") + TEXT[1] + info.output + TEXT[2];
                }
                Texture attackImg = getAttackIntent(info.output);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        }
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                if (currentStage == STAGE3) {
                    if (i == 1 && additionalIntent.baseDamage >= 0) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, corpse, makeUIPath("CorpseIcon.png"));
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, corpse, makeUIPath("CorpseIcon.png"));
                }
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            Iterator var2 = this.powers.iterator();
            while (var2.hasNext()) {
                AbstractPower p = (AbstractPower) var2.next();
                p.onDeath();
            }

            var2 = AbstractDungeon.player.relics.iterator();

            while (var2.hasNext()) {
                AbstractRelic r = (AbstractRelic) var2.next();
                r.onMonsterDeath(this);
            }
            if (this.nextMove != REVIVE) {
                setMoveShortcut(REVIVE);
                this.createIntent();
                atb(new SetMoveAction(this, REVIVE, Intent.NONE));
            }
            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power.ID.equals(ABSORPTION_POWER_ID)) && !(power.ID.equals(BODIES_POWER_ID))) {
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
        float xPosition = -480.0F;
        corpse = new MeltedCorpses(xPosition, 0.0f);
        atb(new SpawnMonsterAction(corpse, true));
        atb(new UsePreBattleActionAction(corpse));
    }

    private void Grow() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        if (currentStage < STAGE3) {
            currentStage++;
            if (numAdditionalMoves < maxAdditionalMoves) {
                numAdditionalMoves++;
            }
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

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (currentStage == STAGE1 && targetTexture != null && !corpse.isDeadOrEscaped()) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

}