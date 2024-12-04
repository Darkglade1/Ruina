package ruina.monsters.act1.scorchedGirl;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScorchedGirl extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScorchedGirl.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte EMBER = 0;
    private static final byte EXTINGUISH = 1;

    private final int BURN = calcAscensionSpecial(1);

    public ScorchedGirl() {
        this(0.0f, 0.0f);
    }

    public ScorchedGirl(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 230.0f, 235.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Scorched/Spriter/Scorched.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(35), calcAscensionTankiness(39));
        addMove(EMBER, Intent.DEBUFF);
        addMove(EXTINGUISH, Intent.ATTACK, calcAscensionDamage(22));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case EMBER: {
                specialAnimation(adp());
                intoDrawMo(new Burn(), BURN, this);
                resetIdle(1.0f);
                break;
            }
            case EXTINGUISH: {
                attackAnimation(adp());
                atb(new VFXAction(new ExplosionSmallEffect(this.hb.cX, this.hb.cY), 0.1F));
                dmg(adp(), info);
                atb(new SuicideAction(this));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!lastTwoMoves(EMBER)) {
            setMoveShortcut(EMBER, MOVES[EMBER]);
        } else {
            setMoveShortcut(EXTINGUISH, MOVES[EXTINGUISH]);
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Special", "MatchExplode", enemy, this);
    }

    private void specialAnimation(AbstractCreature enemy) {
        animationAction("Move", "MatchSizzle", enemy, this);
    }

}