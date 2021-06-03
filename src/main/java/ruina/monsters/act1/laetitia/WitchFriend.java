package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WitchFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(WitchFriend.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte GLITCH = 0;

    private final int glitchDamage = calcAscensionDamage(15);

    public static final String POWER_ID = makeID("LonelyIsSad");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Laetitia parent;

    public WitchFriend(final float x, final float y, Laetitia elite) {
        super(NAME, ID, 140, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WeeWitch/Spriter/WeeWitch.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(15));
        addMove(GLITCH, Intent.ATTACK, glitchDamage);
        parent = elite;
        firstMove = true;
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {

    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        parent.onMonsterDeath();
    }

    @Override
    public void takeTurn() {
        DamageInfo info;
        int multiplier;
        if (moves.containsKey(nextMove)) {
            info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = this.moves.get(nextMove).multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (this.nextMove) {
            case GLITCH: {
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
        setMoveShortcut(GLITCH, MOVES[GLITCH]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaFriendAtk", enemy, this);
    }

}