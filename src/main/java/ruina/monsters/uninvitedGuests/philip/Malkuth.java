package ruina.monsters.uninvitedGuests.philip;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.actions.AllyDamageAllEnemiesAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.cardmods.ManifestMod;
import ruina.monsters.AbstractAllyCardMonster;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.eventboss.redMist.monster.RedMist;
import ruina.monsters.uninvitedGuests.tanya.Tanya;
import ruina.monsters.uninvitedGuests.tanya.geburaCards.Ally_GreaterSplitHorizontal;
import ruina.monsters.uninvitedGuests.tanya.geburaCards.Ally_GreaterSplitVertical;
import ruina.monsters.uninvitedGuests.tanya.geburaCards.Ally_LevelSlash;
import ruina.monsters.uninvitedGuests.tanya.geburaCards.Ally_Spear;
import ruina.monsters.uninvitedGuests.tanya.geburaCards.Ally_UpstandingSlash;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Emotion;
import ruina.vfx.VFXActionButItCanFizzle;
import ruina.vfx.WaitEffect;

import java.util.ArrayList;

import static ruina.RuinaMod.*;
import static ruina.monsters.eventboss.redMist.monster.RedMist.horizontalSplitVfx;
import static ruina.util.Wiz.*;

public class Malkuth extends AbstractAllyCardMonster
{
    public static final String ID = RuinaMod.makeID(Malkuth.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte COORDINATED_ASSAULT = 0;
    private static final byte EMOTIONAL_TURBULENCE = 1;
    private static final byte FERVID_EMOTIONS = 2;
    private static final byte RAGING_STORM = 3;
    private static final byte INFERNO = 4;

    public final int STARTING_STR = 2;
    public final int STRENGTH = 2;
    public final int ALLY_BLOCK = 10;
    public final int SELF_BLOCK = 14;
    public final int DRAW = 1;
    public final int fervidHits = 2;
    public final int fervidEmotions = 4;
    public final int emotionalEmotions = 3;
    public final int stormHits = 2;
    public final int infernoStrScaling = 5;
    public final int VULNERABLE = 2;
    public final int passiveVulnerable = 1;
    public static final int firstEmotionThreshold = 2;
    public static final int secondEmotionThreshold = 4;

    public final int massAttackCooldown = 2;
    public int massAttackCooldownCounter = massAttackCooldown;

    public final int EMOTION_THRESHOLD = 15;
    public final int EXHAUST_GAIN = 2;

    private boolean distorted = false;
    private boolean manifestedEGO = false;
    private static final int NORMAL = 1;
    private static final int DISTORTED = 2;
    private static final int EGO = 3;
    private int phase = NORMAL;

    public Philip philip;

    public static final String POWER_ID = makeID("Dragon");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final String R_POWER_ID = makeID("Wildfire");
    public static final PowerStrings R_powerStrings = CardCrawlGame.languagePack.getPowerStrings(R_POWER_ID);
    public static final String R_POWER_NAME = R_powerStrings.NAME;
    public static final String[] R_POWER_DESCRIPTIONS = R_powerStrings.DESCRIPTIONS;

    public Malkuth() {
        this(0.0f, 0.0f);
    }

    public Malkuth(final float x, final float y) {
        super(NAME, ID, 180, -5.0F, 0, 200.0f, 240.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Malkuth/Spriter/Malkuth.scml"));
        this.animation.setFlip(true, false);

        this.setHp(maxHealth);
        this.type = EnemyType.BOSS;

        addMove(COORDINATED_ASSAULT, Intent.DEFEND_BUFF);
        addMove(EMOTIONAL_TURBULENCE, Intent.ATTACK_DEFEND, 16);
        addMove(FERVID_EMOTIONS, Intent.ATTACK_BUFF, 12, fervidHits, true);
        addMove(RAGING_STORM, Intent.ATTACK_DEBUFF, 20, stormHits, true);
        addMove(INFERNO, Intent.ATTACK, 50);

//        cardList.add(new Ally_UpstandingSlash(this));
//        cardList.add(new Ally_LevelSlash(this));
//        cardList.add(new Ally_Spear(this));
//        cardList.add(new Ally_GreaterSplitVertical(this));
//        cardList.add(new Ally_GreaterSplitHorizontal(this));

        this.allyIcon = makeUIPath("MalkuthIcon.png");
    }

    public void distort() {
        if (!distorted) {
            phase = DISTORTED;
            distorted = true;
            IdlePose();
        }
    }

    public void manifest() {
        if (!manifestedEGO) {
            phase = EGO;
            manifestedEGO = true;
            applyToTargetTop(this, this, new AbstractLambdaPower(R_POWER_NAME, R_POWER_ID, AbstractPower.PowerType.BUFF, false, this, passiveVulnerable) {
                @Override
                public void atEndOfRound() {
                    for (AbstractMonster mo : monsterList()) {
                        if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                            applyToTarget(mo, owner, new VulnerablePower(mo, amount, true));
                        }
                    }
                }

                @Override
                public void onInitialApplication() {
                    for (AbstractMonster mo : monsterList()) {
                        if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                            applyToTarget(mo, owner, new VulnerablePower(mo, amount, true));
                        }
                    }
                }

                @Override
                public void updateDescription() {
                    description = R_POWER_DESCRIPTIONS[0] + amount + R_POWER_DESCRIPTIONS[1];
                }
            });
            IdlePose();
        }
    }

