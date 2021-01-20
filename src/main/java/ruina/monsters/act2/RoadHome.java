package ruina.monsters.act2;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class RoadHome extends AbstractRuinaMonster
{
    public static final String ID = makeID(RoadHome.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static final String HOUSE = RuinaMod.makeMonsterPath("RoadHome/House.png");
    private static final Texture HOUSE_TEXTURE = new Texture(HOUSE);

    private static final byte LETS_GO = 0;
    private static final byte PLAY_TAG = 1;
    private static final byte HOMING_INSTINCT = 2;

    private ScaredyCat cat;

    private final int BLOCK = calcAscensionTankiness(8);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int PARALYSIS = calcAscensionSpecial(2);

    public RoadHome() {
        this(0.0f, 0.0f);
    }

    public RoadHome(final float x, final float y) {
        super(NAME, ID, 120, -5.0F, 0, 200.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("RoadHome/Spriter/RoadHome.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(LETS_GO, Intent.BUFF);
        addMove(PLAY_TAG, Intent.DEFEND);
        addMove(HOMING_INSTINCT, Intent.ATTACK_DEBUFF, calcAscensionDamage(25));
    }

    @Override
    public void usePreBattleAction() {
        //CustomDungeon.playTempMusicInstantly("Warning1");
        float xPosition = -300.0F;
        cat = new ScaredyCat(xPosition, 0.0f, this);
        atb(new SpawnMonsterAction(cat, true));
        atb(new UsePreBattleActionAction(cat));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        switch (this.nextMove) {
            case LETS_GO: {
                specialAnimation();
                if (!cat.isDeadOrEscaped()) {
                    applyToTarget(cat, this, new StrengthPower(cat, STRENGTH));
                } else {
                    block(this, BLOCK);
                }
                resetIdle();
                break;
            }
            case PLAY_TAG: {
                specialAnimation();
                for (AbstractMonster mo : monsterList()) {
                    block(mo, BLOCK);
                }
                resetIdle();
                break;
            }
            case HOMING_INSTINCT: {
                attackAnimation(adp());
                HouseEffect();
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (cat != null && cat.isDeadOrEscaped()) {
            if (lastMove(HOMING_INSTINCT)) {
                setMoveShortcut(PLAY_TAG, MOVES[PLAY_TAG]);
            } else {
                setMoveShortcut(HOMING_INSTINCT, MOVES[HOMING_INSTINCT]);
            }
        } else {
            if (lastMove(LETS_GO)) {
                setMoveShortcut(PLAY_TAG, MOVES[PLAY_TAG]);
            } else {
                setMoveShortcut(LETS_GO, MOVES[LETS_GO]);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!cat.isDeadOrEscaped()) {
            cat.roadDeath();
        }
    }

    public void catDeath() {
        atb(new TalkAction(this, DIALOG[1]));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "HouseAttack", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "MakeRoad", this);
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