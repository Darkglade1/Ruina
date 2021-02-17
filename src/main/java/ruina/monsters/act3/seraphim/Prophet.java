package ruina.monsters.act3.seraphim;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Prophet extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID("ParadiseLost");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final Texture EGG = new Texture(makeMonsterPath("Seraphim/WhiteNightEgg.png"));
    private TextureRegion EGG_REGION;

    private static final byte BAPTISM = 0;

    private static final int HEAL = 5;
    private static final int STR = 2;
    private static final int DEX = 2;
    private static final int DRAW = 1;
    private static final int ENERGY = 1;

    private static final int TURN_THRESHOLD = 4;
    private int turnCounter = TURN_THRESHOLD;
    private int wingCounter = 1;
    
    private int apostleKillCounter = 0;
    private int numBuff1 = 0;
    private int numBuff2 = 0;
    private int numBuff3 = 0;
    private int numBuff4 = 0;

    public AbstractMonster[] minions = new AbstractMonster[3];

    public static final String APOSTLES_POWER_ID = RuinaMod.makeID("Apostles");
    public static final PowerStrings apostlePowerStrings = CardCrawlGame.languagePack.getPowerStrings(APOSTLES_POWER_ID);
    public static final String APOSTLE_POWER_NAME = apostlePowerStrings.NAME;
    public static final String[] APOSTLE_POWER_DESCRIPTIONS = apostlePowerStrings.DESCRIPTIONS;

    public static final String ADVENT_POWER_ID = RuinaMod.makeID("Advent");
    public static final PowerStrings adventPowerStrings = CardCrawlGame.languagePack.getPowerStrings(ADVENT_POWER_ID);
    public static final String advent_POWER_NAME = adventPowerStrings.NAME;
    public static final String[] advent_POWER_DESCRIPTIONS = adventPowerStrings.DESCRIPTIONS;

    public static final String BLESSING_POWER_ID = RuinaMod.makeID("Blessing");
    public static final PowerStrings BLESSINGPowerStrings = CardCrawlGame.languagePack.getPowerStrings(BLESSING_POWER_ID);
    public static final String BLESSING_POWER_NAME = BLESSINGPowerStrings.NAME;
    public static final String[] BLESSING_POWER_DESCRIPTIONS = BLESSINGPowerStrings.DESCRIPTIONS;

    public Prophet() {
        this(-1200.0f, 0.0f);
    }

    public Prophet(final float x, final float y) {
        super(NAME, ID, 100, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Seraphim/Spriter/Seraphim.scml"));
        this.animation.setFlip(true, false);
        runAnim("Idle1");
        addMove(BAPTISM, Intent.BUFF);
        EGG_REGION = new TextureRegion(EGG);
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning3");
        applyToTarget(this, this, new AbstractLambdaPower(APOSTLE_POWER_NAME, APOSTLES_POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void atEndOfRound() {
                if (turnCounter > 1) {
                    Summon();
                }
            }

            @Override
            public void updateDescription() {
                description = APOSTLE_POWER_DESCRIPTIONS[0] + STR + APOSTLE_POWER_DESCRIPTIONS[1] + DEX + APOSTLE_POWER_DESCRIPTIONS[2] + DRAW + APOSTLE_POWER_DESCRIPTIONS[3];
            }
        });
        applyToTarget(this, this, new AbstractLambdaPower(advent_POWER_NAME, ADVENT_POWER_ID, AbstractPower.PowerType.BUFF, true, this, turnCounter) {
            @Override
            public void atEndOfRound() {
                turnCounter--;
                amount = turnCounter;
                wingCounter++;
                if (wingCounter > 3) {
                    wingCounter = 3;
                }
                IdlePose();
                updateDescription();
                if (this.amount <= 0) {
                    for (AbstractMonster mo : monsterList()) {
                        if (mo instanceof ScytheApostle || mo instanceof SpearApostle) {
                            mo.currentHealth = 0;
                            mo.loseBlock();
                            mo.isDead = true;
                            mo.isDying = true;
                            mo.healthBarUpdatedEvent();
                        }
                    }
                    AbstractMonster seraphim = new Seraphim();
                    atb(new SpawnMonsterAction(seraphim, false));
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            owner.currentHealth = 0;
                            owner.loseBlock();
                            owner.isDead = true;
                            owner.isDying = true;
                            owner.healthBarUpdatedEvent();
                            this.isDone = true;
                        }
                    });
                    atb(new UsePreBattleActionAction(seraphim));
                    seraphim.rollMove();
                    seraphim.createIntent();
                }
            }
            @Override
            public void updateDescription() {
                if (amount == 1) {
                    description = advent_POWER_DESCRIPTIONS[0] + amount + advent_POWER_DESCRIPTIONS[2];
                } else {
                    description = advent_POWER_DESCRIPTIONS[0] + amount + advent_POWER_DESCRIPTIONS[1];
                }
            }
        });
        Summon();
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        switch (this.nextMove) {
            case BAPTISM: {
                healAnimation();
                atb(new HealAction(adp(), this, HEAL));
                if (numBuff1 > 0) {
                    for (int i = 0; i < numBuff1; i++) {
                        applyToTarget(adp(), this, new StrengthPower(adp(), STR));
                    }
                    numBuff1 = 0;
                }
                if (numBuff2 > 0) {
                    for (int i = 0; i < numBuff2; i++) {
                        applyToTarget(adp(), this, new DexterityPower(adp(), DEX));
                    }
                    numBuff2 = 0;
                }
                if (numBuff3 > 0) {
                    for (int i = 0; i < numBuff3; i++) {
                        applyToTarget(adp(), this, new DrawPower(adp(), DRAW));
                    }
                    numBuff3 = 0;
                }
                if (numBuff4 > 0) {
                    for (int i = 0; i < numBuff4; i++) {
                        applyToTarget(adp(), this, new AbstractLambdaPower(BLESSING_POWER_NAME, BLESSING_POWER_ID, AbstractPower.PowerType.BUFF, false, adp(), ENERGY) {
                            @Override
                            public void atStartOfTurn() {
                                this.flash();
                                atb(new GainEnergyAction(this.amount));
                            }

                            @Override
                            public void updateDescription() {
                                StringBuilder sb = new StringBuilder();
                                sb.append(BLESSING_POWER_DESCRIPTIONS[0]);
                                for(int i = 0; i < this.amount; ++i) {
                                    sb.append("[E] ");
                                }
                                sb.append(LocalizedStrings.PERIOD);
                                this.description = sb.toString();
                            }
                        });
                    }
                    numBuff4 = 0;
                }
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }
    
    public void incrementApostleKills() {
        apostleKillCounter++;
        AbstractPower apostlePower = this.getPower(APOSTLES_POWER_ID);
        if (apostlePower != null) {
            apostlePower.amount = apostleKillCounter;
        }
        if (apostleKillCounter == 1 || apostleKillCounter == 5 || apostleKillCounter == 9) {
            numBuff1++;
        }
        if (apostleKillCounter == 2 || apostleKillCounter == 6 || apostleKillCounter == 10) {
            numBuff2++;
        }
        if (apostleKillCounter == 3 || apostleKillCounter == 7 || apostleKillCounter == 11) {
            numBuff3++;
        }
        if (apostleKillCounter == 4 || apostleKillCounter == 8 || apostleKillCounter == 12) {
            numBuff4++;
        }
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(BAPTISM, MOVES[BAPTISM]);
    }

    @Override
    public void applyPowers() {
        applyPowers(this);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!isDead) {
            sb.setColor(Color.WHITE);
            sb.draw(EGG_REGION, (float)Settings.WIDTH / 2 - (float)this.EGG_REGION.getRegionWidth() / 2, (float)Settings.HEIGHT / 2, 0.0F, 0.0F, this.EGG_REGION.getRegionWidth(), this.EGG_REGION.getRegionHeight(), Settings.scale, Settings.scale, 0.0F);
        }
    }

    @Override
    protected void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                IdlePose();
                this.isDone = true;
            }
        });
    }

    private void IdlePose() {
        runAnim("Idle" + wingCounter);
    }

    public void Summon() {
        float xPos_Farthest_L = -450.0F;
        float xPos_Middle_L = -150F;
        float xPos_Short_L = 150F;

        for (int i = 0; i < minions.length; i++) {
            if (minions[i] == null) {
                AbstractMonster apostle;
                if (i == 0) {
                    apostle = new ScytheApostle(xPos_Farthest_L, 0.0f, this, AbstractDungeon.monsterRng.randomBoolean() ? 1 : 3);
                } else if (i == 1) {
                    apostle = new SpearApostle(xPos_Middle_L, 0.0f, this, AbstractDungeon.monsterRng.randomBoolean() ? 1 : 3);
                } else {
                    if (AbstractDungeon.monsterRng.randomBoolean()) {
                        apostle = new ScytheApostle(xPos_Short_L, 0.0f, this, AbstractDungeon.monsterRng.randomBoolean() ? 1 : 3);
                    } else {
                        apostle = new SpearApostle(xPos_Short_L, 0.0f, this, AbstractDungeon.monsterRng.randomBoolean() ? 1 : 3);
                    }
                }
                atb(new SpawnMonsterAction(apostle, true));
                atb(new UsePreBattleActionAction(apostle));
                apostle.rollMove();
                apostle.createIntent();
                minions[i] = apostle;
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {

    }

    private void healAnimation() {
        animationAction("Heal" + wingCounter, "Bless", this);
    }

}