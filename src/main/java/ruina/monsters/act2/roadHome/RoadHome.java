package ruina.monsters.act2.roadHome;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.act2.EasilyDistracted;
import ruina.powers.multiplayer.EasilyDistractedMultiplayer;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RoadHome extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(RoadHome.class.getSimpleName());
    public static final String HOUSE = RuinaMod.makeMonsterPath("RoadHome/House.png");
    private static final Texture HOUSE_TEXTURE = TexLoader.getTexture(HOUSE);

    private static final byte LETS_GO = 0;
    public static final byte NONE = 1;
    private static final byte HOMING_INSTINCT = 2;

    private ScaredyCat cat;

    public boolean isHomeDead = false;

    private final int STATUS = calcAscensionSpecial(2);

    public RoadHome() {
        this(0.0f, 0.0f);
    }

    public RoadHome(final float x, final float y) {
        super(ID, ID, 120, -5.0F, 0, 200.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RoadHome/Spriter/RoadHome.scml"));
        setNumAdditionalMoves(2);
        setHp(calcAscensionTankiness(120));
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
                target = (HomeAlly)mo;
            }
        }
        if (RuinaMod.isMultiplayerConnected()) {
            applyToTarget(this, this, new EasilyDistractedMultiplayer(this, RuinaMod.getMultiplayerPlayerCountScaling(1), 0));
        } else {
            applyToTarget(this, this, new EasilyDistracted(this));
        }
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
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
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (isHomeDead) {
            setMoveShortcut(HOMING_INSTINCT);
        } else {
            setMoveShortcut(LETS_GO);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case HOMING_INSTINCT: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.DAZED_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (!isHomeDead) {
            setAdditionalMoveShortcut(LETS_GO, moveHistory);
        }
    }

    @Override
    public void applyPowers() {
        if (!isHomeDead) {
            attackingMonsterWithPrimaryIntent = true;
        } else {
            attackingMonsterWithPrimaryIntent = false;
        }
        super.applyPowers();
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!cat.isDeadOrEscaped()) {
            cat.roadDeath();
        }
        if (!isHomeDead && target instanceof HomeAlly) {
            ((HomeAlly) target).OnRoadDeath();
        }
    }

    public void homeDeath() {
        isHomeDead = true;
        if (RuinaMod.isMultiplayerConnected()) {
            makePowerRemovable(this, EasilyDistractedMultiplayer.POWER_ID);
            atb(new RemoveSpecificPowerAction(this, this, EasilyDistractedMultiplayer.POWER_ID));
        } else {
            makePowerRemovable(this, EasilyDistracted.POWER_ID);
            atb(new RemoveSpecificPowerAction(this, this, EasilyDistracted.POWER_ID));
        }
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