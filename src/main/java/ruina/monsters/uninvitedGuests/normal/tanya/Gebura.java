package ruina.monsters.uninvitedGuests.normal.tanya;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.uninvitedGuests.normal.tanya.geburaCards.*;
import ruina.powers.AbstractLambdaPower;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.monsters.eventboss.redMist.monster.RedMist.horizontalSplitVfx;
import static ruina.util.Wiz.*;

public class Gebura extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Gebura.class.getSimpleName());

    private static final byte UPSTANDING_SLASH = 0;
    private static final byte LEVEL_SLASH = 1;
    private static final byte SPEAR = 2;
    private static final byte GSV = 3;
    private static final byte GSH = 4;

    public final int STRENGTH = 2;
    public final int VULNERABLE = 1;

    public final int upstanding_damage = 7;
    public final int upstanding_threshold = upstanding_damage;
    public final int upstandingHits = 2;
    public final int spearHits = 3;
    public final float spearMultiplier = 2.0f;
    public final int level_damage = 6;
    public final int level_threshold = level_damage;
    public final int levelHits = 2;
    
    public final int GREATER_SPLIT_COOLDOWN = 3;
    public int greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN;

    public final int powerStrength = 1;
    public final int EGOtimer = 4;
    protected boolean manifestedEGO = false;
    protected int phase = 1;
    public boolean canSplit = true;

    public static final String POWER_ID = makeID("GeburaRedMist");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Gebura() {
        this(0.0f, 0.0f);
    }

    public Gebura(final float x, final float y) {
        super(ID, ID, 200, -5.0F, 0, 200.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gebura/Spriter/RedMist.scml"));
        this.animation.setFlip(true, false);
        this.setHp(200);

        addMove(UPSTANDING_SLASH, Intent.ATTACK_DEBUFF, upstanding_damage, upstandingHits);
        addMove(LEVEL_SLASH, Intent.ATTACK_BUFF, level_damage, levelHits);
        addMove(SPEAR, Intent.ATTACK, 4, spearHits);
        addMove(GSV, Intent.ATTACK_DEBUFF, 30);
        addMove(GSH, Intent.ATTACK_DEBUFF, 40);

        cardList.add(new Ally_UpstandingSlash(this));
        cardList.add(new Ally_LevelSlash(this));
        cardList.add(new Ally_Spear(this));
        cardList.add(new Ally_GreaterSplitVertical(this));
        cardList.add(new Ally_GreaterSplitHorizontal(this));

        this.icon = TexLoader.getTexture(makeUIPath("GeburaIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Tanya) {
                target = (Tanya)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, powerStrength) {
            @Override
            public void onInitialApplication() {
                amount2 = EGOtimer;
                updateDescription();
            }

            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL && target != owner) {
                    applyToTarget(owner, owner, new StrengthPower(owner, amount));
                }
            }

            @Override
            public void atEndOfRound() {
                if (!manifestedEGO) {
                    amount2--;
                    if (amount2 <= 0) {
                        manifestEGO();
                    }
                    updateDescription();
                }
            }

            @Override
            public void updateDescription() {
                if (amount2 > 0) {
                    description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1] + POWER_DESCRIPTIONS[2] + amount2 + POWER_DESCRIPTIONS[3];
                } else {
                    description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                }
            }
        });
        super.usePreBattleAction();
    }

    protected void manifestEGO() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                playSound("RedMistChange");
                CustomDungeon.playTempMusicInstantly("RedMistBGM");
                this.isDone = true;
            }
        });
        manifestedEGO = true;
        phase = 2;
        resetIdle(0.0f);
        AbstractPower strength = getPower(StrengthPower.POWER_ID);
        if (strength != null) {
            applyToTarget(this, this, new StrengthPower(this, strength.amount));
        }
    }

    public void dialogue() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
    }

    @Override
    public void takeTurn() {
        dialogue();
        super.takeTurn();

        AbstractCreature enemy = target;

        final int[] threshold = {0};
        switch (this.nextMove) {
            case UPSTANDING_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        upstandingAnimation(enemy);
                    } else {
                        levelAnimation(enemy);
                    }
                    dmg(enemy, info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += enemy.lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if(threshold[0] >= upstanding_threshold){
                            applyToTargetTop(enemy, Gebura.this, new VulnerablePower(enemy, VULNERABLE, true));
                        }
                        isDone = true;
                    }
                });

                break;
            }
            case LEVEL_SLASH: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        levelAnimation(enemy);
                    } else {
                        upstandingAnimation(enemy);
                    }
                    dmg(enemy, info);
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            threshold[0] += enemy.lastDamageTaken;
                            isDone = true;
                        }
                    });
                    resetIdle();
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (threshold[0] >= level_threshold) {
                            applyToTargetTop(Gebura.this, Gebura.this, new StrengthPower(Gebura.this, STRENGTH));
                        }
                        isDone = true;
                    }
                });
                break;
            }
            case SPEAR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        spearAnimation(enemy);
                    } else {
                        upstandingAnimation(enemy);
                    }
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (enemy.currentBlock == 0) {
                                info.applyPowers(Gebura.this, enemy);
                                info.output *= spearMultiplier;
                            }
                            this.isDone = true;
                        }
                    });
                    dmg(enemy, info);
                    resetIdle();
                }
                break;
            }
            case GSV: {
                verticalUpAnimation(enemy);
                atb(new VFXAction(new WaitEffect(), 0.25f));
                RedMist.verticalSplitVfx();
                verticalDownAnimation(enemy);
                boolean hadGuts = enemy.hasPower(Tanya.POWER_ID);
                int previousBlock = enemy.currentBlock;
                dmg(enemy, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        boolean stillHasGuts = enemy.hasPower(Tanya.POWER_ID);
                        if (hadGuts != stillHasGuts) {
                            //because lastDamageTaken doesn't factor in overkill damage
                            //so a greater split that kills tanya would reduce less strength than expected
                            //so we have to handle it here
                            enemy.lastDamageTaken = info.output - previousBlock;
                        }
                        if (enemy.lastDamageTaken > 0) {
                            applyToTargetTop(enemy, Gebura.this, new StrengthPower(enemy, -enemy.lastDamageTaken));
                            if (!enemy.hasPower(ArtifactPower.POWER_ID)) {
                                applyToTargetTop(enemy, Gebura.this, new GainStrengthPower(enemy, enemy.lastDamageTaken));
                            }
                        }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case GSH: {
                horizontalSplitVfx();
                horizontalAnimation(enemy);
                boolean hadGuts = enemy.hasPower(Tanya.POWER_ID);
                int previousBlock = enemy.currentBlock;
                dmg(enemy, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        boolean stillHasGuts = enemy.hasPower(Tanya.POWER_ID);
                        if (hadGuts != stillHasGuts) {
                            //because lastDamageTaken doesn't factor in overkill damage
                            //so a greater split that kills tanya would reduce less strength than expected
                            //so we have to handle it here
                            enemy.lastDamageTaken = info.output - previousBlock;
                        }
                        if (enemy.lastDamageTaken > 0) {
                            applyToTargetTop(enemy, Gebura.this, new StrengthPower(enemy, -enemy.lastDamageTaken));
                            if (!enemy.hasPower(ArtifactPower.POWER_ID)) {
                                applyToTargetTop(enemy, Gebura.this, new GainStrengthPower(enemy, enemy.lastDamageTaken));
                            }
                        }
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        greaterSplitCooldownCounter = GREATER_SPLIT_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                greaterSplitCooldownCounter--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    private void upstandingAnimation(AbstractCreature enemy) {
        animationAction("Upstanding" + phase, "RedMistVert" + phase, enemy, this);
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Spear" + phase, "RedMistStab" + phase, enemy, this);
    }

    private void levelAnimation(AbstractCreature enemy) {
        animationAction("Level" + phase, "RedMistHori" + phase, enemy, this);
    }

    private void verticalUpAnimation(AbstractCreature enemy) {
        animationAction("VerticalUp" + phase, "RedMistVertHit", enemy, this);
    }

    private void verticalDownAnimation(AbstractCreature enemy) {
        animationAction("VerticalDown" + phase, "RedMistVertFin", enemy, this);
    }

    private void horizontalAnimation(AbstractCreature enemy) {
        animationAction("Horizontal", "RedMistHoriFin", enemy, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (greaterSplitCooldownCounter <= 0 && canSplit) {
            if (phase == 1) {
                setMoveShortcut(GSV, MOVES[GSV], cardList.get(GSV));
            } else {
                setMoveShortcut(GSH, MOVES[GSH], cardList.get(GSH));
            }
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(UPSTANDING_SLASH) && !this.lastMoveBefore(UPSTANDING_SLASH)) {
                possibilities.add(UPSTANDING_SLASH);
            }
            if (!this.lastMove(SPEAR) && !this.lastMoveBefore(SPEAR)) {
                possibilities.add(SPEAR);
            }
            if (!this.lastMove(LEVEL_SLASH) && !this.lastMoveBefore(LEVEL_SLASH)) {
                possibilities.add(LEVEL_SLASH);
            }
            if (possibilities.isEmpty()) {
                possibilities.add(UPSTANDING_SLASH);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move));
        }
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

}