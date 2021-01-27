package ruina.monsters.act2.Jester;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.CollectorCurseEffect;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.SenselessWrath;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleCalmParticleEffect;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class JesterOfNihil extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(JesterOfNihil.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte WILL_OF_NIHIL = 0;
    private static final byte CONSUMING_DESIRE = 1;
    private static final byte LOVE_AND_HATE = 2;
    private static final byte SWORD_OF_TEARS = 3;
    private static final byte RAMPAGE = 4;
    private static final byte SETUP = 5;

    private final int HATE_BLOCK = calcAscensionTankiness(20);
    private final int STRENGTH = calcAscensionSpecial(2);
    private final int BLEED = calcAscensionSpecial(2);
    private final int DEBUFF = calcAscensionSpecial(1);

    private static final int MASS_ATTACK_COOLDOWN = 3;
    private static final int RAMPAGE_COOLDOWN = 1;
    private static final float GIRL_1_X_POSITION = -700.0f;
    private static final float GIRL_2_X_POSITION = -500.0f;
    private static final float STATUE_1_X_POSITION = -250.0f;
    private static final float STATUE_2_X_POSITION = -450.0f;
    private boolean girl1Spawned = false;
    private boolean girl2Spawned = false;
    private AbstractMagicalGirl girl1;
    private AbstractMagicalGirl girl2;
    private InvisibleBarricadePower power = new InvisibleBarricadePower(this);
    private int numIntentThatCanRampage = 2; //0 is the second intent, 1 is the third intent, 2 is the first intent
    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;
    private int ramageCooldown = RAMPAGE_COOLDOWN;

    private float particleTimer;
    private float particleTimer2;

    public static final String HATE_POWER_ID = makeID("PointlessHate");
    public static final PowerStrings hatePowerStrings = CardCrawlGame.languagePack.getPowerStrings(HATE_POWER_ID);
    public static final String HATE_POWER_NAME = hatePowerStrings.NAME;
    public static final String[] HATE_POWER_DESCRIPTIONS = hatePowerStrings.DESCRIPTIONS;

    public JesterOfNihil() {
        this(0.0f, 0.0f);
    }

    public JesterOfNihil(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 280.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Jester/Spriter/Jester.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(this.maxHealth));

        ArrayList<Integer> possibleGirls = new ArrayList<>();
        possibleGirls.add(0);
        possibleGirls.add(1);
        Collections.shuffle(possibleGirls, AbstractDungeon.monsterRng.random);
        int first = possibleGirls.remove(0);
        int second = possibleGirls.remove(0);
        //If Queen of Love is selected always put her in the first position
        if (second == 0) {
            int temp = second;
            second = first;
            first = temp;
        }
        girl1 = returnGirl(first, GIRL_1_X_POSITION);
        girl2 = returnGirl(second, GIRL_2_X_POSITION);

        addMove(WILL_OF_NIHIL, IntentEnums.MASS_ATTACK, calcAscensionDamage(30));
        addMove(CONSUMING_DESIRE, Intent.ATTACK_DEBUFF, calcAscensionDamage(6), 2, true);
        addMove(LOVE_AND_HATE, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(SWORD_OF_TEARS, Intent.ATTACK, calcAscensionDamage(10), 2, true);
        addMove(RAMPAGE, Intent.BUFF);
        addMove(SETUP, Intent.UNKNOWN);
    }

    public AbstractMagicalGirl returnGirl(int num, float xPosition) {
        AbstractMagicalGirl girl = null;
        if (num == 0) {
            girl = new QueenOfLove(xPosition, 0.0f, this);
        }
        if (num == 1) {
            girl = new ServantOfCourage(xPosition, 0.0f, this);
        }
        return girl;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Roland3");
        Statue statue1 = new Statue(STATUE_1_X_POSITION, 0.0f, this, girl1);
        Statue statue2 = new Statue(STATUE_2_X_POSITION, 0.0f, this, girl2);
        atb(new SpawnMonsterAction(statue1, true));
        atb(new UsePreBattleActionAction(statue1));
        atb(new SpawnMonsterAction(statue2, true));
        atb(new UsePreBattleActionAction(statue2));
        applyToTarget(this, this, power);
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        this.loseBlock(); //manually remove block due to the invisible barricade power xd

        if (this.firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
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
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size()];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size() - 1; i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
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
                applyToTarget(target, this, new Bleed(target, BLEED));
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
                    resetIdle();
                }
                break;
            }
            case RAMPAGE: {
                slamAnimation(target);
                applyToTargetNextTurn(this, new StrengthPower(this, STRENGTH));
                ramageCooldown = RAMPAGE_COOLDOWN + 1;
                numIntentThatCanRampage = (numIntentThatCanRampage + 1) % 3;
                resetIdle(1.0f);
                break;
            }
            case SETUP: {
                if (girl1 instanceof QueenOfLove || girl2 instanceof QueenOfLove) {
                    applyToTarget(this, this, new AbstractLambdaPower(HATE_POWER_NAME, HATE_POWER_ID, AbstractPower.PowerType.BUFF, false, this, HATE_BLOCK) {
                        @Override
                        public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
                            this.flash();
                            block(owner, amount);
                        }

                        @Override
                        public void updateDescription() {
                            description = HATE_POWER_DESCRIPTIONS[0] + amount + HATE_POWER_DESCRIPTIONS[1];
                        }
                    });
                }
                if (girl1 instanceof ServantOfCourage || girl2 instanceof ServantOfCourage) {
                    applyToTarget(this, this, new SenselessWrath(this));
                }
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
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new IntentFlashAction(this));
            if (i == 0) {
                if (!girl1Spawned || girl1.isDead || girl1.isDying) {
                    takeCustomTurn(additionalMove, adp());
                } else {
                    takeCustomTurn(additionalMove, girl1);
                }
            }
            if (i == 1) {
                if (!girl2Spawned || girl2.isDead || girl2.isDying) {
                    takeCustomTurn(additionalMove, adp());
                } else {
                    takeCustomTurn(additionalMove, girl2);
                }
            }
        }
        atb(new RollMoveAction(this));
        if (girl1Spawned) {
            atb(new RollMoveAction(girl1));
        }
        if (girl2Spawned) {
            atb(new RollMoveAction(girl2));
        }
        massAttackCooldown--;
        ramageCooldown--;
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(SETUP);
        } else if (girl1Spawned && girl2Spawned &&(!girl1.isDead || !girl2.isDead) && massAttackCooldown <= 0) {
            setMoveShortcut(WILL_OF_NIHIL);
        } else if (numIntentThatCanRampage == 2 && ramageCooldown <= 0) {
            setMoveShortcut(RAMPAGE, MOVES[RAMPAGE]);
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
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (!firstMove) {
            if (numIntentThatCanRampage == whichMove && ramageCooldown <= 0) {
                setAdditionalMoveShortcut(RAMPAGE, moveHistory);
            } else {
                ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
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
                byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
                setAdditionalMoveShortcut(move, moveHistory);
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        for (int i = 0; i < additionalIntents.size(); i++) {
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            EnemyMoveInfo additionalMove = null;
            if (i < additionalMoves.size()) {
                additionalMove = additionalMoves.get(i);
            }
            if (additionalMove != null) {
                if (i == 0) {
                    if (girl1Spawned) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, girl1, girl1.allyIcon);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                } else {
                    if (girl2Spawned) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, girl2, girl2.allyIcon);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                }
            }
        }
    }

    public void SummonGirl(AbstractMagicalGirl girl) {
        if (girl == girl1) {
            girl1Spawned = true;
        }
        if (girl == girl2) {
            girl2Spawned = true;
        }
        atb(new SpawnMonsterAction(girl, false));
        atb(new UsePreBattleActionAction(girl));
        atb(new TalkAction(girl, girl.getSummonDialog()));
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
                girl1.isDead = true;
                girl2.isDead = true;
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