package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class LittleRed extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(LittleRed.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(RuinaMod.makeID("AllyStrings"));
    private static final String[] TEXT = uiStrings.TEXT;

    private static final byte BEAST_HUNT = 0;
    private static final byte CATCH_BREATH = 1;
    private static final byte HOLLOW_POINT_SHELL = 2;
    private static final byte BULLET_SHOWER = 3;

    private final int DEFENSE = calcAscensionTankiness(10);
    private final int STRENGTH = 2;
    private boolean enraged = false;

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
        this.type = EnemyType.BOSS;

        this.setHp(calcAscensionTankiness(this.maxHealth));

        addMove(BEAST_HUNT, Intent.ATTACK, calcAscensionDamage(9));
        addMove(CATCH_BREATH, Intent.BUFF);
        addMove(HOLLOW_POINT_SHELL, Intent.ATTACK, calcAscensionDamage(7), 2, true);
        addMove(BULLET_SHOWER, Intent.ATTACK, calcAscensionDamage(8), 3, true);
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning2");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof NightmareWolf) {
                wolf = (NightmareWolf)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(FURY_POWER_NAME, AbstractPower.PowerType.BUFF, false, this, -1) {
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
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0]));
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
                //runAnim("Spark");
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case CATCH_BREATH: {
                //runAnim("Spark");
                atb(new AddTemporaryHPAction(this, this, DEFENSE));
                applyToTarget(this, this, new AbstractLambdaPower(STRIKE_POWER_NAME, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {

                    boolean justApplied = true;

                    @Override
                    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                        System.out.println(damageAmount);
                        if (info.owner == owner && damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
                            System.out.println("am I here???");
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
                        }
                    }

                    @Override
                    public void atEndOfRound() {
                        if (justApplied) {
                            justApplied = false;
                        } else {
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
                //runAnim("Smack");
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
            case BULLET_SHOWER: {
                //runAnim("Special");
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                }
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    public void enrage() {
        halfDead = false;
        enraged = true;
        isAlly = false;
        animation.setFlip(false, false);
        AbstractDungeon.actionManager.addToBottom(new ShoutAction(this, DIALOG[1], 2.0F, 3.0F));
        applyToTarget(this, this, new StrengthPower(this, STRENGTH));
        atb(new HealAction(this, this, maxHealth));
    }

    @Override
    protected void getMove(final int num) {
        if (!enraged) {
            if (this.lastMove(HOLLOW_POINT_SHELL)) {
                setMoveShortcut(CATCH_BREATH, MOVES[CATCH_BREATH]);
            } else if (this.lastMove(CATCH_BREATH)) {
                setMoveShortcut(BEAST_HUNT, MOVES[BEAST_HUNT]);
            } else {
                setMoveShortcut(HOLLOW_POINT_SHELL, MOVES[HOLLOW_POINT_SHELL]);
            }
        } else {
            if (this.lastMove(BULLET_SHOWER)) {
                setMoveShortcut(HOLLOW_POINT_SHELL, MOVES[HOLLOW_POINT_SHELL]);
            } else {
                setMoveShortcut(BULLET_SHOWER, MOVES[BULLET_SHOWER]);
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
        DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        if (target == wolf) {
            if(info.base > -1) {
                info.applyPowers(this, target);
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
                PowerTip intentTip = (PowerTip)ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentTip");
                Texture attackImg;
                if (moves.get(this.nextMove).multiplier > 0) {
                    intentTip.body = TEXT[7] + info.output + TEXT[8] + moves.get(this.nextMove).multiplier + TEXT[9];
                    attackImg = getAttackIntent(info.output * moves.get(this.nextMove).multiplier);
                } else {
                    intentTip.body = TEXT[5] + info.output + TEXT[6];
                    attackImg = getAttackIntent(info.output);
                }
                ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            }
        } else {
            super.applyPowers();
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!wolf.isDeadOrEscaped()) {
            wolf.onRedDeath();
        } else {
            onBossVictoryLogic();
        }
    }

    public void onKillWolf() {
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[2]));
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new WaitEffect(), 1.0F));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                wolf.red.isDead = true;
                onBossVictoryLogic();
                this.isDone = true;
            }
        });
    }

}