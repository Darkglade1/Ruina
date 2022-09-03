package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Paralysis;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SanguineBat extends AbstractRuinaMonster
{
    public static final String ID = makeID(SanguineBat.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte BLOODSUCKING = 0;
    private static final byte DIGGING_TEETH = 1;
    private static final byte AVID_THIRST = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);

    public SanguineBat() {
        this(0.0f, 0.0f);
    }

    public SanguineBat(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 50.0f, 230.0f, 155.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bat/Spriter/Bat.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(31), calcAscensionTankiness(37));
        addMove(BLOODSUCKING, Intent.ATTACK_BUFF, calcAscensionDamage(5), 2, true);
        addMove(DIGGING_TEETH, Intent.ATTACK_DEBUFF, calcAscensionDamage(7));
        addMove(AVID_THIRST, Intent.BUFF);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (this.firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case BLOODSUCKING: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case DIGGING_TEETH: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), DEBUFF));
                resetIdle();
                break;
            }
            case AVID_THIRST: {
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BLOODSUCKING) && !firstMove) {
            possibilities.add(BLOODSUCKING);
        }
        if (!this.lastMove(DIGGING_TEETH)) {
            possibilities.add(DIGGING_TEETH);
        }
        if (!this.lastMove(AVID_THIRST) && !this.lastMoveBefore(AVID_THIRST)) {
            possibilities.add(AVID_THIRST);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BatAttack", 0.5f, enemy, this);
    }

}