package ruina.monsters.act2;

import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SetMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.Bleed;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ScaredyCat extends AbstractRuinaMonster
{
    public static final String ID = makeID(ScaredyCat.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte RAWR = 0;
    private static final byte GROWL = 1;
    private static final byte FLEE = 2;

    private RoadHome road;

    private final int BLEED = calcAscensionSpecial(2);

    public ScaredyCat() {
        this(0.0f, 0.0f, null);
    }

    public ScaredyCat(final float x, final float y, RoadHome road) {
        super(NAME, ID, 120, -5.0F, 0, 270.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScaredyCat/Spriter/ScaredyCat.scml"));
        this.type = EnemyType.ELITE;
        this.road = road;
        setHp(calcAscensionTankiness(maxHealth));
        addMove(RAWR, Intent.ATTACK, calcAscensionDamage(8), 2, true);
        addMove(GROWL, Intent.ATTACK_DEBUFF, calcAscensionDamage(13));
        addMove(FLEE, Intent.ESCAPE);
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        if (this.firstMove) {
            firstMove = false;
        }

        switch (this.nextMove) {
            case RAWR: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        attack1Animation(adp());
                    } else {
                        attack2Animation(adp());
                    }
                    dmg(adp(), info);
                    resetIdle();
                }
                break;
            }
            case GROWL: {
                poisonAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
            case FLEE: {
                animation.setFlip(true, false);
                atb(new EscapeAction(this));
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (road.isDeadOrEscaped()) {
            setMoveShortcut(FLEE);
        } else {
            if (this.lastMove(RAWR)) {
                setMoveShortcut(GROWL, MOVES[GROWL]);
            } else {
                setMoveShortcut(RAWR, MOVES[RAWR]);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        if (!road.isDeadOrEscaped()) {
            road.catDeath();
        }
    }

    public void roadDeath() {
        runAnim("Scared");
        playSound("LionChange");
        this.currentHealth = this.maxHealth = 1;
        healthBarUpdatedEvent();
        applyToTarget(this, this, new StrengthPower(this, -99));
        setMoveShortcut(FLEE);
        createIntent();
        atb(new SetMoveAction(this, FLEE, Intent.ESCAPE));
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "Claw", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "WoodStrike", enemy, this);
    }

    private void poisonAnimation(AbstractCreature enemy) {
        animationAction("Poison", "LionPoison", this);
    }

}