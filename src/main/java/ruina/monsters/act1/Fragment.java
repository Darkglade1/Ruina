package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.cards.Enlightenment;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Fragment extends AbstractRuinaMonster
{
    public static final String ID = makeID(Fragment.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PENETRATE = 0;
    private static final byte ECHOES = 1;

    private final int STATUS = 2;
    AbstractCard status = new Enlightenment();

    public Fragment() {
        this(0.0f, 0.0f);
    }

    public Fragment(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Fragment/Spriter/Fragment.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(46), calcAscensionTankiness(52));
        addMove(PENETRATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(ECHOES, Intent.ATTACK, calcAscensionDamage(12));

        if (AbstractDungeon.ascensionLevel >= 17) {
            status.upgrade();
        }
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case PENETRATE: {
                attackAnimation(adp());
                dmg(adp(), info);
                intoDiscardMo(status.makeStatEquivalentCopy(), STATUS, this);
                resetIdle();
                break;
            }
            case ECHOES: {
                specialAnimation(adp());
                dmg(adp(), info);
                resetIdle(1.0f);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(PENETRATE)) {
            setMoveShortcut(ECHOES, MOVES[ECHOES]);
        } else {
            setMoveShortcut(PENETRATE, MOVES[PENETRATE]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "FragmentStab", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Special", "FragmentSing", enemy, this);
    }

}