package ruina.monsters.act2;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class MeltedCorpses extends AbstractRuinaMonster
{
    public static final String ID = makeID(MeltedCorpses.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;

    private static final byte NONE = 0;

    public MeltedCorpses() {
        this(0.0f, 0.0f);
    }

    public MeltedCorpses(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 0, 230.0f, 225.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Corpse/Spriter/Corpse.scml"));
        this.type = EnemyType.NORMAL;
        addMove(NONE, Intent.NONE);
    }

    @Override
    public void usePreBattleAction() {

    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(NONE);
    }
}