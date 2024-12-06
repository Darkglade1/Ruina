package ruina.monsters.uninvitedGuests.normal.greta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.uninvitedGuests.normal.greta.hodCards.*;
import ruina.powers.act4.PurpleTearStance;
import ruina.util.AllyMove;
import ruina.util.TexLoader;
import ruina.vfx.FlexibleDivinityParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Hod extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Hod.class.getSimpleName());

    private static final byte SNAKE_SLIT = 0;
    private static final byte VIOLET_BLADE = 1;
    private static final byte LACERATION = 2;
    private static final byte VENOMOUS_FANGS = 3;
    private static final byte SERPENTINE_BARRIER = 4;
    private static final byte DUEL = 5;

    public final int snakeHits = 2;
    public final int violetHits = 3;
    public final int STRENGTH = 3;
    public final int BARRIER_BLOCK = 18;
    public final int DUEL_BLOCK = 10;
    public final int WEAK = 2;
    public final int VULNERABLE = 2;
    public static final int slashDamageBonus = 50;
    public static final int pierceTriggerHits = 2;

    private PurpleTearStance stancePower;

    public static final int SLASH = 1;
    public static final int PIERCE = 2;
    public static final int GUARD = 3;
    public int stance = PIERCE;

    private byte slashMove = SNAKE_SLIT;
    private byte pierceMove = LACERATION;
    private byte guardMove = SERPENTINE_BARRIER;

    public Hod() {
        this(0.0f, 0.0f);
    }

    public Hod(final float x, final float y) {
        super(ID, ID, 130, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hod/Spriter/Hod.scml"));
        this.animation.setFlip(true, false);

        this.setHp(calcAscensionTankiness(130));

        addMove(SNAKE_SLIT, Intent.ATTACK_BUFF, 7, snakeHits);
        addMove(VIOLET_BLADE, Intent.ATTACK, 8, violetHits);
        addMove(LACERATION, Intent.ATTACK_DEBUFF, 12);
        addMove(VENOMOUS_FANGS, Intent.ATTACK_DEBUFF, 14);
        addMove(SERPENTINE_BARRIER, Intent.DEFEND);
        addMove(DUEL, Intent.ATTACK_DEFEND, 17);

        cardList.add(new SnakeSlit(this));
        cardList.add(new VioletBlade(this));
        cardList.add(new Laceration(this));
        cardList.add(new VenomousFangs(this));
        cardList.add(new SerpentineBarrier(this));
        cardList.add(new Duel(this));

        this.icon = TexLoader.getTexture(makeUIPath("HodIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Greta) {
                target = (Greta)mo;
            }
        }
        stancePower = new PurpleTearStance(this, stance);
        applyToTarget(this, this, stancePower);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                stancePower.changeStance(stance);
                this.isDone = true;
            }
        });
        super.usePreBattleAction();
        AllyMove changeToSlash = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("SlashStance.png")), DIALOG[3], () -> {
            if (stance == SLASH) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(SLASH);
            }
        });
        changeToSlash.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        changeToSlash.setY(this.intentHb.cY - ((32.0f - 80.0f) * Settings.scale));
        allyMoves.add(changeToSlash);

        AllyMove changeToPierce = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("PierceStance.png")), DIALOG[4], () -> {
            if (stance == PIERCE) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(PIERCE);
            }
        });
        changeToPierce.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        changeToPierce.setY(this.intentHb.cY - ((32.0f - 160.0f) * Settings.scale));
        allyMoves.add(changeToPierce);

        AllyMove changeToGuard = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("GuardStance.png")), DIALOG[5], () -> {
            if (stance == GUARD) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(GUARD);
            }
        });
        changeToGuard.setX(this.intentHb.x - ((50.0F + 32.0f) * Settings.scale));
        changeToGuard.setY(this.intentHb.cY - ((32.0f - 240.0f) * Settings.scale));
        allyMoves.add(changeToGuard);
    }

    @Override
    public void takeTurn() {
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        switch (this.nextMove) {
            case SNAKE_SLIT: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashHoriAnimation(target);
                    } else {
                        slashVertAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                slashMove = VIOLET_BLADE;
                break;
            }
            case VIOLET_BLADE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashVertAnimation(target);
                    } else {
                        slashHoriAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                slashMove = SNAKE_SLIT;
                break;
            }
            case LACERATION: {
                pierceAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                pierceMove = VENOMOUS_FANGS;
                break;
            }
            case VENOMOUS_FANGS: {
                pierceAnimation2(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, false));
                resetIdle();
                pierceMove = LACERATION;
                break;
            }
            case SERPENTINE_BARRIER: {
                blockAnimation();
                block(adp(), BARRIER_BLOCK);
                atb(new AllyGainBlockAction(this, BARRIER_BLOCK));
                resetIdle();
                guardMove = DUEL;
                break;
            }
            case DUEL: {
                block(adp(), DUEL_BLOCK);
                atb(new AllyGainBlockAction(this, DUEL_BLOCK));
                bluntAnimation(target);
                dmg(target, info);
                resetIdle();
                guardMove = SERPENTINE_BARRIER;
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (stance == SLASH) {
            if (slashMove == VIOLET_BLADE) {
                setMoveShortcut(VIOLET_BLADE, cardList.get(VIOLET_BLADE));
            } else {
                setMoveShortcut(SNAKE_SLIT, cardList.get(SNAKE_SLIT));
            }
        } else if (stance == PIERCE) {
            if (pierceMove == VENOMOUS_FANGS) {
                setMoveShortcut(VENOMOUS_FANGS, cardList.get(VENOMOUS_FANGS));
            } else {
                setMoveShortcut(LACERATION, cardList.get(LACERATION));
            }
        } else {
            if (guardMove == DUEL) {
                setMoveShortcut(DUEL, cardList.get(DUEL));
            } else {
                setMoveShortcut(SERPENTINE_BARRIER, cardList.get(SERPENTINE_BARRIER));
            }
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

    private void slashHoriAnimation(AbstractCreature enemy) {
        animationAction("SlashHori", "PurpleSlashHori", enemy, this);
    }

    private void slashVertAnimation(AbstractCreature enemy) {
        animationAction("SlashVert", "PurpleSlashVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "PurpleStab1", enemy, this);
    }

    private void pierceAnimation2(AbstractCreature enemy) {
        animationAction("Pierce", "PurpleStab2", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "PurpleBlunt", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "PurpleGuard", this);
    }

    public void IdlePose() {
        runAnim("Idle" + stance);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                IdlePose();
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(PurpleTearStance.POWER_ID)) {
            if (stance == PIERCE && this.getPower(PurpleTearStance.POWER_ID).amount >= pierceTriggerHits - 1) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(DivinityStance.STANCE_ID, this));
                }
            }
        }
    }

}