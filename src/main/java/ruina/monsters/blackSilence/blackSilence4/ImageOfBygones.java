package ruina.monsters.blackSilence.blackSilence4;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.GenericStrengthUpPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ImageOfBygones extends AbstractRuinaMonster
{
    public static final String ID = makeID(ImageOfBygones.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte AGONY = 0;
    private static final byte SCREAM = 1;

    private final int STRENGTH = calcAscensionSpecial(3);
    private final BlackSilence4 parent;

    public ImageOfBygones() {
        this(0.0f, 0.0f, null);
    }

    public ImageOfBygones(final float x, final float y, BlackSilence4 parent) {
        super(NAME, ID, 100, -15.0F, 0, 230.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BlackSilence4/Spriter/BlackSilence4.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(132), calcAscensionTankiness(143));
        addMove(AGONY, Intent.ATTACK, calcAscensionDamage(20));
        addMove(SCREAM, Intent.ATTACK, calcAscensionDamage(9), 2, true);
        this.parent = parent;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new GenericStrengthUpPower(this, MOVES[2], STRENGTH));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        AbstractCreature target = adp();

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case AGONY: {
                int animation = AbstractDungeon.monsterRng.random(2);
                switch (animation) {
                    case 0:
                        wheelsAnimation(target);
                        break;
                    case 1:
                        claw1Animation(target);
                        break;
                    case 2:
                        club1Animation(target);
                        break;
                }
                dmg(target, info);
                resetIdle();
                break;
            }
            case SCREAM: {
                int animation = AbstractDungeon.monsterRng.random(2);
                for (int i = 0; i < multiplier; i++) {
                    switch (animation) {
                        case 0:
                            if (i % 2 == 0) {
                                pierceAnimation(target);
                            } else {
                                attackAnimation(target);
                            }
                            break;
                        case 1:
                            if (i % 2 == 0) {
                                gun1Animation(target);
                            } else {
                                gun3Animation(target);
                            }
                            break;
                        case 2:
                            if (i % 2 == 0) {
                                slashAnimation(target);
                            } else {
                                sword1Animation(target);
                            }
                            break;
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        parent.minion = null;
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(AGONY)) {
            possibilities.add(AGONY);
        }
        if (!this.lastTwoMoves(SCREAM)) {
            possibilities.add(SCREAM);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "RolandAxe", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "RolandAxe", enemy, this);
    }

    private void gun1Animation(AbstractCreature enemy) {
        animationAction("Gun1", "RolandRevolver", enemy, this);
    }

    private void gun3Animation(AbstractCreature enemy) {
        animationAction("Gun3", "RolandShotgun", enemy, this);
    }

    private void claw1Animation(AbstractCreature enemy) {
        animationAction("Claw1", "SwordStab", enemy, this);
    }

    private void club1Animation(AbstractCreature enemy) {
        animationAction("Club1", "BluntVert", enemy, this);
    }

    private void wheelsAnimation(AbstractCreature enemy) {
        animationAction("Wheels", "RolandGreatSword", enemy, this);
    }

    private void sword1Animation(AbstractCreature enemy) {
        animationAction("Sword1", "RolandDuralandalDown", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "RolandDualSword", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

}