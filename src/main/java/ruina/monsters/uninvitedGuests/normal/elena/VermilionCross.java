package ruina.monsters.uninvitedGuests.normal.elena;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.elena.vermilionCards.*;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class VermilionCross extends AbstractCardMonster
{
    public static final String ID = makeID(VermilionCross.class.getSimpleName());

    private static final byte OBSTRUCT = 0;
    private static final byte SHOCKWAVE = 1;
    private static final byte HEATED_WEAPON = 2;
    private static final byte RAMPAGE = 3;
    private static final byte HEAT_UP = 4;

    public final int heatedWeaponHits = 2;

    public final int OBSTRUCT_BLOCK = calcAscensionTankiness(50);
    public final int HEAT_UP_BLOCK = calcAscensionTankiness(10);
    public final int STRENGTH = calcAscensionSpecial(5);
    public final int BURNS = calcAscensionSpecial(1);
    public final int allyIntangible = calcAscensionSpecial(1);
    public Elena elena;
    public boolean hadBlock;

    public VermilionCross() {
        this(0.0f, 0.0f);
    }

    public VermilionCross(final float x, final float y) {
        super(ID, ID, 600, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Vermilion/Spriter/Vermilion.scml"));
        setNumAdditionalMoves(1);
        this.setHp(calcAscensionTankiness(600));

        addMove(OBSTRUCT, Intent.DEFEND);
        addMove(SHOCKWAVE, Intent.ATTACK_BUFF, calcAscensionDamage(16));
        addMove(HEATED_WEAPON, Intent.ATTACK_DEBUFF, calcAscensionDamage(7), heatedWeaponHits);
        addMove(RAMPAGE, Intent.ATTACK, calcAscensionDamage(45));
        addMove(HEAT_UP, Intent.DEFEND_BUFF);

        cardList.add(new Obstruct(this));
        cardList.add(new Shockwave(this));
        cardList.add(new HeatedWeapon(this));
        cardList.add(new Rampage(this));
        cardList.add(new HeatUp(this));

        this.icon = TexLoader.getTexture(makeUIPath("VermilionIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Binah) {
                target = (Binah)mo;
            }
            if (mo instanceof Elena) {
                elena = (Elena)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case OBSTRUCT: {
                blockAnimation();
                block(this, OBSTRUCT_BLOCK);
                resetIdle();
                break;
            }
            case SHOCKWAVE: {
                slashAnimation(target);
                dmg(target, info);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (hadBlock) {
                            AbstractCreature powerTarget = elena;
                            if (elena.isDeadOrEscaped()) {
                                powerTarget = VermilionCross.this;
                            }
                            applyToTargetTop(powerTarget, VermilionCross.this, new IntangiblePlayerPower(powerTarget, allyIntangible + 1));
                        }
                        this.isDone = true;
                    }
                });
                resetIdle();
                break;
            }
            case HEATED_WEAPON: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        slashAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                intoDrawMo(new Burn(), BURNS, this);
                break;
            }
            case RAMPAGE: {
                strongAttackAnimation(target);
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case HEAT_UP: {
                blockAnimation();
                block(this, HEAT_UP_BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireHori", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "FireVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", "FireGuard", this);
    }

    private void strongAttackAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "FireStrong", enemy, this);
    }


    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(OBSTRUCT)) {
            setMoveShortcut(SHOCKWAVE, MOVES[SHOCKWAVE], cardList.get(SHOCKWAVE).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (moveHistory.size() >= 3) {
                moveHistory.clear(); //resets the cooldowns after all moves have been used once
            }
            if (!this.lastMove(OBSTRUCT) && !this.lastMoveBefore(OBSTRUCT)) {
                possibilities.add(OBSTRUCT);
            }
            if (!this.lastMove(HEATED_WEAPON) && !this.lastMoveBefore(HEATED_WEAPON)) {
                possibilities.add(HEATED_WEAPON);
            }
            if (possibilities.isEmpty()) {
                possibilities.add(HEATED_WEAPON);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (moveHistory.size() >= 3) {
            moveHistory.clear(); //resets the cooldowns after all moves have been used once
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(RAMPAGE, moveHistory) && !this.lastMoveBefore(RAMPAGE, moveHistory)) {
            possibilities.add(RAMPAGE);
        }
        if (!this.lastMove(HEATED_WEAPON, moveHistory) && !this.lastMoveBefore(HEATED_WEAPON, moveHistory)) {
            possibilities.add(HEATED_WEAPON);
        }
        if (!this.lastMove(HEAT_UP, moveHistory) && !this.lastMoveBefore(HEAT_UP, moveHistory)) {
            possibilities.add(HEAT_UP);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        elena.onVermilionDeath();
        target.target = elena;
        if (elena.isDeadOrEscaped()) {
            if (target instanceof Binah) {
                ((Binah) target).onBossDeath();
            }
        }
    }

}