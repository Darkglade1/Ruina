package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BobEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RoadHome extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(RoadHome.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String HOUSE = RuinaMod.makeMonsterPath("RoadHome/House.png");
    private static final Texture HOUSE_TEXTURE = TexLoader.getTexture(HOUSE);
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("MultiIntentStrings"));
    protected static final String[] TEXT = uiStrings.TEXT;

    private static final byte LETS_GO = 0;
    private static final byte NONE = 1;
    private static final byte HOMING_INSTINCT = 2;

    private ScaredyCat cat;
    private HomeAlly home;

    public boolean isHomeDead = false;

    private final int STATUS = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("EasilyDistracted");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public RoadHome() {
        this(0.0f, 0.0f);
    }

    public RoadHome(final float x, final float y) {
        super(NAME, ID, 120, -5.0F, 0, 200.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RoadHome/Spriter/RoadHome.scml"));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        setHp(calcAscensionTankiness(maxHealth));
        addMove(LETS_GO, Intent.ATTACK, calcAscensionDamage(10));
        addMove(HOMING_INSTINCT, Intent.ATTACK_DEBUFF, calcAscensionDamage(35));
        addMove(NONE, Intent.NONE);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof ScaredyCat) {
                cat = (ScaredyCat)mo;
            }
            if (mo instanceof HomeAlly) {
                home = (HomeAlly)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public int onAttacked(DamageInfo info, int damageAmount) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != this.owner) {
                    this.flash();
                    att(new AbstractGameAction() {
                        @Override
                        public void update() {
                            if (!RoadHome.this.hasPower(StunMonsterPower.POWER_ID)) {
                                if (RoadHome.this.additionalIntents.size() > 0) {
                                    RoadHome.this.additionalIntents.remove(0);
                                    RoadHome.this.additionalMoves.remove(0);
                                } else {
                                    setMoveShortcut(NONE);
                                    createIntent();
                                }
                            }
                            this.isDone = true;
                        }
                    });
                }
                return damageAmount;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        switch (this.nextMove) {
            case LETS_GO: {
                specialAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
            case HOMING_INSTINCT: {
                attackAnimation(target);
                HouseEffect();
                dmg(target, info);
                intoDiscardMo(new Dazed(), STATUS, this);
                resetIdle();
                break;
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (isHomeDead) {
            takeCustomTurn(this.moves.get(nextMove), adp());
        } else {
            takeCustomTurn(this.moves.get(nextMove), home);
        }
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, home);
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
        if (isHomeDead) {
            setMoveShortcut(HOMING_INSTINCT, MOVES[HOMING_INSTINCT]);
        } else {
            setMoveShortcut(LETS_GO, MOVES[LETS_GO]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (!isHomeDead) {
            setAdditionalMoveShortcut(LETS_GO, moveHistory);
        }
    }

    @Override
    public void createIntent() {
        super.createIntent();
        applyPowers();
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        if (!isHomeDead) {
            DamageInfo info = new DamageInfo(this, moves.get(this.nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            AbstractCreature target = home;
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, home, home.allyIcon);
            }
        }
    }

    @Override
    public void renderIntent(SpriteBatch sb) {
        super.renderIntent(sb);
        if (!isHomeDead) {
            BobEffect bobEffect = ReflectionHacks.getPrivate(this, AbstractMonster.class, "bobEffect");
            float intentAngle = ReflectionHacks.getPrivate(this, AbstractMonster.class, "intentAngle");
            sb.draw(HomeAlly.targetTexture, this.intentHb.cX - 48.0F, this.intentHb.cY - 48.0F + (40.0f * Settings.scale) + bobEffect.y, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, intentAngle, 0, 0, 48, 48, false, false);
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.output > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            AbstractPower power = cat.getPower(ScaredyCat.POWER_ID);
            if (power != null) {
                power.onSpecificTrigger();
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!cat.isDeadOrEscaped()) {
            cat.roadDeath();
        }
        if (!isHomeDead) {
            home.OnRoadDeath();
        }
    }

    public void homeDeath() {
        isHomeDead = true;
        makePowerRemovable(this, POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, POWER_ID));
    }

    public void catDeath() {
        atb(new TalkAction(this, DIALOG[1]));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "HouseAttack", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "MakeRoad", enemy, this);
    }

    private void HouseEffect() {
        float duration = 1.0f;
        AbstractGameEffect houseEffect = new VfxBuilder(HOUSE_TEXTURE, adp().hb.cX, 0f, duration)
                .moveY(Settings.HEIGHT, adp().hb.y + adp().hb.height / 6, VfxBuilder.Interpolations.EXP5IN)
                .playSoundAt(duration, makeID("HouseBoom"))
                .build();
        atb(new VFXAction(houseEffect, duration));
    }

}