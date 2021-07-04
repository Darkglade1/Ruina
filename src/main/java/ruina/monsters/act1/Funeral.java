//package ruina.monsters.act1;
//
//import actlikeit.dungeons.CustomDungeon;
//import com.megacrit.cardcrawl.actions.common.RollMoveAction;
//import com.megacrit.cardcrawl.cards.DamageInfo;
//import com.megacrit.cardcrawl.cards.status.Dazed;
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.localization.MonsterStrings;
//import com.megacrit.cardcrawl.localization.PowerStrings;
//import com.megacrit.cardcrawl.powers.AbstractPower;
//import com.megacrit.cardcrawl.powers.FrailPower;
//import com.megacrit.cardcrawl.powers.WeakPower;
//import ruina.BetterSpriterAnimation;
//import ruina.monsters.AbstractRuinaMonster;
//import ruina.powers.AbstractLambdaPower;
//
//import static ruina.RuinaMod.makeID;
//import static ruina.RuinaMod.makeMonsterPath;
//import static ruina.util.Wiz.*;
//
//public class Funeral extends AbstractRuinaMonster
//{
//    public static final String ID = makeID(Funeral.class.getSimpleName());
//    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
//    public static final String NAME = monsterStrings.NAME;
//    public static final String[] MOVES = monsterStrings.MOVES;
//    public static final String[] DIALOG = monsterStrings.DIALOG;
//
//    private static final byte QUIET = 0;
//    private static final byte COMFORT = 1;
//    private static final byte GUIDING = 2;
//    private static final byte LAMENT = 3;
//
//    private final int DEBUFF = calcAscensionSpecial(1);
//    private final int STATUS = calcAscensionSpecial(3);
//    private final int DAMAGE_THRESHOLD = 12;
//    public boolean useStrongAttack;
//    public boolean attackFullyBlocked;
//    public boolean attackDealtDamage;
//
//    public static final String POWER_ID = makeID("Lamentation");
//    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
//    public static final String POWER_NAME = powerStrings.NAME;
//    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
//
//    public Funeral() {
//        this(0.0f, 0.0f);
//    }
//
//    public Funeral(final float x, final float y) {
//        super(NAME, ID, 230, 0.0F, 0, 250.0f, 280.0f, null, x, y);
//        this.animation = new BetterSpriterAnimation(makeMonsterPath("Funeral/Spriter/Funeral.scml"));
//        this.type = EnemyType.BOSS;
//        setHp(calcAscensionTankiness(maxHealth));
//        addMove(QUIET, Intent.STRONG_DEBUFF);
//        addMove(COMFORT, Intent.DEBUFF);
//        addMove(GUIDING, Intent.ATTACK, calcAscensionDamage(13));
//        addMove(LAMENT, Intent.ATTACK, calcAscensionDamage(7), 3, true);
//    }
//
//    @Override
//    protected void setUpMisc() {
//        super.setUpMisc();
//        this.type = EnemyType.BOSS;
//    }
//
//    @Override
//    public void usePreBattleAction() {
//        CustomDungeon.playTempMusicInstantly("Angela2");
//        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DAMAGE_THRESHOLD) {
//
//            @Override
//            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
//                if (damageAmount > 0 && info.owner == owner && target == adp()) {
//                    this.amount -= damageAmount;
//                    if (this.amount < 0) {
//                        this.amount = 0;
//                    }
//                    attackDealtDamage = true;
//                    updateDescription();
//                } else {
//                    attackFullyBlocked = true;
//                }
//            }
//
//            @Override
//            public void atEndOfRound() {
//                if (this.amount <= 0) {
//                    useStrongAttack = true;
//                    rollMove();
//                    createIntent();
//                    this.amount = DAMAGE_THRESHOLD;
//                    updateDescription();
//                }
//            }
//
//            @Override
//            public void updateDescription() {
//                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
//            }
//        });
//    }
//
//    @Override
//    public void takeTurn() {
//        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
//        int multiplier = this.moves.get(nextMove).multiplier;
//
//        if(info.base > -1) {
//            info.applyPowers(this, adp());
//        }
//
//        attackDealtDamage = false;
//        attackFullyBlocked = false;
//
//        switch (this.nextMove) {
//            case QUIET: {
//                blockAnimation();
//                intoDiscardMo(new Dazed(), STATUS, this);
//                resetIdle(1.0f);
//                break;
//            }
//            case COMFORT: {
//                blockAnimation();
//                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
//                applyToTarget(adp(), this, new FrailPower(adp(), DEBUFF, true));
//                resetIdle(1.0f);
//                break;
//            }
//            case GUIDING: {
//                attackAnimation(adp());
//                dmg(adp(), info);
//                resetIdle();
//                break;
//            }
//            case LAMENT: {
//                for (int i = 0; i < multiplier; i++) {
//                    if (i % 2 == 0) {
//                        whiteAttackAnimation(adp());
//                    } else {
//                        blackAttackAnimation(adp());
//                    }
//                    dmg(adp(), info);
//                    waitAnimation();
//                }
//                resetIdle(0.0f);
//                break;
//            }
//        }
//        atb(new RollMoveAction(this));
//    }
//
//    @Override
//    protected void getMove(final int num) {
//        if (useStrongAttack) {
//            setMoveShortcut(LAMENT, MOVES[LAMENT]);
//            useStrongAttack = false;
//        } else if (attackDealtDamage) {
//            if (lastMove(QUIET)) {
//                setMoveShortcut(GUIDING, MOVES[GUIDING]);
//            } else {
//                setMoveShortcut(QUIET, MOVES[QUIET]);
//            }
//        } else if (attackFullyBlocked) {
//            if (lastMove(COMFORT)) {
//                setMoveShortcut(GUIDING, MOVES[GUIDING]);
//            } else {
//                setMoveShortcut(COMFORT, MOVES[COMFORT]);
//            }
//        } else {
//            setMoveShortcut(GUIDING, MOVES[GUIDING]);
//        }
//    }
//
//    private void blackAttackAnimation(AbstractCreature enemy) {
//        animationAction("Special", "FuneralAtkBlack", enemy, this);
//    }
//
//    private void whiteAttackAnimation(AbstractCreature enemy) {
//        animationAction("Special", "FuneralAtkWhite", enemy, this);
//    }
//
//    private void attackAnimation(AbstractCreature enemy) {
//        animationAction("Attack", "FuneralAtkBlack", enemy, this);
//    }
//
//    private void blockAnimation() {
//        animationAction("Block", "FuneralSpecial", this);
//    }
//
//}