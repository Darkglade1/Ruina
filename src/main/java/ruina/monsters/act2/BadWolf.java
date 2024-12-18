package ruina.monsters.act2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.util.DetailedIntent;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class BadWolf extends AbstractRuinaMonster
{
    public static final String ID = makeID(BadWolf.class.getSimpleName());

    private static final byte CLAW = 0;
    private static final byte BITE = 1;
    private static final byte HUNT = 2;

    public static final float HP_THRESHOLD = 0.5f;
    private final int SKULK_TURNS = calcAscensionSpecial(1);
    private final int BLEED = calcAscensionSpecial(2);
    private final int STRENGTH = calcAscensionSpecial(4);
    private int phase = 1;
    boolean triggered = false;

    public static final String HUNTER_POWER_ID = makeID("Hunter");
    public static final PowerStrings HUNTERPowerStrings = CardCrawlGame.languagePack.getPowerStrings(HUNTER_POWER_ID);
    public static final String HUNTER_POWER_NAME = HUNTERPowerStrings.NAME;
    public static final String[] HUNTER_POWER_DESCRIPTIONS = HUNTERPowerStrings.DESCRIPTIONS;

    public static final String SKULK_POWER_ID = makeID("Skulk");
    public static final PowerStrings SKULKPowerStrings = CardCrawlGame.languagePack.getPowerStrings(SKULK_POWER_ID);
    public static final String SKULK_POWER_NAME = SKULKPowerStrings.NAME;
    public static final String[] SKULK_POWER_DESCRIPTIONS = SKULKPowerStrings.DESCRIPTIONS;

    public BadWolf() {
        this(0.0f, 0.0f);
    }

    public BadWolf(final float x, final float y) {
        super(ID, ID, 40, -5.0F, 0, 300.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("BadWolf/Spriter/BadWolf.scml"));
        setHp(calcAscensionTankiness(80), calcAscensionTankiness(84));
        addMove(CLAW, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BITE, Intent.ATTACK_BUFF, calcAscensionDamage(14));
        addMove(HUNT, Intent.ATTACK, calcAscensionDamage(18));
    }

    @Override
    public void usePreBattleAction() {
        playSound("WolfPhase");
        applyToTarget(this, this, new AbstractLambdaPower(HUNTER_POWER_NAME, HUNTER_POWER_ID, AbstractPower.PowerType.BUFF, false, this, SKULK_TURNS) {
            @Override
            public void atEndOfRound() {
                if (owner instanceof BadWolf) {
                    ((BadWolf) owner).checkSkulkTrigger();
                }
            }

            @Override
            public void updateDescription() {
                if (amount == 1) {
                    description = HUNTER_POWER_DESCRIPTIONS[0] + (int)(HP_THRESHOLD * 100) + HUNTER_POWER_DESCRIPTIONS[1] + STRENGTH + HUNTER_POWER_DESCRIPTIONS[2] + amount + HUNTER_POWER_DESCRIPTIONS[4];
                } else {
                    description = HUNTER_POWER_DESCRIPTIONS[0] + (int)(HP_THRESHOLD * 100) + HUNTER_POWER_DESCRIPTIONS[1] + STRENGTH + HUNTER_POWER_DESCRIPTIONS[2] + amount + HUNTER_POWER_DESCRIPTIONS[3];
                }
            }
        });
    }

    @Override
    public void takeTurn() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                halfDead = false;
                this.isDone = true;
            }
        });
        super.takeTurn();
        switch (this.nextMove) {
            case CLAW: {
                slashAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Bleed(adp(), BLEED));
                resetIdle();
                break;
            }
            case BITE: {
                biteAnimation(adp());
                atb(new VampireDamageActionButItCanFizzle(adp(), info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case HUNT: {
                slashAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (triggered) {
            setMoveShortcut(HUNT);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(CLAW)) {
                possibilities.add(CLAW);
            }
            if (!this.lastMove(BITE)) {
                possibilities.add(BITE);
            }
            if (!this.lastMove(HUNT)) {
                possibilities.add(HUNT);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CLAW: {
                DetailedIntent detail = new DetailedIntent(this, BLEED, DetailedIntent.BLEED_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case BITE: {
                DetailedIntent detail = new DetailedIntent(this, DetailedIntent.LIFESTEAL);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    public void changePhase(int phase) {
        this.phase = phase;
        if (phase == 2) {
            playSound("Fog", 1.2f);
        }
        runAnim("Idle" + phase);
    }

    private void biteAnimation(AbstractCreature enemy) {
        animationAction("Bite" + phase, "Bite", enemy, this);
    }

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash" + phase, "Claw", enemy, this);
    }

    @Override
    public void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                runAnim("Idle" + phase);
                this.isDone = true;
            }
        });
    }

    @Override
    public void damage(DamageInfo info) {
        if (this.hasPower(SKULK_POWER_ID)) {
            return;
        }
        super.damage(info);
    }

    public void checkSkulkTrigger() {
        if (this.currentHealth < (int)(this.maxHealth * HP_THRESHOLD) && !triggered) {
            triggered = true;
            applyToTarget(this, this, new StrengthPower(this, STRENGTH));
            applyToTarget(this, this, new AbstractLambdaPower(SKULK_POWER_NAME, SKULK_POWER_ID, AbstractPower.PowerType.BUFF, false, this, SKULK_TURNS) {

                @Override
                public void onInitialApplication() {
                    att(new AbstractGameAction() {
                        @Override
                        public void update() {
                            halfDead = true;
                            this.isDone = true;
                        }
                    });
                    if (owner instanceof BadWolf) {
                        ((BadWolf) owner).changePhase(2);
                        ((BadWolf) owner).rollMove();
                        ((BadWolf) owner).createIntent();
                    }
                }

                @Override
                public void onRemove() {
                    atb(new AbstractGameAction() {
                        @Override
                        public void update() {
                            halfDead = false;
                            this.isDone = true;
                        }
                    });
                    if (owner instanceof BadWolf) {
                        ((BadWolf) owner).changePhase(1);
                    }
                }

                @Override
                public void atEndOfRound() {
                    if (amount == 1) {
                        makePowerRemovable(owner, SKULK_POWER_ID);
                    }
                    atb(new ReducePowerAction(owner, owner, this, 1));
                }

                @Override
                public void updateDescription() {
                    if (amount == 1) {
                        description = SKULK_POWER_DESCRIPTIONS[0] + amount + SKULK_POWER_DESCRIPTIONS[2];
                    } else {
                        description = SKULK_POWER_DESCRIPTIONS[0] + amount + SKULK_POWER_DESCRIPTIONS[1];
                    }
                }
            });
        }
    }

    @Override
    public void renderReticle(SpriteBatch sb) {
        if (!this.hasPower(SKULK_POWER_ID)) {
            super.renderReticle(sb);
        }
    }

}