    private void IdlePose() {
        runAnim("Idle" + phase);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Philip) {
                philip = (Philip)mo;
            }
        }
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, EXHAUST_GAIN) {
            @Override
            public void onInitialApplication() {
                for (AbstractCard card : adp().drawPile.group) {
                    CardModifierManager.addModifier(card, new ManifestMod());
                }
            }

            @Override
            public void onExhaust(AbstractCard card) {
                applyToTarget(owner, owner, new Emotion(owner, amount, EMOTION_THRESHOLD));
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new StrengthPower(this, STARTING_STR));
        applyToTarget(this, this, new Emotion(this, 0, EMOTION_THRESHOLD));
        super.usePreBattleAction();
    }

    @Override
    public void takeTurn() {
        if (this.isDead) {
            return;
        }
        super.takeTurn();
        if (firstMove) {
            atb(new TalkAction(this, DIALOG[0]));
            firstMove = false;
        }

        DamageInfo info;
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            info = new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
            multiplier = emi.multiplier;
        } else {
            info = new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }

        AbstractCreature target = philip;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }

        switch (this.nextMove) {
            case COORDINATED_ASSAULT: {
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                block(adp(), ALLY_BLOCK);
                applyToTarget(adp(), this, new DrawCardNextTurnPower(adp(), DRAW));
                resetIdle();
                break;
            }
            case EMOTIONAL_TURBULENCE: {
                block(this, SELF_BLOCK);
                dmg(target, info);
                applyToTarget(this, this, new Emotion(this, emotionalEmotions, EMOTION_THRESHOLD));
                resetIdle();
                break;
            }
            case FERVID_EMOTIONS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        spearAnimation(target);
                    } else {
                        upstandingAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new Emotion(this, fervidEmotions, EMOTION_THRESHOLD));
                break;
            }
            case RAGING_STORM: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size()];
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                for (int i = 0; i < multiplier; i++) {
                    atb(new AllyDamageAllEnemiesAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                    for (AbstractMonster mo : monsterList()) {
                        if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                            applyToTarget(mo, this, new VulnerablePower(mo, VULNERABLE, true));
                        }
                    }
                }
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldownCounter = massAttackCooldown + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case INFERNO: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size()];
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                atb(new AllyDamageAllEnemiesAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle(1.0f);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldownCounter = massAttackCooldown + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                massAttackCooldownCounter--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    private void upstandingAnimation(AbstractCreature enemy) {
        animationAction("Upstanding" + phase, "RedMistVert" + phase, enemy, this);
    }

    private void spearAnimation(AbstractCreature enemy) {
        animationAction("Spear" + phase, "RedMistStab" + phase, enemy, this);
    }

    private void levelAnimation(AbstractCreature enemy) {
        animationAction("Level" + phase, "RedMistHori" + phase, enemy, this);
    }

    private void verticalUpAnimation(AbstractCreature enemy) {
        animationAction("VerticalUp" + phase, "RedMistVertHit", enemy, this);
    }

    private void verticalDownAnimation(AbstractCreature enemy) {
        animationAction("VerticalDown" + phase, "RedMistVertFin", enemy, this);
    }

    private void horizontalAnimation(AbstractCreature enemy) {
        animationAction("Horizontal", "RedMistHoriFin", enemy, this);
    }

    @Override
    protected void resetIdle(float duration) {
        atb(new VFXActionButItCanFizzle(this, new WaitEffect(), duration));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                IdlePose();
                this.isDone = true;
            }
        });
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear();
        }
        if (canUseMassAttack()) {
            if (phase == EGO) {
                setMoveShortcut(INFERNO, MOVES[INFERNO], cardList.get(INFERNO));
            } else {
                setMoveShortcut(RAGING_STORM, MOVES[RAGING_STORM], cardList.get(RAGING_STORM));
            }
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (phase == NORMAL) {
                if (!this.lastMove(COORDINATED_ASSAULT) && !this.lastMoveBefore(COORDINATED_ASSAULT)) {
                    possibilities.add(COORDINATED_ASSAULT);
                }
            }
            if (!this.lastMove(FERVID_EMOTIONS) && !this.lastMoveBefore(FERVID_EMOTIONS)) {
                possibilities.add(FERVID_EMOTIONS);
            }
            if (!this.lastMove(EMOTIONAL_TURBULENCE) && !this.lastMoveBefore(EMOTIONAL_TURBULENCE)) {
                possibilities.add(EMOTIONAL_TURBULENCE);
            }
            if (possibilities.isEmpty()) {
                possibilities.add(FERVID_EMOTIONS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move));
        }
    }

    public boolean canUseMassAttack() {
        if (massAttackCooldownCounter <= 0 && phase > NORMAL) {
            for (AbstractMonster mo : monsterList()) {
                if (mo instanceof CryingChild) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void applyPowers() {
        if (this.nextMove == -1 || philip.isDeadOrEscaped()) {
            super.applyPowers();
            return;
        }
        applyPowers(philip);
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new TalkAction(this, DIALOG[1]));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

}