package ruina.monsters.uninvitedGuests.puppeteer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
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
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Puppeteer extends AbstractMultiIntentMonster
{
    public static final String ID = makeID(Puppeteer.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte PULLING_STRINGS_TAUT = 0;
    private static final byte TUGGING_STRINGS = 1;
    private static final byte ASSAILING_PULLS = 2;
    private static final byte THIN_STRINGS = 3;
    private static final byte PUPPETRY = 4;

    private static final float MASTERMIND_DAMAGE_REDUCTION = 0.5f;
    private static final int MASS_ATTACK_COOLDOWN = 3;
    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;

    private final int BLOCK = calcAscensionTankiness(12);
    private final int STRENGTH = calcAscensionSpecial(3);
    private final int WEAK = calcAscensionSpecial(1);
    private final int VULNERABLE = calcAscensionSpecial(1);
    public Chesed chesed;
    public Puppet puppet;

    public static final String POWER_ID = makeID("Mastermind");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Puppeteer() {
        this(100.0f, 0.0f);
    }

    public Puppeteer(final float x, final float y) {
        super(NAME, ID, 600, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Hermit/Spriter/Hermit.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(600));

        addMove(PULLING_STRINGS_TAUT, IntentEnums.MASS_ATTACK, calcAscensionDamage(36));
        addMove(TUGGING_STRINGS, Intent.ATTACK, calcAscensionDamage(11), 2, true);
        addMove(ASSAILING_PULLS, Intent.ATTACK_DEBUFF, calcAscensionDamage(14));
        addMove(THIN_STRINGS, Intent.DEFEND_DEBUFF);
        addMove(PUPPETRY, Intent.BUFF);
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.getCurrRoom().cannotLose = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Chesed) {
                chesed = (Chesed)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        Summon();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                //handles attack damage
                if (type == DamageInfo.DamageType.NORMAL) {
                    return calculateDamageTakenAmount(damage, type);
                } else {
                    return damage;
                }
            }

            private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
                return damage * (1 - MASTERMIND_DAMAGE_REDUCTION);
            }

            @Override
            public int onAttackedToChangeDamage(DamageInfo info, int damageAmount) {
                //handles non-attack damage
                if (info.type != DamageInfo.DamageType.NORMAL) {
                    return (int) calculateDamageTakenAmount(damageAmount, info.type);
                } else {
                    return damageAmount;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + (int)(MASTERMIND_DAMAGE_REDUCTION * 100) + POWER_DESCRIPTIONS[1];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case PULLING_STRINGS_TAUT: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case TUGGING_STRINGS: {
                for (int i = 0; i < multiplier; i++) {
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case ASSAILING_PULLS: {
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, VULNERABLE, true));
                resetIdle();
                //force puppet to attack this target next turn
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (target == chesed) {
                            puppet.attackingAlly = true;
                        } else {
                            puppet.attackingAlly = false;
                        }
                        AbstractDungeon.onModifyPower();
                        this.isDone = true;
                    }
                });
                break;
            }
            case THIN_STRINGS: {
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                        block(mo, BLOCK);
                    }
                }
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                resetIdle(1.0f);
                break;
            }
            case PUPPETRY: {
                for (AbstractMonster mo : monsterList()) {
                    if (!mo.isDeadOrEscaped() && !(mo instanceof AbstractAllyMonster)) {
                        applyToTarget(mo, this, new StrengthPower(mo, STRENGTH));
                    }
                }
                resetIdle(1.0f);
                break;
            }
        }
    }

    private void attack1Animation(AbstractCreature enemy) {
        animationAction("Attack1", "HermitAtk", enemy, this);
    }

    private void attack2Animation(AbstractCreature enemy) {
        animationAction("Attack2", "HermitStrongAtk", enemy, this);
    }

    private void specialAnimation() {
        animationAction("Special", "HermitWand", this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        atb(new RemoveAllBlockAction(this, this));
        takeCustomTurn(this.moves.get(nextMove), adp());
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, MOVES[additionalMove.nextMove])));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            if (chesed.isDead || chesed.isDying) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, chesed);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (massAttackCooldown <= 0) {
            setMoveShortcut(PULLING_STRINGS_TAUT);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(TUGGING_STRINGS)) {
                possibilities.add(TUGGING_STRINGS);
            }
            if (!this.lastMove(ASSAILING_PULLS) && !chesed.isDead && !chesed.isDying) {
                possibilities.add(ASSAILING_PULLS);
            }
            if (!this.lastMove(THIN_STRINGS)) {
                possibilities.add(THIN_STRINGS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move]);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(TUGGING_STRINGS, moveHistory)) {
            possibilities.add(TUGGING_STRINGS);
        }
        if (!this.lastMove(ASSAILING_PULLS, moveHistory) && !(this.nextMove == ASSAILING_PULLS)) {
            possibilities.add(ASSAILING_PULLS);
        }
        if (!this.lastMove(PUPPETRY, moveHistory) && !this.lastMoveBefore(PUPPETRY)) {
            possibilities.add(PUPPETRY);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory);
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, chesed, chesed.allyIcon);
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        AbstractDungeon.getCurrRoom().cannotLose = false;
        chesed.onBossDeath();
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof Puppet) {
                atb(new SuicideAction(mo));
            }
        }
    }

    private void Summon() {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                float xPosition = -200.0F;
                puppet = new Puppet(xPosition, 0.0f, Puppeteer.this);
                att(new UsePreBattleActionAction(puppet));
                att(new SpawnMonsterAction(puppet, true));
                this.isDone = true;
            }
        });
    }

}