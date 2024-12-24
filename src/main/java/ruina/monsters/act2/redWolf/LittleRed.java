package ruina.monsters.act2.redWolf;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.multiplayer.MultiplayerAllyBuff;
import ruina.powers.act2.FuryWithNoOutlet;
import ruina.powers.act2.StrikeWithoutHesitation;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class LittleRed extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(LittleRed.class.getSimpleName());

    private static final byte BEAST_HUNT = 0;
    private static final byte CATCH_BREATH = 1;
    private static final byte HOLLOW_POINT_SHELL = 2;
    private static final byte BULLET_SHOWER = 3;

    private final int DEFENSE = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(10));
    private final int STRENGTH = 3;

    public static final int ENRAGE_PHASE = 2;

    public LittleRed() {
        this(0.0f, 0.0f);
    }

    public LittleRed(final float x, final float y) {
        super(ID, ID, 150, -5.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("LittleRed/Spriter/LittleRed.scml"));
        this.animation.setFlip(true, false);
        this.setHp(150);

        addMove(BEAST_HUNT, Intent.ATTACK, calcAscensionDamage(9));
        addMove(CATCH_BREATH, Intent.BUFF);
        addMove(HOLLOW_POINT_SHELL, Intent.ATTACK, calcAscensionDamage(7), 2);
        addMove(BULLET_SHOWER, Intent.ATTACK, calcAscensionDamage(8), 3);

        this.icon = TexLoader.getTexture(makeUIPath("RedIcon.png"));
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
                target = (NightmareWolf)mo;
            }
        }
        addPower(new FuryWithNoOutlet(this));
        if (phase == ENRAGE_PHASE) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    enrage(false);
                    this.isDone = true;
                }
            });
        } else {
            super.usePreBattleAction();
        }
    }

    @Override
    public void takeTurn() {
        if (this.firstMove && phase == DEFAULT_PHASE) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();

        AbstractCreature target;
        if (phase == ENRAGE_PHASE) {
            target = AbstractDungeon.player;
        } else {
            target = this.target;
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
                atb(new HealAction(this, this, DEFENSE));
                applyToTarget(this, this, new StrikeWithoutHesitation(this, STRENGTH));
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

    public void enrage(boolean gainStrAndHeal) {
        if (isAlly) {
            halfDead = false;
            setPhase(ENRAGE_PHASE);
            isAlly = false;
            animation.setFlip(false, false);
            playSound("Rage", 2.0f);
            Color color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentColor", color);
            AbstractDungeon.scene.nextRoom(AbstractDungeon.getCurrRoom()); //switches bg
            atb(new ShoutAction(this, DIALOG[1], 2.0F, 3.0F));
            atb(new VFXAction(this, new InflameEffect(this), 1.0F));
            if (gainStrAndHeal) {
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                atb(new HealAction(this, this, maxHealth));
            }
            if (RuinaMod.isMultiplayerConnected()) {
                makePowerRemovable(this, MultiplayerAllyBuff.POWER_ID);
                atb(new RemoveSpecificPowerAction(this, this, MultiplayerAllyBuff.POWER_ID));
            }
        }
    }

    @Override
    protected void getMove(final int num) {
        if (phase == DEFAULT_PHASE) {
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
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makePowerPath("StrikeWithoutHesitation32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case CATCH_BREATH: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, texture);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, DEFENSE, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!target.isDeadOrEscaped()) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.0f, DIALOG[3], false));
            if (target instanceof NightmareWolf) {
                ((NightmareWolf) target).onRedDeath();
            }
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