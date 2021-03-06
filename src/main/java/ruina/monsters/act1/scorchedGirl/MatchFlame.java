package ruina.monsters.act1.scorchedGirl;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class MatchFlame extends AbstractRuinaMonster
{
    public static final String ID = makeID(MatchFlame.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BROKEN_HOPE = 0;
    private static final byte KINDLE = 1;

    private final int BLOCK = calcAscensionTankiness(6);

    public MatchFlame() {
        this(0.0f, 0.0f);
    }

    public MatchFlame(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 155.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("MatchFlame/Spriter/MatchFlame.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(12), calcAscensionTankiness(15));
        addMove(BROKEN_HOPE, Intent.DEFEND);
        addMove(KINDLE, Intent.ATTACK, calcAscensionDamage(5));
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case BROKEN_HOPE: {
                specialAnimation();
                boolean foundGirl = false;
                for (AbstractMonster mo : monsterList()) {
                    if (mo instanceof ScorchedGirl && mo.intent != Intent.ATTACK) {
                        block(mo, BLOCK);
                        foundGirl = true;
                    }
                }
                if (!foundGirl) {
                    block(this, BLOCK);
                }
                resetIdle(1.0f);
                break;
            }
            case KINDLE: {
                attackAnimation(adp());
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
        if (!this.lastMove(BROKEN_HOPE)) {
            possibilities.add(BROKEN_HOPE);
        }
        if (!this.lastTwoMoves(KINDLE)) {
            possibilities.add(KINDLE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "MatchSizzle", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }

}