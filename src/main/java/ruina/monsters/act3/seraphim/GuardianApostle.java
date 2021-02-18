package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Unkillable;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;
import static ruina.util.Wiz.atb;

public class GuardianApostle extends AbstractMultiIntentMonster {
    public static final String ID = makeID(GuardianApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte RISE_AND_SERVE = 0;
    private static final byte COMMAND_FIRE = 1;
    private static final byte THE_KING = 2;
    private static final byte GIVE_US_REST = 3;
    private static final byte THY_WORDS_COME_UNTO_ME = 4;
    private static final byte LORD_SHEW_US = 5;
    private static final byte EMPTY = 6;

    private final int riseAndServe = calcAscensionTankiness(30);
    private int heavenlyAura = 1;
    private final int giveUsRestGuardian = calcAscensionTankiness(15);
    private final int giveUsRestBoss = calcAscensionTankiness(30);
    private final int thyWordsBlock = calcAscensionTankiness(10);
    private Seraphim seraphim;
    private boolean deadForThisTurn = false;

    public GuardianApostle(final float x, final float y, Seraphim parent) {
        super(NAME, ID, 75, -5.0F, 0, 280.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GuardianApostle/Spriter/GuardianApostle.scml"));
        this.setHp(calcAscensionTankiness(maxHealth));
        this.type = EnemyType.ELITE;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        firstMove = true;
        addMove(RISE_AND_SERVE, Intent.DEFEND_BUFF);
        addMove(COMMAND_FIRE, Intent.ATTACK, calcAscensionDamage(9));
        addMove(THE_KING, Intent.ATTACK, calcAscensionDamage(3), 3, true);
        addMove(GIVE_US_REST, Intent.DEFEND);
        addMove(THY_WORDS_COME_UNTO_ME, Intent.ATTACK_DEFEND, calcAscensionDamage(6));
        addMove(LORD_SHEW_US, Intent.ATTACK, calcAscensionDamage(2), 2, true);
        addMove(EMPTY, Intent.NONE);
        seraphim = parent;
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case RISE_AND_SERVE:
                specialAnimation();
                block(this, riseAndServe);
                applyToTarget(this, this, new RitualPower(this, heavenlyAura, false));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        firstMove = false;
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            case COMMAND_FIRE:
                slashUpAnimation(adp());
                dmg(target, info);
                resetIdle();
                break;
            case THE_KING:
            case LORD_SHEW_US:
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashUpAnimation(adp());
                    } else {
                        slashDownAnimation(adp());
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            case GIVE_US_REST:
                specialAnimation();
                for (AbstractMonster m : monsterList()) {
                    if (m instanceof GuardianApostle) {
                        block(m, giveUsRestGuardian);
                    } else {
                        block(m, giveUsRestBoss);
                    }
                }
                resetIdle();
                break;
            case THY_WORDS_COME_UNTO_ME:
                slashDownAnimation(adp());
                dmg(target, info);
                block(this, thyWordsBlock);
                resetIdle();
                break;
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                deadForThisTurn = false;
                this.isDone = true;
            }
        });
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp());
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
        if (deadForThisTurn) {
            setMoveShortcut(EMPTY, MOVES[EMPTY]);
        } else if (firstMove) {
            setMoveShortcut(RISE_AND_SERVE, MOVES[RISE_AND_SERVE]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(COMMAND_FIRE)) {
                possibilities.add(COMMAND_FIRE);
            }
            if (!this.lastMove(THE_KING)) {
                possibilities.add(THE_KING);
            }
            if (!this.lastMove(GIVE_US_REST)) {
                possibilities.add(GIVE_US_REST);
            }
            if (!this.lastMove(THY_WORDS_COME_UNTO_ME)) {
                possibilities.add(THY_WORDS_COME_UNTO_ME);
            }
            if (!this.lastMove(LORD_SHEW_US)) {
                possibilities.add(LORD_SHEW_US);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (deadForThisTurn) {
            setMoveShortcut(EMPTY, MOVES[EMPTY]);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(COMMAND_FIRE)) {
                possibilities.add(COMMAND_FIRE);
            }
            if (!this.lastMove(THE_KING)) {
                possibilities.add(THE_KING);
            }
            if (!this.lastMove(GIVE_US_REST)) {
                possibilities.add(GIVE_US_REST);
            }
            if (!this.lastMove(THY_WORDS_COME_UNTO_ME)) {
                possibilities.add(THY_WORDS_COME_UNTO_ME);
            }
            if (!this.lastMove(LORD_SHEW_US)) {
                possibilities.add(LORD_SHEW_US);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory);
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new Unkillable(this));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth == 1) {
            for (AbstractPower p : this.powers) {
                p.onDeath();
            }
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                r.onMonsterDeath(this);
            }
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractPower p : powers) {
                        p.onDeath();
                    }
                    deadForThisTurn = true;
                    this.isDone = true;
                }
            });
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    rollMove();
                    createIntent();
                    healthBarUpdatedEvent();
                    this.isDone = true;

                }
            });
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    seraphim.rollMove();
                    seraphim.createIntent();
                    this.isDone = true;
                }
            });
        }
    }

    private void slashUpAnimation(AbstractCreature enemy) {
        animationAction("SlashUp", "ApostleScytheUp", enemy, this);
    }

    private void slashDownAnimation(AbstractCreature enemy) {
        animationAction("SlashDown", "ApostleScytheDown", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


}