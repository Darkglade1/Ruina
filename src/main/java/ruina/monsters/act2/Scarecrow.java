package ruina.monsters.act2;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.BetterSpriterAnimation;
import ruina.actions.WisdomAction;
import ruina.cards.Wisdom;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.act2.Search;
import ruina.util.DetailedIntent;
import ruina.util.TexLoader;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.util.Wiz.*;

public class Scarecrow extends AbstractRuinaMonster
{
    public static final String ID = makeID(Scarecrow.class.getSimpleName());

    private static final byte RAKE = 0;
    private static final byte HARVEST = 1;
    private static final byte STRUGGLE = 2;

    private static final int STRUGGLE_THRESHOLD = 2;
    private int struggleCounter;

    private final int WISDOM_AMT = calcAscensionSpecial(2);

    public Scarecrow() {
        this(0.0f, 0.0f, STRUGGLE_THRESHOLD);
    }

    public Scarecrow(final float x, final float y, int struggleCounter) {
        super(ID, ID, 40, -5.0F, 0, 230.0f, 275.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Scarecrow/Spriter/Scarecrow.scml"));
        setHp(calcAscensionTankiness(48), calcAscensionTankiness(54));
        addMove(RAKE, Intent.ATTACK, calcAscensionDamage(12));
        addMove(HARVEST, Intent.ATTACK, calcAscensionDamage(3), 3, true);
        addMove(STRUGGLE, Intent.DEBUFF);
        this.struggleCounter = struggleCounter;
    }

    @Override
    public void usePreBattleAction() {
        atb(new WisdomAction());
        applyToTarget(this, this, new Search(this));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case RAKE: {
                rakeAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
            case HARVEST: {
                for (int i = 0; i < multiplier; i++) {
                    harvestAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case STRUGGLE: {
                intoDiscardMo(new Wisdom(), WISDOM_AMT, this);
                struggleCounter = STRUGGLE_THRESHOLD + 1;
                break;
            }
        }
        struggleCounter--;
        //In case it gets stunned by Wisdom
        AbstractMonster mo = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                atb(new RollMoveAction(mo));
                this.isDone = true;
            }
        });

    }

    @Override
    protected void getMove(final int num) {
        if (this.hasPower(StunMonsterPower.POWER_ID)) {
            return;
        }
        if (struggleCounter <= 0) {
            setMoveShortcut(STRUGGLE);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(RAKE)) {
                possibilities.add(RAKE);
            }
            if (!this.lastMove(HARVEST)) {
                possibilities.add(HARVEST);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        String textureString = makeUIPath("detailedIntents/Wisdom.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case STRUGGLE: {
                DetailedIntent detail = new DetailedIntent(this, WISDOM_AMT, texture, DetailedIntent.TargetType.DISCARD_PILE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        playSound("ScarecrowDeath", 0.5f);
    }

    private void rakeAnimation(AbstractCreature enemy) {
        animationAction("Attack1", "Rake", enemy, this);
    }

    private void harvestAnimation(AbstractCreature enemy) {
        animationAction("Attack2", "Harvest", 0.3f, enemy, this);
    }

}