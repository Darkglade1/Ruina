package ruina.monsters.act3.bigBird;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.cards.Dazzled;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Enchanted;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.util.TexLoader;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BigBird extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(BigBird.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final Texture EXECUTE = TexLoader.getTexture(makeMonsterPath("BigBird/Salvation.png"));

    private static final byte SALVATION = 0;
    private static final byte DAZZLE_ENEMY = 1;
    private static final byte DAZZLE_PLAYER = 2;
    private static final byte ILLUMINATE = 3;
    private static final byte ILLUMINATE_2 = 4;
    
    private final int STATUS = calcAscensionSpecial(3);
    private final int DEBUFF = calcAscensionSpecial(1);
    
    public Sage sage1;
    public Sage sage2;

    public static final int INSTANT_KILL_NUM = 999;

    public static final String Salvation_POWER_ID = makeID("Salvation");
    public static final PowerStrings SalvationPowerStrings = CardCrawlGame.languagePack.getPowerStrings(Salvation_POWER_ID);
    public static final String Salvation_POWER_NAME = SalvationPowerStrings.NAME;
    public static final String[] Salvation_POWER_DESCRIPTIONS = SalvationPowerStrings.DESCRIPTIONS;

    public BigBird() {
        this(100.0f, 0.0f);
    }

    public BigBird(final float x, final float y) {
        super(NAME, ID, 400, -5.0F, 0, 300.0f, 355.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BigBird/Spriter/BigBird.scml"));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(400));

        addMove(SALVATION, Intent.ATTACK, calcAscensionDamage(17));
        addMove(DAZZLE_ENEMY, Intent.STRONG_DEBUFF);
        addMove(DAZZLE_PLAYER, Intent.DEBUFF);
        addMove(ILLUMINATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(ILLUMINATE_2, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
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
        applyToTarget(this, this, new AbstractLambdaPower(Salvation_POWER_NAME, Salvation_POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void updateDescription() {
                description = Salvation_POWER_DESCRIPTIONS[0];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case SALVATION: {
                if (target == adp()) {
                    dazzleAnimation(target);
                    dmg(target, info);
                } else {
                    if (target.hasPower(Enchanted.POWER_ID)) {
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
            case DAZZLE_ENEMY: {
                dazzleAnimation(target);
                if (target != adp()) {
                    applyToTarget(target, this, new Enchanted(target, 1));
                } else {
                    intoDiscardMo(new Dazzled(), STATUS, this);
                }
                resetIdle(1.0f);
                break;
            }
            case DAZZLE_PLAYER: {
                specialAnimation(target);
                intoDrawMo(new Dazzled(), STATUS, this);
                resetIdle(1.0f);
                break;
            }
            case ILLUMINATE: {
                dazzleAnimation(target);
                dmg(target, info);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                    applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                } else {
                    applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                }
                resetIdle(1.0f);
                break;
            }
            case ILLUMINATE_2: {
                dazzleAnimation(target);
                dmg(target, info);
                if (AbstractDungeon.ascensionLevel >= 18) {
                    applyToTarget(target, this, new VulnerablePower(target, DEBUFF, true));
                } else {
                    applyToTarget(target, this, new FrailPower(target, DEBUFF, true));
                }
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
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (i == 0) {
                if (sage1.isDead || sage1.isDying) {
                    takeCustomTurn(additionalMove, adp());
                } else {
                    takeCustomTurn(additionalMove, sage1);
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        additionalIntent.usePrimaryIntentsColor = true;
                        this.isDone = true;
                    }
                });
            }
            if (i == 1) {
                if (sage2.isDead || sage2.isDying) {
                    takeCustomTurn(additionalMove, adp());
                } else {
                    takeCustomTurn(additionalMove, sage2);
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        additionalIntent.usePrimaryIntentsColor = true;
                        this.isDone = true;
                    }
                });
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SALVATION)) {
            setMoveShortcut(DAZZLE_PLAYER, MOVES[DAZZLE_PLAYER]);
        } else {
            setMoveShortcut(SALVATION, MOVES[SALVATION]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            if (this.firstMove) {
                setAdditionalMoveShortcut(DAZZLE_PLAYER, moveHistory);
            } else {
                if (sage1 != null && (sage1.isDead || sage1.isDying)) {
                    if (this.lastMove(ILLUMINATE, moveHistory)) {
                        setAdditionalMoveShortcut(SALVATION, moveHistory);
                    } else {
                        setAdditionalMoveShortcut(ILLUMINATE, moveHistory);
                    }
                } else {
                    if (!this.lastMove(DAZZLE_ENEMY, moveHistory)) {
                        setAdditionalMoveShortcut(DAZZLE_ENEMY, moveHistory);
                    } else {
                        setAdditionalMoveShortcut(SALVATION, moveHistory);
                    }
                }
            }
        }

        if (whichMove == 1) {
            if (sage2 != null && (sage2.isDead || sage2.isDying)) {
                if (this.lastMove(ILLUMINATE_2, moveHistory)) {
                    setAdditionalMoveShortcut(SALVATION, moveHistory);
                } else {
                    setAdditionalMoveShortcut(ILLUMINATE_2, moveHistory);
                }
            } else {
                if (!this.lastMove(DAZZLE_ENEMY, moveHistory)) {
                    setAdditionalMoveShortcut(DAZZLE_ENEMY, moveHistory);
                } else {
                    setAdditionalMoveShortcut(SALVATION, moveHistory);
                }
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                if (i == 0) {
                    if (additionalMove.nextMove == DAZZLE_PLAYER) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, sage1, sage1.allyIcon);
                    }
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, sage2, sage2.allyIcon);
                }
            }
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