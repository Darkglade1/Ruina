package ruina.monsters.uninvitedGuests.greta;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyGainBlockAction;
import ruina.actions.TransferBlockToAllyAction;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.powers.PurpleTearStance;
import ruina.util.AllyMove;
import ruina.vfx.WaitEffect;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.*;

public class Hod extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Hod.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

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
    public Greta greta;

    public static final int SLASH = 1;
    public static final int PIERCE = 2;
    public static final int GUARD = 3;
    private int stance = PIERCE;

    private byte slashMove = SNAKE_SLIT;
    private byte pierceMove = LACERATION;
    private byte guardMove = SERPENTINE_BARRIER;

    public Hod() {
        this(0.0f, 0.0f);
    }

    public Hod(final float x, final float y) {
        super(NAME, ID, 160, -5.0F, 0, 230.0f, 250.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hod/Spriter/Hod.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(SNAKE_SLIT, Intent.ATTACK_BUFF, 7, snakeHits, true);
        addMove(VIOLET_BLADE, Intent.ATTACK, 8, violetHits);
        addMove(LACERATION, Intent.ATTACK_DEBUFF, 12);
        addMove(VENOMOUS_FANGS, Intent.ATTACK_DEBUFF, 14);
        addMove(SERPENTINE_BARRIER, Intent.DEFEND);
        addMove(DUEL, Intent.ATTACK_DEFEND, 17);

//        cardList.add(new Will(this));
//        cardList.add(new BalefulBrand(this));
//        cardList.add(new Faith(this));

        this.allyIcon = makeUIPath("HodIcon.png");
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
                greta = (Greta)mo;
            }
        }
        stancePower = new PurpleTearStance(this, stance);
        applyToTarget(this, this, stancePower);
        super.usePreBattleAction();
        AllyMove changeToSlash = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("defend.png")), DIALOG[3], () -> {
            if (stance == SLASH) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(SLASH);
            }
        });
        changeToSlash.setX(this.intentHb.x - ((30.0F + 32.0f) * Settings.scale));
        changeToSlash.setY(this.intentHb.cY - ((32.0f - 30.0f) * Settings.scale));
        allyMoves.add(changeToSlash);

        AllyMove changeToPierce = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("defend.png")), DIALOG[4], () -> {
            if (stance == PIERCE) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(PIERCE);
            }
        });
        changeToPierce.setX(this.intentHb.x - ((30.0F + 32.0f) * Settings.scale));
        changeToPierce.setY(this.intentHb.cY - ((32.0f - 60.0f) * Settings.scale));
        allyMoves.add(changeToPierce);

        AllyMove changeToGuard = new AllyMove(DIALOG[2], this, new Texture(makeUIPath("defend.png")), DIALOG[5], () -> {
            if (stance == GUARD) {
                atb(new TalkAction(this, DIALOG[6]));
            } else {
                stancePower.changeStance(GUARD);
            }
        });
        changeToGuard.setX(this.intentHb.x - ((30.0F + 32.0f) * Settings.scale));
        changeToGuard.setY(this.intentHb.cY - ((32.0f - 90.0f) * Settings.scale));
        allyMoves.add(changeToGuard);
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target = greta;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (this.nextMove) {
            case SNAKE_SLIT: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
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
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                slashMove = SNAKE_SLIT;
                break;
            }
            case LACERATION: {
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                pierceMove = VENOMOUS_FANGS;
                break;
            }
            case VENOMOUS_FANGS: {
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, false));
                resetIdle();
                pierceMove = LACERATION;
                break;
            }
            case SERPENTINE_BARRIER: {
                block(adp(), BARRIER_BLOCK);
                atb(new AllyGainBlockAction(this, BARRIER_BLOCK));
                resetIdle();
                guardMove = DUEL;
                break;
            }
            case DUEL: {
                block(adp(), DUEL_BLOCK);
                atb(new AllyGainBlockAction(this, DUEL_BLOCK));
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
                setMoveShortcut(VIOLET_BLADE, MOVES[VIOLET_BLADE], cardList.get(VIOLET_BLADE));
            } else {
                setMoveShortcut(SNAKE_SLIT, MOVES[SNAKE_SLIT], cardList.get(SNAKE_SLIT));
            }
        } else if (stance == PIERCE) {
            if (pierceMove == VENOMOUS_FANGS) {
                setMoveShortcut(VENOMOUS_FANGS, MOVES[VENOMOUS_FANGS], cardList.get(VENOMOUS_FANGS));
            } else {
                setMoveShortcut(LACERATION, MOVES[LACERATION], cardList.get(LACERATION));
            }
        } else {
            if (guardMove == DUEL) {
                setMoveShortcut(DUEL, MOVES[DUEL], cardList.get(DUEL));
            } else {
                setMoveShortcut(SERPENTINE_BARRIER, MOVES[SERPENTINE_BARRIER], cardList.get(SERPENTINE_BARRIER));
            }
        }
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1) {
            super.applyPowers();
            return;
        }
        applyPowers(greta);
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

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "YanBrand", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "YanVert", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "YanStab", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}