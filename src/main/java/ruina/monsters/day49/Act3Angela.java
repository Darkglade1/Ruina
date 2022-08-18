package ruina.monsters.day49;

import basemod.ReflectionHacks;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.*;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.act3.Pinocchio;
import ruina.monsters.day49.angelaCards.marionette.MarionetteAngela;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.function.BiFunction;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Act3Angela extends AbstractCardMonster
{
    public static final String ID = makeID(Act3Angela.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String POWER_ID = makeID("Mirroring");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public final int STRENGTH = 3;

    public static final String ALT_POWER_ID = makeID("FibleFable");
    public static final PowerStrings altpowerStrings = CardCrawlGame.languagePack.getPowerStrings(ALT_POWER_ID);
    public static final String ALT_POWER_NAME = altpowerStrings.NAME;
    public static final String[] ALT_POWER_DESCRIPTIONS = altpowerStrings.DESCRIPTIONS;

    public static final String MAXHP_POWER_ID = makeID("BrokenStrings");
    public static final PowerStrings maxHPpowerStrings = CardCrawlGame.languagePack.getPowerStrings(MAXHP_POWER_ID);
    public static final String MAXHP_POWER_NAME = maxHPpowerStrings.NAME;
    public static final String[] MAXHP_POWER_DESCRIPTIONS = maxHPpowerStrings.DESCRIPTIONS;

    private static final byte MARIONETTE = 0;
    public int marionetteDamage = 30;
    public final int marionetteBaseDamage = 30;

    public static final String LINE = RuinaMod.makeMonsterPath("Day49/Marionette/Line.png");
    private static final Texture LINE_TEXTURE = new Texture(LINE);

    public Act3Angela() {
        this(185.0f, 0.0f);
    }
    public Act3Angela(final float x, final float y) {
        super(NAME, ID, 600, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SnowQueen/Spriter/SnowQueen.scml"));
        this.type = EnemyType.ELITE;
        this.setHp(4000);
        addMove(MARIONETTE, Intent.ATTACK, marionetteDamage);
        firstMove = true;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction()
    {
        specialAnimation(this);
        marionetteEffect();
        resetIdle();
        atb(new ApplyPowerAction(this, this, new Refracting(this, -1)));
        Summon();
        applyToTarget(this, this, new AbstractLambdaPower(ALT_POWER_NAME, ALT_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void atEndOfRound() {
                this.flash();
                atb(new HealAction(Act3Angela.this, Act3Angela.this, Act3Angela.this.maxHealth));
            }

            @Override
            public void updateDescription() {
                description = ALT_POWER_DESCRIPTIONS[0];
            }
        });
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
            @Override
            public void atEndOfRound() {
                this.flash();
                applyToTarget(owner, owner, new StrengthPower(owner, amount));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, marionetteDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if(info.base > -1) { info.applyPowers(this, target); }
        switch (move.nextMove) {
            case MARIONETTE: {
                specialAnimation(adp());
                marionetteEffect();
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "SnowBlizzard", enemy, this);
    }


    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle");
                this.isDone = true;
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                marionetteDamage = marionetteBaseDamage;
                isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(MARIONETTE, MOVES[MARIONETTE], new MarionetteAngela(this));
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == MARIONETTE) {
            DamageInfo info = new DamageInfo(this, marionetteDamage, DamageInfo.DamageType.NORMAL);
            applyPowersOnlyIncrease(adp(), info);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
            Texture attackImg = getAttackIntent(info.output * 1);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentImg", attackImg);
            updateCard();
        } else {
            super.applyPowers();
        }
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }


    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth > 0 && !this.halfDead) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS){
                        marionetteDamage = Act3Angela.this.lastDamageTaken;
                        setMove(MARIONETTE, Intent.ATTACK, marionetteDamage);
                        createIntent();
                        updateCard();
                    }
                    isDone = true;
                }
            });
        }
    }

    public void Summon() {
        float xPos_Farthest_L = -550.0F;
        float xPos_Short_L = -200F;
        AbstractMonster puppet1 = new Pinocchio(xPos_Farthest_L, 0.0f);
        puppet1.maxHealth = 2000;
        puppet1.currentHealth = 2000;
        atb(new SpawnMonsterAction(puppet1, true));
        atb(new UsePreBattleActionAction(puppet1));
        applyToTarget(puppet1, puppet1, new AbstractLambdaPower(MAXHP_POWER_NAME, MAXHP_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onDeath() {
                this.flash();
                Act3Angela.this.onMinionDeath(puppet1.maxHealth);
            }
            @Override
            public void updateDescription() {
                description = MAXHP_POWER_DESCRIPTIONS[0];
            }
        });
        puppet1.rollMove();
        puppet1.createIntent();
        ((Pinocchio) puppet1).blockAnimation();
        ((Pinocchio) puppet1).resetIdle();
        AbstractMonster puppet2 = new Pinocchio(xPos_Short_L, 0.0f);
        puppet2.maxHealth = 2000;
        puppet2.currentHealth = 2000;
        atb(new SpawnMonsterAction(puppet2, true));
        atb(new UsePreBattleActionAction(puppet2));
        applyToTarget(puppet2, puppet2, new AbstractLambdaPower(MAXHP_POWER_NAME, MAXHP_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onDeath() {
                this.flash();
                Act3Angela.this.onMinionDeath(puppet2.maxHealth);
            }
            @Override
            public void updateDescription() {
                description = MAXHP_POWER_DESCRIPTIONS[0];
            }
        });
        puppet2.rollMove();
        puppet2.createIntent();
        ((Pinocchio) puppet2).blockAnimation();
        ((Pinocchio) puppet2).resetIdle();
    }

    private void marionetteEffect() {
        float chargeDuration = 1.5f;
        float scale = 1.5f;
        float startY = 0.0f;
        int numStrings = 20;

        VfxBuilder effect = new VfxBuilder(LINE_TEXTURE, 0.0f, -9999, 0.0f)
                .scale(0.0f, scale, VfxBuilder.Interpolations.LINEAR)
                .setY(startY)
                .setAngle((float)AbstractDungeon.monsterRng.random(45, 135));
        for (int i = 1; i < numStrings; i++) {
            int finalI = i;
            effect.triggerVfxAt(0.0f, 1,new BiFunction<Float, Float, AbstractGameEffect>() {
                @Override
                public AbstractGameEffect apply(Float aFloat, Float aFloat2) {
                    return new VfxBuilder(LINE_TEXTURE, (float) Settings.WIDTH / numStrings * finalI, -9999, chargeDuration)
                            .scale(0.0f, scale, VfxBuilder.Interpolations.LINEAR)
                            .setY(startY)
                            .setAngle((float)AbstractDungeon.monsterRng.random(45, 135)).build();
                }
            });
        }
        AbstractGameEffect finalEffect = effect.build();
        atb(new VFXAction(finalEffect, chargeDuration));
    }

    public void onMinionDeath(int maxHP) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if(Act3Angela.this.maxHealth == maxHP){ att(new LoseHPAction(Act3Angela.this, Act3Angela.this, maxHP)); }
                else { Act3Angela.this.increaseMaxHp(-maxHP, true); }
                isDone = true;
            }
        });
    }
}