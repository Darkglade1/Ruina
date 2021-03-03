package ruina.monsters.act3.heart;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class LungsOfCraving extends AbstractRuinaMonster
{
    public static final String ID = makeID(LungsOfCraving.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte FERVENT_BEATS = 0;
    private static final byte RETRACTING_BEATS = 1;

    private final int BLOCK = calcAscensionTankiness(5);

    public LungsOfCraving() {
        this(0.0f, 0.0f);
    }

    public LungsOfCraving(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 280.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Lungs/Spriter/Lungs.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(42), calcAscensionTankiness(46));
        addMove(FERVENT_BEATS, Intent.ATTACK, calcAscensionDamage(6), 2, true);
        addMove(RETRACTING_BEATS, Intent.ATTACK_DEFEND, calcAscensionDamage(10));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case FERVENT_BEATS: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case RETRACTING_BEATS: {
                attackAnimation(adp());
                block(this, BLOCK);
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastTwoMoves(FERVENT_BEATS)) {
            possibilities.add(FERVENT_BEATS);
        }
        if (!this.lastTwoMoves(RETRACTING_BEATS)) {
            possibilities.add(RETRACTING_BEATS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "WoodStrike", enemy, this);
    }

}