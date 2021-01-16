package ruina.monsters.act2.Jester;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class Statue extends AbstractRuinaMonster
{
    public static final String ID = makeID(Statue.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    protected AbstractMagicalGirl magicalGirl;
    protected  JesterOfNihil jester;
    private static final byte NONE = 0;

    public Statue(final float x, final float y, JesterOfNihil jester, AbstractMagicalGirl girl) {
        super(NAME, ID, 10, -5.0F, 0, 200.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Statue/Spriter/Statue.scml"));
        this.type = EnemyType.NORMAL;
        setHp(10);
        addMove(NONE, Intent.NONE);
        this.jester = jester;
        this.magicalGirl = girl;
        if (girl instanceof QueenOfLove) {
            name = MOVES[0];
            runAnim("Hate");
        }
        if (girl instanceof ServantOfCourage) {
            name = MOVES[1];
            runAnim("Wrath");
        }
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMove(NONE, Intent.NONE);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        jester.SummonGirl(magicalGirl);
    }
}