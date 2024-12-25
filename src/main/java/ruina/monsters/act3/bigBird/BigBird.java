package ruina.monsters.act3.bigBird;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.cards.Dazzled;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act3.Enchanted;
import ruina.powers.act3.Salvation;
import ruina.powers.multiplayer.EnchantedMultiplayer;
import ruina.util.AdditionalIntent;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class BigBird extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(BigBird.class.getSimpleName());

    private static final Texture EXECUTE = TexLoader.getTexture(makeMonsterPath("BigBird/Salvation.png"));

    private static final byte SALVATION = 0;
    private static final byte DAZZLE_ALLY = 1;
    private static final byte DAZZLE_PLAYER = 2;
    private static final byte ILLUMINATE = 3;
    private static final byte ILLUMINATE_2 = 4;
    
    private final int STATUS = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    private final int STRENGTH = calcAscensionSpecial(2);
    
    public Sage sage1;
    public Sage sage2;

    public static final int INSTANT_KILL_NUM = 999;
    private static final int ENCHANTED_HP_AMT = 20;
    private static final int ENCHANTED_HP_INCREASE = 5;

    public BigBird() {
        this(100.0f, 0.0f);
    }

    public BigBird(final float x, final float y) {
        super(ID, ID, 400, -5.0F, 0, 300.0f, 355.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BigBird/Spriter/BigBird.scml"));
        setNumAdditionalMoves(2);
        this.setHp(calcAscensionTankiness(400));

        addMove(SALVATION, Intent.ATTACK, calcAscensionDamage(17));
        addMove(DAZZLE_ALLY, Intent.STRONG_DEBUFF);
        addMove(DAZZLE_PLAYER, Intent.DEBUFF);
        addMove(ILLUMINATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(ILLUMINATE_2, Intent.ATTACK_BUFF, calcAscensionDamage(8));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning2");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Sage) {
                if (sage1 == null) {
                    sage1 = (Sage) mo;
                } else if (sage2 == null) {
                    sage2 = (Sage) mo;
                }
            }
        }
        applyToTarget(this, this, new Salvation(this));
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case SALVATION: {
                if (target == adp()) {
                    dazzleAnimation(target);
                    dmg(target, info);
                } else {
                    if (target.hasPower(Enchanted.POWER_ID) || target.hasPower(EnchantedMultiplayer.POWER_ID)) {
                        salvation1Animation(target);
                        atb(new VFXAction(new WaitEffect(), 0.25f));
                        flashImageVfx(EXECUTE, 1.5f);
                        salvation2Animation(target);
                        AbstractCreature realTarget = target;
                        atb(new AbstractGameAction() {
                            @Override
                            public void update() {
                                AbstractDungeon.effectList.add(new StrikeEffect(realTarget, realTarget.hb.cX, realTarget.hb.cY, INSTANT_KILL_NUM));
                                this.isDone = true;
                            }
                        });
                        atb(new InstantKillAction(target));
                    } else {
                        dazzleAnimation(target);
                        dmg(target, info);
                    }
                }
                resetIdle(1.0f);
                break;
            }
            case DAZZLE_ALLY: {
                dazzleAnimation(target);
                if (target != adp()) {
                    int currEnchantedHPAmt = ENCHANTED_HP_AMT + ((phase - 1) * ENCHANTED_HP_INCREASE);
                    if (RuinaMod.isMultiplayerConnected()) {
                        applyToTarget(target, this, new EnchantedMultiplayer(target, RuinaMod.getMultiplayerPlayerCountScaling(currEnchantedHPAmt), 1));
                    } else {
                        applyToTarget(target, this, new Enchanted(target, currEnchantedHPAmt, 1));
                    }
                    setPhase(phase + 1);
                } else {
                    intoDiscardMo(new Dazzled(), STATUS, this);
                }
                resetIdle(1.0f);
                break;
            }
            case DAZZLE_PLAYER: {
                specialAnimation(target);
                if (whichMove >= 0) {
                    intoDiscardMo(new Dazzled(), STATUS, this);
                } else {
                    intoDrawMo(new Dazzled(), STATUS, this);
                }
                resetIdle(1.0f);
                break;
            }
            case ILLUMINATE: {
                dazzleAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                resetIdle(1.0f);
                break;
            }
            case ILLUMINATE_2: {
                dazzleAnimation(target);
                dmg(target, info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void salvation1Animation(AbstractCreature enemy) {
        animationAction("Salvation1", "BigBirdOpen", enemy, this);
    }

    private void salvation2Animation(AbstractCreature enemy) {
        animationAction("Salvation2", "BigBirdCrunch", enemy, this);
    }

    private void dazzleAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdLamp", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Lamp", "BigBirdEyes", enemy, this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected int applySpecialMultiplier(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, AbstractCreature target, int whichMove, int dmg) {
        if (target.hasPower(Enchanted.POWER_ID) || target.hasPower(EnchantedMultiplayer.POWER_ID)) {
            return BigBird.INSTANT_KILL_NUM;
        }
        return dmg;
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SALVATION)) {
            setMoveShortcut(DAZZLE_PLAYER);
        } else {
            setMoveShortcut(SALVATION);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            if (this.firstMove) {
                setAdditionalMoveShortcut(DAZZLE_PLAYER, moveHistory);
                moveHistory.clear(); // we don't want subsequent moves to be affected by this move being in the move history
            } else {
                if (sage1 != null && (sage1.isDead || sage1.isDying)) {
                    if (this.lastMove(SALVATION)) {
                        setAdditionalMoveShortcut(SALVATION, moveHistory);
                    } else {
                        setAdditionalMoveShortcut(ILLUMINATE, moveHistory);
                    }
                } else {
                    if (this.lastMove(DAZZLE_ALLY, moveHistory)) {
                        setAdditionalMoveShortcut(DAZZLE_PLAYER, moveHistory);
                    } else if (this.lastMove(DAZZLE_PLAYER, moveHistory)) {
                        setAdditionalMoveShortcut(SALVATION, moveHistory);
                    }  else {
                        setAdditionalMoveShortcut(DAZZLE_ALLY, moveHistory);
                    }
                }
            }
        }

        if (whichMove == 1) {
            if (sage2 != null && (sage2.isDead || sage2.isDying)) {
                if (this.lastMove(SALVATION)) {
                    setAdditionalMoveShortcut(SALVATION, moveHistory);
                } else {
                    setAdditionalMoveShortcut(ILLUMINATE_2, moveHistory);
                }
            } else {
                if (this.lastMove(DAZZLE_ALLY, moveHistory)) {
                    setAdditionalMoveShortcut(DAZZLE_PLAYER, moveHistory);
                } else if (this.lastMove(DAZZLE_PLAYER, moveHistory)) {
                    setAdditionalMoveShortcut(SALVATION, moveHistory);
                }  else {
                    setAdditionalMoveShortcut(DAZZLE_ALLY, moveHistory);
                }
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makeUIPath("detailedIntents/Dazzled.png");
        Texture texture = TexLoader.getTexture(textureString);
        String textureString2 = makePowerPath("Enchanted32.png");
        Texture texture2 = TexLoader.getTexture(textureString2);
        switch (move.nextMove) {
            case DAZZLE_PLAYER: {
                if (intentNum >= 0) {
                    DetailedIntent detail = new DetailedIntent(this, STATUS, texture, DetailedIntent.TargetType.DISCARD_PILE);
                    detailsList.add(detail);
                } else {
                    DetailedIntent detail = new DetailedIntent(this, STATUS, texture, DetailedIntent.TargetType.DRAW_PILE);
                    detailsList.add(detail);
                }
                break;
            }
            case DAZZLE_ALLY: {
                DetailedIntent detail = new DetailedIntent(this, 1, texture2);
                detailsList.add(detail);
                break;
            }
            case ILLUMINATE: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, DEBUFF, DetailedIntent.FRAIL_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case ILLUMINATE_2: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (additionalMove.nextMove == DAZZLE_PLAYER) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
        } else if (index == 0) {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, sage1, sage1.icon, index);
        } else {
            applyPowersToAdditionalIntent(additionalMove, additionalIntent, sage2, sage2.icon, index);
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (mo instanceof Sage) {
                ((Sage) mo).onBigBirdDeath();
            }
        }
    }

}