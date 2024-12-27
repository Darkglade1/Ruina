package ruina.monsters.act1;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act1.FerventAdoration;
import ruina.powers.act1.Performance;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.OrchestraCurtainEffect;
import ruina.vfx.OrchestraMusicEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Orchestra extends AbstractRuinaMonster
{
    public static final String ID = makeID(Orchestra.class.getSimpleName());

    private static final byte FIRST = 0;
    private static final byte SECOND = 1;
    private static final byte THIRD = 2;
    private static final byte FOURTH = 3;
    private static final byte FINALE = 4;
    private static final byte CURTAIN = 5;

    private final int WEAK = calcAscensionSpecial(1);
    private final int FERVENT_DAMAGE = calcAscensionSpecial(2);
    private final int FERVENT_DRAW = 1;
    private final int PLAYER_DRAW = 2;
    private final int STATUS = 1;
    private final int BLOCK = calcAscensionTankiness(8);
    private final int HEAL = RuinaMod.getMultiplayerEnemyHealthScaling(calcAscensionTankiness(10));

    public static final String MOVEMENT1 = RuinaMod.makeMonsterPath("Orchestra/1st.png");
    private final Texture MOVEMENT1_TEXTURE = TexLoader.getTexture(MOVEMENT1);

    public static final String MOVEMENT2 = RuinaMod.makeMonsterPath("Orchestra/2nd.png");
    private final Texture MOVEMENT2_TEXTURE = TexLoader.getTexture(MOVEMENT2);

    public static final String MOVEMENT3 = RuinaMod.makeMonsterPath("Orchestra/3rd.png");
    private final Texture MOVEMENT3_TEXTURE = TexLoader.getTexture(MOVEMENT3);

    public static final String MOVEMENT4 = RuinaMod.makeMonsterPath("Orchestra/4th.png");
    private final Texture MOVEMENT4_TEXTURE = TexLoader.getTexture(MOVEMENT4);

    private OrchestraMusicEffect movement1;
    private OrchestraMusicEffect movement2;
    private OrchestraMusicEffect movement3;
    private OrchestraMusicEffect movement4;

    public Orchestra() {
        this(0.0f, 0.0f);
    }

    public Orchestra(final float x, final float y) {
        super(ID, ID, 230, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Orchestra/Spriter/Orchestra.scml"));
        setHp(calcAscensionTankiness(230));
        addMove(FIRST, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(SECOND, Intent.ATTACK_DEBUFF, calcAscensionDamage(10));
        addMove(THIRD, Intent.ATTACK_BUFF, calcAscensionDamage(17));
        addMove(FOURTH, Intent.DEBUFF);
        addMove(FINALE, Intent.ATTACK_DEBUFF, calcAscensionDamage(25));
        addMove(CURTAIN, Intent.DEFEND_BUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Angela3");
        applyToTarget(this, this, new Performance(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case FIRST: {
                attackAnimation1(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        movement1 = new OrchestraMusicEffect(hb.cX + (10.0f * Settings.scale), hb.cY, MOVEMENT1_TEXTURE, false, 0.75f);
                        AbstractDungeon.effectsQueue.add(movement1);
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                applyToTarget(adp(), this, new WeakPower(adp(), WEAK, true));
                resetIdle(1.0f);
                break;
            }
            case SECOND: {
                attackAnimation2(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        movement2 = new OrchestraMusicEffect(hb.cX + (10.0f * Settings.scale), hb.cY, MOVEMENT2_TEXTURE, true, 1.25f);
                        AbstractDungeon.effectsQueue.add(movement2);
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                applyToTarget(adp(), this, new FerventAdoration(adp(), FERVENT_DAMAGE, FERVENT_DRAW));
                resetIdle(1.0f);
                break;
            }
            case THIRD: {
                attackAnimation1(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        movement3 = new OrchestraMusicEffect(hb.cX + (10.0f * Settings.scale), hb.cY, MOVEMENT3_TEXTURE, false, 1.75f);
                        AbstractDungeon.effectsQueue.add(movement3);
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), PLAYER_DRAW));
                resetIdle(1.0f);
                break;
            }
            case FOURTH: {
                attackAnimation2(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        movement4 = new OrchestraMusicEffect(hb.cX + (10.0f * Settings.scale), hb.cY, MOVEMENT4_TEXTURE, true, 2.25f);
                        AbstractDungeon.effectsQueue.add(movement4);
                        this.isDone = true;
                    }
                });
                intoDiscardMo(new VoidCard(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case FINALE: {
                finaleAnimation(adp());
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (movement1 != null) {
                            movement1.end();
                        }
                        if (movement2 != null) {
                            movement2.end();
                        }
                        if (movement3 != null) {
                            movement3.end();
                        }
                        if (movement4 != null) {
                            movement4.end();
                        }
                        this.isDone = true;
                    }
                });
                dmg(adp(), info);
                applyToTarget(adp(), this, new FerventAdoration(adp(), FERVENT_DAMAGE, FERVENT_DRAW));
                resetIdle(1.5f);
                break;
            }
            case CURTAIN: {
                curtainAnimation();
                float duration = 5.0f;
                atb(new VFXAction(new OrchestraCurtainEffect(duration), duration));
                block(this, BLOCK);
                atb(new HealAction(this, this, HEAL));
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(FIRST)) {
            setMoveShortcut(SECOND);
        } else if (lastMove(SECOND)) {
            setMoveShortcut(THIRD);
        } else if (lastMove(THIRD)) {
            setMoveShortcut(FOURTH);
        } else if (lastMove(FOURTH)) {
            setMoveShortcut(FINALE);
        } else if (lastMove(FINALE)) {
            setMoveShortcut(CURTAIN);
        } else {
            setMoveShortcut(FIRST);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makePowerPath("FerventAdoration32.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case FIRST: {
                DetailedIntent detail = new DetailedIntent(this, WEAK, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case SECOND: {
                DetailedIntent detail = new DetailedIntent(this, 1, texture);
                detailsList.add(detail);
                break;
            }
            case THIRD: {
                DetailedIntent detail = new DetailedIntent(this, PLAYER_DRAW, DetailedIntent.DRAW_UP_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case FOURTH: {
                DetailedIntent detail = new DetailedIntent(this, STATUS, DetailedIntent.VOID_TEXTURE, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
            case FINALE: {
                DetailedIntent detail = new DetailedIntent(this, 1, texture);
                detailsList.add(detail);
                break;
            }
            case CURTAIN: {
                DetailedIntent detail = new DetailedIntent(this, BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, HEAL, DetailedIntent.HEAL_TEXTURE);
                detailsList.add(detail2);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        onBossVictoryLogic();
    }

    private void attackAnimation1(AbstractCreature enemy) {
        animationAction("Special", "OrchestraMovement1", enemy, this);
    }

    private void attackAnimation2(AbstractCreature enemy) {
        animationAction("Special", "OrchestraMovement2", enemy, this);
    }

    private void finaleAnimation(AbstractCreature enemy) {
        animationAction("Special", "OrchestraFinale", enemy, this);
    }

    private void curtainAnimation() {
        animationAction("Special", "OrchestraClap", this);
    }

}