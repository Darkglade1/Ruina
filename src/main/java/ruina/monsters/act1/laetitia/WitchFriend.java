package ruina.monsters.act1.laetitia;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.BetterSpriterAnimation;
import ruina.cards.Gift;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class WitchFriend extends AbstractRuinaMonster
{
    public static final String ID = makeID(WitchFriend.class.getSimpleName());
    private static final byte GLITCH = 0;

    public static final String POWER_ID = makeID("Gimme");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public WitchFriend(final float x, final float y) {
        super(ID, ID, 15, 0.0F, 0, 220.0f, 200.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("WeeWitch/Spriter/WeeWitch.scml"));
        setHp(calcAscensionTankiness(10), calcAscensionTankiness(12));
        addMove(GLITCH, Intent.ATTACK, calcAscensionDamage(9));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }


    public void takeTurn() {
        super.takeTurn();
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
        setMoveShortcut(GLITCH);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "LaetitiaFriendAtk", enemy, this);
    }

}