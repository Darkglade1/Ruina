package ruina.monsters.act3.whiteNight;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act3.WhiteNightBlessing;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WhiteNight extends AbstractRuinaMonster {
    public static final String ID = makeID(WhiteNight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    public static final String GLOWING_CIRCLE = RuinaMod.makeMonsterPath("WhiteNight/GlowingCircle.png");
    private static final Texture GLOWING_CIRCLE_TEXTURE = new Texture(GLOWING_CIRCLE);
    private static final byte PRAYER = 0;
    private static final byte RISE_AND_SERVE = 1;
    private static final byte BENEDICTION = 2;
    private static final byte SALVATION = 3;
    private static final byte BEHOLD = 4;
    private static final byte SAVIOR = 5;

    private boolean awakened = false;
    public AbstractMonster[] minions = new AbstractMonster[3];
    private int moveCounter = 0;

    private final int ritual = calcAscensionSpecial(1);
    private final int heal = calcAscensionTankiness(8);
    private final int block = calcAscensionTankiness(15);
    private final int BLESSING_AMT = 12;

    public WhiteNight() {
        this(-50.0f, 0.0f);
    }

    public WhiteNight(final float x, final float y) {
        super(NAME, ID, 666, -5.0F, 170, 280.0f, 305.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WhiteNight/Spriter/WhiteNightEgg.scml"));
        this.setHp(666);

        addMove(PRAYER, Intent.BUFF);
        addMove(RISE_AND_SERVE, Intent.ATTACK, calcAscensionDamage(50));
        addMove(BENEDICTION, Intent.BUFF);
        addMove(SALVATION, Intent.DEFEND_BUFF);
        addMove(BEHOLD, Intent.ATTACK, calcAscensionDamage(21));
        addMove(SAVIOR, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela3");
        applyToTarget(this, this, new WhiteNightBlessing(this, BLESSING_AMT, this));
    }

    public void awaken() {
        waitAnimation(1.0f);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.fadeIn(2.5f);
                CustomDungeon.playTempMusicInstantly("WhiteNightBGM");
                playSound("WhiteNightAppear");
                AbstractDungeon.effectsQueue.add(new WhiteNightAura(hb.cX + (10.0f * Settings.scale), hb.cY));
                WhiteNight.this.animation = null;
                loadAnimation(makeMonsterPath("WhiteNight/atlas/WhiteNight.atlas"), makeMonsterPath("WhiteNight/atlas/WhiteNight.json"), 1.5F);
                AnimationState.TrackEntry e = WhiteNight.this.state.setAnimation(0, "normal", true);
                e.setTime(e.getEndTime() * MathUtils.random());
                WhiteNight.this.state.setTimeScale(1.0F);
                WhiteNight.this.flipHorizontal = true;
                awakened = true;
                this.isDone = true;
            }
        });
        makePowerRemovable(this, WhiteNightBlessing.POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, WhiteNightBlessing.POWER_ID));
        atb(new RollMoveAction(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case PRAYER:
                playSoundAnimation("ProphetBless");
                applyToTarget(this, this, new RitualPower(this, ritual, false));
                waitAnimation();
                break;
            case RISE_AND_SERVE:
                shockwaveEffect();
                dmg(adp(), info);
                waitAnimation();
                Texture apostles = TexLoader.getTexture(makeMonsterPath("Seraphim/Apostles.png"));
                playSoundAnimation("WhiteNightSummon");
                flashImageVfx(apostles, 2.0f);
                Summon();
                break;
            case BENEDICTION:
                playSoundAnimation("ProphetBless");
                int ritAmt = 0;
                AbstractPower ritPower = this.getPower(RitualPower.POWER_ID);
                if (ritPower != null) {
                    ritAmt = ritPower.amount;
                }
                ritAmt = Math.max(ritAmt, ritual);
                for (AbstractMonster m : monsterList()) {
                    if (!m.isDeadOrEscaped()) {
                        if (m == this) {
                            applyToTarget(this, this, new StrengthPower(this, ritAmt));
                        } else {
                            applyToTarget(m, this, new RitualPower(m, ritAmt, false));
                        }
                    }
                }
                waitAnimation();
                break;
            case SALVATION:
                playSoundAnimation("ProphetBless");
                for (AbstractMonster m : monsterList()) {
                    if (!m.isDeadOrEscaped()) {
                        atb(new HealAction(m, this, heal));
                        block(m, block);
                    }
                }
                waitAnimation();
                break;
            case BEHOLD:
                shockwaveEffect();
                dmg(adp(), info);
                waitAnimation();
                break;
            case SAVIOR:
                playSoundAnimation("WhiteNightSummon");
                Summon();
                waitAnimation();
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (nextMove == BEHOLD || nextMove == RISE_AND_SERVE) {
                    moveCounter = 0;
                } else if (nextMove != SAVIOR) {
                    moveCounter++;
                }
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!awakened) {
            setMoveShortcut(PRAYER);
        } else {
            if (lastMove(PRAYER)) {
                setMoveShortcut(RISE_AND_SERVE);
            } else if (minions[0] == null && minions[1] == null && minions[2] == null && moveCounter < 2) {
                setMoveShortcut(SAVIOR);
            } else {
                if (moveCounter == 0) {
                    setMoveShortcut(BENEDICTION);
                } else if (moveCounter == 1) {
                    setMoveShortcut(SALVATION);
                } else {
                    setMoveShortcut(BEHOLD);
                }
            }
        }
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (mo.hasPower(MinionPower.POWER_ID)) {
                atb(new SuicideAction(mo));
            }
        }
        onBossVictoryLogic();
        this.onFinalBossVictoryLogic();
    }

    public void Summon() {
        float xPos_Middle_L = -650F;
        float xPos_Short_L = -350F;
        float xPos_Shortest_L = 250F;
        AbstractMonster apostle1 = new ScytheApostle(xPos_Middle_L, 0.0f, this);
        minions[0] = apostle1;
        atb(new SpawnMonsterAction(apostle1, true));
        atb(new UsePreBattleActionAction(apostle1));

        AbstractMonster apostle2 = new SpearApostle(xPos_Short_L, 0.0f, this);
        minions[1] = apostle2;
        atb(new SpawnMonsterAction(apostle2, true));
        atb(new UsePreBattleActionAction(apostle2));

        AbstractMonster apostle3 = new StaffApostle(xPos_Shortest_L, 0.0f, this);
        minions[2] = apostle3;
        atb(new SpawnMonsterAction(apostle3, true));
        atb(new UsePreBattleActionAction(apostle3));

    }

    private void shockwaveEffect() {
        float y = this.hb.cY;
        float x = this.hb.cX;
        float chargeDuration = 1.2f;
        AbstractGameEffect shockwaveCharge = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, x, y, chargeDuration)
                .scale(4.0f, 0.0f, VfxBuilder.Interpolations.LINEAR)
                .playSoundAt(0.0f, makeID("WhiteNightCharge"))
                .rotate(-600f)
                .build();
        float burstDuration = 0.7f;
        AbstractGameEffect shockwaveBurst = new VfxBuilder(GLOWING_CIRCLE_TEXTURE, x, y, burstDuration)
                .scale(0.0f, 8.0f, VfxBuilder.Interpolations.SWING)
                .playSoundAt(0.0f, makeID("WhiteNightFire"))
                .build();
        atb(new VFXAction(shockwaveCharge, chargeDuration));
        atb(new VFXAction(shockwaveBurst, burstDuration - 0.3f));
    }

}