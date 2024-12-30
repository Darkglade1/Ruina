package ruina.monsters.uninvitedGuests.normal.eileen;

import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractAllyAttackingMinion;
import ruina.powers.InvisibleBarricadePower;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class GearsWorshipper extends AbstractAllyAttackingMinion
{
    public static final String ID = makeID(GearsWorshipper.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte VAPOR = 0;
    private Eileen eileen;

    public GearsWorshipper(final float x, final float y) {
        super(NAME, ID, 40, -5.0F, 0, 100.0f, 205.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("GearsWorshipper/Spriter/GearsWorshipper.scml"));
        this.type = EnemyType.BOSS;
        setHp(calcAscensionTankiness(132), calcAscensionTankiness(144));
        addMove(VAPOR, Intent.ATTACK, calcAscensionDamage(12));
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Eileen) {
                eileen = (Eileen) mo;
            }
            if (mo instanceof Yesod) {
                this.target = (Yesod)mo;
            }
        }
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        createIntent();
        addPower(new MinionPower(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RemoveAllBlockAction(this, this));
        AbstractCreature target;
        if (!this.target.isDead && !this.target.isDying && attackingAlly) {
            target = this.target;
        } else {
            target = adp();
        }

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case VAPOR: {
                slashAnimation(target);
                dmg(target, info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(VAPOR);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "GearVert", enemy, this);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        eileen.onMinionDeath();
        for (int i = 0; i < eileen.minions.length; i++) {
            if (eileen.minions[i] == this) {
                eileen.minions[i] = null;
                break;
            }
        }
    }

}