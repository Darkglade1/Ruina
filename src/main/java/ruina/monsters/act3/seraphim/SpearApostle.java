package ruina.monsters.act3.seraphim;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;
import ruina.powers.WingsOfGrace;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class SpearApostle extends AbstractRuinaMonster {
    public static final String ID = makeID(SpearApostle.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte FOR_HE_IS_HOLY = 0;
    private static final byte THE_WILL_OF_THE_LORD_BE_DONE = 1;

    private final int debuff = calcAscensionSpecial(3);

    private final Prophet prophet;

    public SpearApostle(final float x, final float y, Prophet parent) {
        super(NAME, ID, 50, -5.0F, 0, 160.0f, 185.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("SpearApostle/Spriter/SpearApostle.scml"));
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(38), calcAscensionTankiness(44));
        addMove(FOR_HE_IS_HOLY, Intent.ATTACK, calcAscensionDamage(22));
        addMove(THE_WILL_OF_THE_LORD_BE_DONE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6));
        prophet = parent;
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;
        if (info.base > -1) {
            info.applyPowers(this, adp());
        }
        switch (nextMove) {
            case FOR_HE_IS_HOLY:
                spearAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        addMove(FOR_HE_IS_HOLY, Intent.ATTACK, calcAscensionDamage(10)); //lowers its subsequent damage
                        this.isDone = true;
                    }
                });
                break;
            case THE_WILL_OF_THE_LORD_BE_DONE:
                spearAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                applyToTarget(adp(), this, new Bleed(adp(), debuff));
                break;
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(FOR_HE_IS_HOLY)) {
            setMoveShortcut(THE_WILL_OF_THE_LORD_BE_DONE, MOVES[THE_WILL_OF_THE_LORD_BE_DONE]);
        } else {
            setMoveShortcut(FOR_HE_IS_HOLY, MOVES[FOR_HE_IS_HOLY]);
        }
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "ApostleSpear", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Block", null, this);
    }


    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        prophet.incrementApostleKills();
        for (int i = 0; i < prophet.minions.length; i++) {
            if (prophet.minions[i] == this) {
                prophet.minions[i] = null;
                break;
            }
        }
    }

    @Override
    public void usePreBattleAction() {
        atb(new ApplyPowerAction(this, this, new WingsOfGrace(this, calcAscensionSpecial(1))));
        createIntent();
    }
}