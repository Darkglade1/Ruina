package ruina.monsters.act2.jester;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.act2.PointlessHate;
import ruina.powers.act2.SenselessWrath;
import ruina.util.AdditionalIntent;
import ruina.util.DetailedIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class JesterOfNihil extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(JesterOfNihil.class.getSimpleName());

    private static final byte WILL_OF_NIHIL = 0;
    private static final byte CONSUMING_DESIRE = 1;
    private static final byte LOVE_AND_HATE = 2;
    private static final byte SWORD_OF_TEARS = 3;
    private static final byte RAMPAGE = 4;
    private static final byte SETUP = 5;

    private final int HATE_BLOCK = calcAscensionTankiness(20);
    private final int ATTACK_BLOCK = calcAscensionTankiness(6);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);
    private static final int RAMPAGE_COOLDOWN = 1;
    private boolean girl1Spawned = false;
    private boolean girl2Spawned = false;
    private AbstractMagicalGirl girl1;
    private AbstractMagicalGirl girl2;
    private int numIntentThatCanRampage = 2; //0 is the second intent, 1 is the third intent, 2 is the first intent
    private int rampageCooldown = RAMPAGE_COOLDOWN;

    public JesterOfNihil() {
        this(100.0f, 0.0f);
    }

    public JesterOfNihil(final float x, final float y) {
        super(ID, ID, 600, -5.0F, 0, 280.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jester/Spriter/Jester.scml"));
        setNumAdditionalMoves(2);
        this.setHp(calcAscensionTankiness(600));

        addMove(WILL_OF_NIHIL, IntentEnums.MASS_ATTACK, calcAscensionDamage(28));
        addMove(CONSUMING_DESIRE, Intent.ATTACK_DEFEND, calcAscensionDamage(6), 2);
        addMove(LOVE_AND_HATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(SWORD_OF_TEARS, Intent.ATTACK, calcAscensionDamage(9), 2);
        addMove(RAMPAGE, Intent.BUFF);
        addMove(SETUP, Intent.UNKNOWN);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case WILL_OF_NIHIL: {
                atb(new SFXAction("MONSTER_COLLECTOR_DEBUFF"));
                atb(new VFXAction(new CollectorCurseEffect(adp().hb.cX, adp().hb.cY)));
                for (int i = 0; i < monsterList().size(); i++) {
                    AbstractMonster mo = monsterList().get(i);
                    if (i == monsterList().size() - 2) {
                        //makes the special effects appear all at once for multiple monsters instead of one-by-one
                        atb(new VFXAction(new CollectorCurseEffect(mo.hb.cX, mo.hb.cY), 2.0F));
                    } else if (mo != this) {
                        atb(new VFXAction(new CollectorCurseEffect(mo.hb.cX, mo.hb.cY)));
                    }
                }
                atb(new DamageAllOtherCharactersAction(this, calcMassAttack(info), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case CONSUMING_DESIRE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        bluntAnimation(target);
                    } else {
                        slamAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                block(this, ATTACK_BLOCK);
                break;
            }
            case LOVE_AND_HATE: {
                hateAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                resetIdle();
                break;
            }
            case SWORD_OF_TEARS: {
                for (int i = 0; i < multiplier; i++) {
                    pierceAnimation(target);
                    dmg(target, info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
            case RAMPAGE: {
                slamAnimation(target);
                applyToTargetNextTurn(this, new StrengthPower(this, STRENGTH));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        rampageCooldown = RAMPAGE_COOLDOWN + 1;
                        numIntentThatCanRampage = (numIntentThatCanRampage + 1) % 3;
                        this.isDone = true;
                    }
                });
                resetIdle(1.0f);
                break;
            }
            case SETUP: {
                applyToTarget(this, this, new PointlessHate(this, HATE_BLOCK));
                applyToTarget(this, this, new SenselessWrath(this));
                break;
            }
        }
    }

    private void hateAnimation(AbstractCreature enemy) {
        animationAction("Hate", "MagicGun", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "KnightAttack", enemy, this);
    }

    private void slamAnimation(AbstractCreature enemy) {
        animationAction("Slash", "GreedSlam", enemy, this);
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "GreedBlunt", enemy, this);
    }

    @Override
    public void takeTurn() {
        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
        }
        super.takeTurn();
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                rampageCooldown--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(SETUP);
        } else if (girl1Spawned && girl2Spawned &&(!girl1.isDead || !girl2.isDead) && threeTurnCooldownHasPassedForMove(WILL_OF_NIHIL)) {
            setMoveShortcut(WILL_OF_NIHIL);
        } else if (numIntentThatCanRampage == 2 && rampageCooldown <= 0) {
            setMoveShortcut(RAMPAGE);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(CONSUMING_DESIRE)) {
                possibilities.add(CONSUMING_DESIRE);
            }
            if (!this.lastMove(LOVE_AND_HATE)) {
                possibilities.add(LOVE_AND_HATE);
            }
            if (!this.lastMove(SWORD_OF_TEARS)) {
                possibilities.add(SWORD_OF_TEARS);
            }
            byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (!firstMove) {
            if (numIntentThatCanRampage == whichMove && rampageCooldown <= 0) {
                setAdditionalMoveShortcut(RAMPAGE, moveHistory);
            } else {
                ArrayList<Byte> possibilities = new ArrayList<>();
                if (!this.lastMove(CONSUMING_DESIRE, moveHistory)) {
                    possibilities.add(CONSUMING_DESIRE);
                }
                if (!this.lastMove(LOVE_AND_HATE, moveHistory)) {
                    possibilities.add(LOVE_AND_HATE);
                }
                if (!this.lastMove(SWORD_OF_TEARS, moveHistory)) {
                    possibilities.add(SWORD_OF_TEARS);
                }
                byte move = possibilities.get(convertNumToRandomIndex(num, possibilities.size() - 1));
                setAdditionalMoveShortcut(move, moveHistory);
            }
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CONSUMING_DESIRE: {
                DetailedIntent detail = new DetailedIntent(this, ATTACK_BLOCK, DetailedIntent.BLOCK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case LOVE_AND_HATE: {
                DetailedIntent detail = new DetailedIntent(this, DEBUFF, DetailedIntent.WEAK_TEXTURE);
                detailsList.add(detail);
                break;
            }
            case RAMPAGE: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    @Override
    public void handleTargetingForIntent(EnemyMoveInfo additionalMove, AdditionalIntent additionalIntent, int index) {
        if (index == 0) {
            if (girl1Spawned) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, girl1, girl1.icon, index);
            } else {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
            }
        } else {
            if (girl2Spawned) {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, girl2, girl2.icon, index);
            } else {
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null, index);
            }
        }
    }

    public void SummonGirl(int girl) {
        AbstractMagicalGirl magicalGirl = null;
        if (girl == 0) {
            magicalGirl = girl1 = new QueenOfLove(-600.0f, 0.0f);
            girl1Spawned = true;
        }
        if (girl == 1) {
            magicalGirl = girl2 = new ServantOfCourage(-400.0f, 0.0f);
            girl2Spawned = true;
        }
        if (magicalGirl != null) {
            atb(new SpawnMonsterAction(magicalGirl, false));
            atb(new UsePreBattleActionAction(magicalGirl));
            atb(new TalkAction(magicalGirl, magicalGirl.getSummonDialog()));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
            if (mo instanceof Statue && !mo.isDeadOrEscaped()) {
                atb(new SuicideAction(mo));
            }
        }
        boolean girlTalking = false;
        if (girl1Spawned && !girl1.isDead && !girl1.isDying) {
            atb(new TalkAction(girl1, girl1.getVictoryDialog()));
            girlTalking = true;
        }
        if (girl2Spawned && !girl2.isDead && !girl2.isDying) {
            atb(new TalkAction(girl2, girl2.getVictoryDialog()));
            girlTalking = true;
        }
        if (girlTalking) {
            atb(new VFXAction(new WaitEffect(), 1.0F));
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                girl1.disappear();
                girl2.disappear();
                onBossVictoryLogic();
                this.isDone = true;
            }
        });
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(SenselessWrath.POWER_ID)) {
            if (this.getPower(SenselessWrath.POWER_ID).amount == SenselessWrath.THRESHOLD) {
                this.particleTimer -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer < 0.0F) {
                    this.particleTimer = 0.04F;
                    AbstractDungeon.effectsQueue.add(new FlexibleWrathParticleEffect(this));
                }

                this.particleTimer2 -= Gdx.graphics.getDeltaTime();
                if (this.particleTimer2 < 0.0F) {
                    this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                    AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(WrathStance.STANCE_ID, this));
                }
            }
        }
    }

}