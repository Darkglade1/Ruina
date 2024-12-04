package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class LittleRed extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(LittleRed.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BEAST_HUNT = 0;
    private static final byte CATCH_BREATH = 1;
    private static final byte HOLLOW_POINT_SHELL = 2;
    private static final byte BULLET_SHOWER = 3;

    private final int DEFENSE = calcAscensionTankiness(10);
    private final int STRENGTH = 3;
    public boolean enraged = false;

    public NightmareWolf wolf;

    public static final String STRIKE_POWER_ID = RuinaMod.makeID("StrikeWithoutHesitation");
    public static final PowerStrings strikePowerStrings = CardCrawlGame.languagePack.getPowerStrings(STRIKE_POWER_ID);
    public static final String STRIKE_POWER_NAME = strikePowerStrings.NAME;
    public static final String[] STRIKE_POWER_DESCRIPTIONS = strikePowerStrings.DESCRIPTIONS;

    public static final String FURY_POWER_ID = RuinaMod.makeID("FuryWithNoOutlet");
    public static final PowerStrings furyPowerStrings = CardCrawlGame.languagePack.getPowerStrings(FURY_POWER_ID);
    public static final String FURY_POWER_NAME = furyPowerStrings.NAME;
    public static final String[] FURY_POWER_DESCRIPTIONS = furyPowerStrings.DESCRIPTIONS;

    public LittleRed() {
        this(0.0f, 0.0f);
    }

    public LittleRed(final float x, final float y) {
        super(NAME, ID, 150, -5.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("LittleRed/Spriter/LittleRed.scml"));
        this.animation.setFlip(true, false);

        this.setHp(150);
        this.type = EnemyType.BOSS;

        addMove(BEAST_HUNT, Intent.ATTACK, calcAscensionDamage(9));
        addMove(CATCH_BREATH, Intent.BUFF);
        addMove(HOLLOW_POINT_SHELL, Intent.ATTACK, calcAscensionDamage(7), 2, true);
        addMove(BULLET_SHOWER, Intent.ATTACK, calcAscensionDamage(8), 3, true);

        this.allyIcon = makeUIPath("RedIcon.png");
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof NightmareWolf) {
                wolf = (NightmareWolf)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(FURY_POWER_NAME, FURY_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = FURY_POWER_DESCRIPTIONS[0];
            }
        });
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        if (this.firstMove && !enraged) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }
        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target;
        if (enraged) {
            target = AbstractDungeon.player;
        } else {
            target = wolf;
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case BEAST_HUNT: {
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case CATCH_BREATH: {
                atb(new AddTemporaryHPAction(this, this, DEFENSE));
                applyToTarget(this, this, new AbstractLambdaPower(STRIKE_POWER_NAME, STRIKE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {

                    boolean justApplied = true;

                    @Override
                    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                        if (info.owner == owner && damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
                            makePowerRemovable(this);
                            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
                        }
                    }

                    @Override
                    public void updateDescription() {
                        description = STRIKE_POWER_DESCRIPTIONS[0] + amount + STRIKE_POWER_DESCRIPTIONS[1];
                    }
                });
                break;
            }
            case HOLLOW_POINT_SHELL: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        shoot1Animation(target);
                    } else {
                        shoot2Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case BULLET_SHOWER: {
                for (int i = 0; i < multiplier; i++) {
                    if (i == 0) {
                        shoot1Animation(target);
                    } else if (i == 1){
                        shoot2Animation(target);
                    } else {
                        shoot3Animation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    public void enrage() {
        halfDead = false;
        enraged = true;
        isAlly = false;
        animation.setFlip(false, false);
        playSound("Rage", 2.0f);
        Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
        ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
        AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
        atb(new ShoutAction(this, DIALOG[1], 2.0F, 3.0F));
        atb(new VFXAction(this, new InflameEffect(this), 1.0F));
        applyToTarget(this, this, new StrengthPower(this, STRENGTH));
        atb(new HealAction(this, this, maxHealth));
    }

    @Override
    protected void getMove(final int num) {
        if (!enraged) {
            if (this.lastMove(HOLLOW_POINT_SHELL)) {
                setMoveShortcut(CATCH_BREATH);
            } else if (this.lastMove(CATCH_BREATH)) {
                setMoveShortcut(BEAST_HUNT);
            } else {
                setMoveShortcut(HOLLOW_POINT_SHELL);
            }
        } else {
            if (this.lastMove(BULLET_SHOWER)) {
                setMoveShortcut(HOLLOW_POINT_SHELL);
            } else {
                setMoveShortcut(BULLET_SHOWER);
            }
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || wolf.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        AbstractCreature target;
        if (!enraged) {
            target = wolf;
        } else {
            target = adp();
        }
        applyPowers(target);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!wolf.isDeadOrEscaped()) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.0f, DIALOG[3], false));
            wolf.onRedDeath();
        } else {
            onBossVictoryLogic();
        }
    }

    public void onKillWolf() {
        atb(new TalkAction(this, DIALOG[2]));
        atb(new VFXAction(new WaitEffect(), 1.0F));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                disappear();
                onBossVictoryLogic();
                this.isDone = true;
            }
        });
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "Slash", enemy, this);
    }

    private void shoot1Animation(AbstractCreature enemy) {
        animationAction("Shoot1", "Gun", enemy, this);
    }

    private void shoot2Animation(AbstractCreature enemy) {
        animationAction("Shoot2", "Gun", enemy, this);
    }

    private void shoot3Animation(AbstractCreature enemy) {
        animationAction("Shoot3", "Gun", enemy, this);
    }

}