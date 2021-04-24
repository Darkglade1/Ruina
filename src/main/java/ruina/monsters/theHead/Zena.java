//package ruina.monsters.theHead;
//
//import actlikeit.dungeons.CustomDungeon;
//import basemod.helpers.VfxBuilder;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.animations.TalkAction;
//import com.megacrit.cardcrawl.actions.animations.VFXAction;
//import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
//import com.megacrit.cardcrawl.actions.common.RollMoveAction;
//import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
//import com.megacrit.cardcrawl.actions.common.SuicideAction;
//import com.megacrit.cardcrawl.cards.DamageInfo;
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.localization.MonsterStrings;
//import com.megacrit.cardcrawl.localization.PowerStrings;
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
//import com.megacrit.cardcrawl.powers.AbstractPower;
//import com.megacrit.cardcrawl.powers.StrengthPower;
//import com.megacrit.cardcrawl.powers.VulnerablePower;
//import com.megacrit.cardcrawl.powers.WeakPower;
//import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
//import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
//import ruina.BetterSpriterAnimation;
//import ruina.CustomIntent.IntentEnums;
//import ruina.actions.BetterIntentFlashAction;
//import ruina.actions.DamageAllOtherCharactersAction;
//import ruina.actions.UsePreBattleActionAction;
//import ruina.monsters.AbstractAllyMonster;
//import ruina.monsters.AbstractCardMonster;
//import ruina.monsters.uninvitedGuests.normal.puppeteer.Chesed;
//import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppet;
//import ruina.powers.AbstractLambdaPower;
//import ruina.powers.InvisibleBarricadePower;
//import ruina.util.AdditionalIntent;
//import ruina.vfx.VFXActionButItCanFizzle;
//
//import java.util.ArrayList;
//import java.util.function.BiFunction;
//
//import static ruina.RuinaMod.makeID;
//import static ruina.RuinaMod.makeMonsterPath;
//import static ruina.util.Wiz.*;
//
//public class Zena extends AbstractCardMonster
//{
//    public static final String ID = makeID(Zena.class.getSimpleName());
//    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
//    public static final String NAME = monsterStrings.NAME;
//    public static final String[] MOVES = monsterStrings.MOVES;
//    public static final String[] DIALOG = monsterStrings.DIALOG;
//
//    private static final byte LINE = 0;
//    private static final byte THIN_LINE = 1;
//    private static final byte THICK_LINE = 2;
//    private static final byte SHOCKWAVE = 3;
//
//    private static final int MASS_ATTACK_COOLDOWN = 3;
//    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;
//
//    public final int BLOCK = calcAscensionTankiness(45);
//    public final int STUN = calcAscensionSpecial(1);
//
//    public Chesed chesed;
//    public Baral baral;
//
//    public static final String POWER_ID = makeID("Arbiter");
//    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
//    public static final String POWER_NAME = powerStrings.NAME;
//    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
//
//    public Zena() {
//        this(0.0f, 0.0f);
//    }
//
//    public Zena(final float x, final float y) {
//        super(NAME, ID, 1000, -5.0F, 0, 160.0f, 245.0f, null, x, y);
//        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppeteer/Spriter/Puppeteer.scml"));
//        this.type = EnemyType.BOSS;
//        numAdditionalMoves = 0;
//        maxAdditionalMoves = 1;
//        for (int i = 0; i < maxAdditionalMoves; i++) {
//            additionalMovesHistory.add(new ArrayList<>());
//        }
//        this.setHp(calcAscensionTankiness(maxHealth));
//
//        addMove(LINE, Intent.ATTACK, calcAscensionDamage(26));
//        addMove(THIN_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(24));
//        addMove(THICK_LINE, Intent.ATTACK_DEBUFF, calcAscensionDamage(22));
//        addMove(SHOCKWAVE, IntentEnums.MASS_ATTACK, calcAscensionDamage(40));
//
////        cardList.add(new PullingStrings(this));
////        cardList.add(new TuggingStrings(this));
////        cardList.add(new AssailingPulls(this));
////        cardList.add(new ThinStrings(this));
////        cardList.add(new Puppetry(this));
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
//        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
//            if (mo instanceof Baral) {
//                baral = (Baral)mo;
//            }
//        }
//        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
//
//            @Override
//            public void updateDescription() {
//                description = POWER_DESCRIPTIONS[0] + POWER_DESCRIPTIONS[1];
//            }
//        });
//        applyToTarget(this, this, new InvisibleBarricadePower(this));
//    }
//
//    @Override
//    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
//        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
//        int multiplier = move.multiplier;
//
//        if(info.base > -1) {
//            info.applyPowers(this, target);
//        }
//        switch (move.nextMove) {
//            case LINE: {
//                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
//                info.applyPowers(this, adp());
//                damageArray[damageArray.length - 1] = info.output;
//                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
//                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
//                    info.applyPowers(this, mo);
//                    damageArray[i] = info.output;
//                }
//
//                massAttackStartAnimation();
//                massAttackEffect();
//                massAttackFinishAnimation();
//                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
//                resetIdle(1.0f);
//                atb(new AbstractGameAction() {
//                    @Override
//                    public void update() {
//                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
//                        this.isDone = true;
//                    }
//                });
//                break;
//            }
//            case THIN_LINE: {
//                for (int i = 0; i < multiplier; i++) {
//                    if (i % 2 == 0) {
//                        pierceAnimation(target);
//                    } else {
//                        bluntAnimation(target);
//                    }
//                    dmg(target, info);
//                    resetIdle();
//                }
//                break;
//            }
//            case THICK_LINE: {
//                rangedAnimation(target);
//                dmg(target, info);
//                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
//                resetIdle();
//                //force puppet to attack this target next turn
//                if (target == chesed) {
//                    puppet.attackingAlly = true;
//                } else {
//                    puppet.attackingAlly = false;
//                }
//                AbstractDungeon.onModifyPower();
//                break;
//            }
//            case SHOCKWAVE: {
//                blockAnimation();
//                for (AbstractMonster mo : monsterList()) {
//                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
//                        block(mo, BLOCK);
//                    }
//                }
//                applyToTarget(target, this, new WeakPower(target, WEAK, true));
//                resetIdle(1.0f);
//                break;
//            }
//            case SERUM_K: {
//                buffAnimation();
//                for (AbstractMonster mo : monsterList()) {
//                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
//                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
//                    }
//                }
//                resetIdle(1.0f);
//                break;
//            }
//        }
//    }
//
//    private void bluntAnimation(AbstractCreature enemy) {
//        animationAction("Blunt", "BluntHori", enemy, this);
//    }
//
//    private void pierceAnimation(AbstractCreature enemy) {
//        animationAction("Pierce", "BluntBlow", enemy, this);
//    }
//
//    private void rangedAnimation(AbstractCreature enemy) {
//        animationAction("Ranged", "PuppetBreak", enemy, this);
//    }
//
//    private void blockAnimation() {
//        animationAction("Block", null, this);
//    }
//
//    private void buffAnimation() {
//        animationAction("Special", null, this);
//    }
//
//    private void massAttackStartAnimation() {
//        animationAction("Special", "PuppetStart", this);
//    }
//
//    private void massAttackFinishAnimation() {
//        animationAction("Special", "PuppetStrongAtk", this);
//    }
//
//    @Override
//    public void takeTurn() {
//        super.takeTurn();
//        if (this.firstMove) {
//            firstMove = false;
//        }
//        atb(new RemoveAllBlockAction(this, this));
//        takeCustomTurn(this.moves.get(nextMove), adp());
//        for (int i = 0; i < additionalMoves.size(); i++) {
//            EnemyMoveInfo additionalMove = additionalMoves.get(i);
//            AdditionalIntent additionalIntent = additionalIntents.get(i);
//            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
//            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
//            if (additionalIntent.targetTexture == null) {
//                takeCustomTurn(additionalMove, adp());
//            } else {
//                takeCustomTurn(additionalMove, chesed);
//            }
//        }
//        atb(new AbstractGameAction() {
//            @Override
//            public void update() {
//                massAttackCooldown--;
//                this.isDone = true;
//            }
//        });
//        atb(new RollMoveAction(this));
//    }
//
//    @Override
//    protected void getMove(final int num) {
//        if (massAttackCooldown <= 0) {
//            setMoveShortcut(LINE, MOVES[LINE], cardList.get(LINE).makeStatEquivalentCopy());
//        } else if (lastMove(THICK_LINE)) {
//            setMoveShortcut(THIN_LINE, MOVES[THIN_LINE], cardList.get(THIN_LINE).makeStatEquivalentCopy());
//        } else {
//            ArrayList<Byte> possibilities = new ArrayList<>();
//            if (!this.lastMove(THIN_LINE)) {
//                possibilities.add(THIN_LINE);
//            }
//            if (!this.lastMove(THICK_LINE) && chesed != null && !chesed.isDead && !chesed.isDying && massAttackCooldown != 1) {
//                possibilities.add(THICK_LINE);
//            }
//            if (!this.lastMove(SHOCKWAVE)) {
//                possibilities.add(SHOCKWAVE);
//            }
//            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
//            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
//        }
//    }
//
//    @Override
//    public void getAdditionalMoves(int num, int whichMove) {
//        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
//        if (this.lastMove(THICK_LINE, moveHistory)) {
//            setAdditionalMoveShortcut(THIN_LINE, moveHistory, cardList.get(THIN_LINE).makeStatEquivalentCopy());
//        } else {
//            ArrayList<Byte> possibilities = new ArrayList<>();
//            if (!this.lastMove(THIN_LINE, moveHistory)) {
//                possibilities.add(THIN_LINE);
//            }
//            if (!this.lastMove(THICK_LINE, moveHistory) && this.nextMove != THICK_LINE && massAttackCooldown != 1) {
//                possibilities.add(THICK_LINE);
//            }
//            if (!this.lastMove(SERUM_K, moveHistory)) {
//                possibilities.add(SERUM_K);
//            }
//            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
//            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
//        }
//    }
//
//    @Override
//    public void applyPowers() {
//        super.applyPowers();
//        for (int i = 0; i < additionalIntents.size(); i++) {
//            AdditionalIntent additionalIntent = additionalIntents.get(i);
//            EnemyMoveInfo additionalMove = null;
//            if (i < additionalMoves.size()) {
//                additionalMove = additionalMoves.get(i);
//            }
//            if (additionalMove != null) {
//                applyPowersToAdditionalIntent(additionalMove, additionalIntent, chesed, chesed.allyIcon);
//            }
//        }
//    }
//
//    @Override
//    public void die(boolean triggerRelics) {
//        super.die(triggerRelics);
//        AbstractDungeon.getCurrRoom().cannotLose = false;
//        for (AbstractMonster mo : monsterList()) {
//            if (mo instanceof Puppet) {
//                atb(new SuicideAction(mo));
//            }
//        }
//        chesed.onBossDeath();
//    }
//
//}