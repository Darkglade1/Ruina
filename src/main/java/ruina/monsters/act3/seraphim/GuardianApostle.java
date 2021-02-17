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
    private InvisibleBarricadePower power = new InvisibleBarricadePower(this);

    private static final byte RISE_AND_SERVE = 0;
    private static final byte COMMAND_FIRE = 1;
    private static final byte THE_KING = 2;
    private static final byte GIVE_US_REST = 3;
    private static final byte THY_WORDS_COME_UNTO_ME = 4;
    private static final byte LORD_SHEW_US = 5;
    private static final byte EMPTY = 6;

    private int riseAndServe = calcAscensionTankiness(30);
    private int heavenlyAura = 1;
    private int commandFire = calcAscensionDamage(7);
    private int theKing = calcAscensionDamage(2);
    private int giveUsRestGuardian = calcAscensionTankiness(15);
    private int giveUsRestBoss = calcAscensionTankiness(30);
    private int thyWordsDamage = calcAscensionDamage(5);
    private int thyWordsBlock = calcAscensionTankiness(10);
    private int shewUs = calcAscensionDamage(1);
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
        addMove(COMMAND_FIRE, Intent.ATTACK, commandFire);
        addMove(THE_KING, Intent.ATTACK, theKing, 3, true);
        addMove(GIVE_US_REST, Intent.DEFEND);
        addMove(THY_WORDS_COME_UNTO_ME, Intent.ATTACK_DEFEND, thyWordsDamage);
        addMove(LORD_SHEW_US, Intent.ATTACK, shewUs, 2, true);
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
                atb(new GainBlockAction(this, riseAndServe));
                atb(new ApplyPowerAction(this, this, new RitualPower(this, heavenlyAura, false)));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        firstMove = false;
                        this.isDone = true;
                    }
                });
                break;
            case COMMAND_FIRE:
                dmg(target, info);
                break;
            case THE_KING:
            case LORD_SHEW_US:
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                }
                break;
            case GIVE_US_REST:
                for (AbstractMonster m : monsterList()) {
                    if (m instanceof GuardianApostle) {
                        atb(new GainBlockAction(m, giveUsRestGuardian));
                    } else {
                        atb(new GainBlockAction(m, giveUsRestBoss));
                    }
                }
                break;
            case THY_WORDS_COME_UNTO_ME:
                dmg(target, info);
                atb(new GainBlockAction(this, thyWordsBlock));
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
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new IntentFlashAction(this));
            takeCustomTurn(additionalMove, adp());
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
        atb(new ApplyPowerAction(this, this, new Unkillable(this)));
    }

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


}