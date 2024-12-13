package ruina.monsters.act2.roadHome;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;
import ruina.monsters.AbstractAllyMonster;
import ruina.powers.act2.WayHome;
import ruina.util.TexLoader;

import static ruina.RuinaMod.makeMonsterPath;
import static ruina.RuinaMod.makeUIPath;
import static ruina.util.Wiz.applyToTarget;

public class HomeAlly extends AbstractAllyMonster
{
    public static final String ID = RuinaMod.makeID(HomeAlly.class.getSimpleName());

    private static final byte NONE = 0;
    public RoadHome roadHome;

    public HomeAlly() {
        this(0.0f, 0.0f);
    }

    public HomeAlly(final float x, final float y) {
        super(ID, ID, 50, -5.0F, 0, 300.0f, 300.0f, makeMonsterPath("RoadHome/HouseSmall.png"), x, y);
        this.setHp(calcAscensionTankiness(50));

        addMove(NONE, Intent.NONE);

        this.icon = TexLoader.getTexture(makeUIPath("HomeIcon.png"));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof RoadHome) {
                roadHome = (RoadHome)mo;
            }
        }
        applyToTarget(this, this, new WayHome(this));
        super.usePreBattleAction();
    }

    public void OnRoadDeath() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                disappear();
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        setMoveShortcut(NONE);
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
            roadHome.homeDeath();
        }
    }
}