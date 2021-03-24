package ruina.monsters.uninvitedGuests.tanya;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.CustomIntent.IntentEnums;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.DamageAllOtherCharactersAction;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards.AssailingPulls;
import ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards.PullingStrings;
import ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards.Puppetry;
import ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards.ThinStrings;
import ruina.monsters.uninvitedGuests.puppeteer.puppeteerCards.TuggingStrings;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.InvisibleBarricadePower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;
import java.util.Iterator;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Tanya extends AbstractCardMonster
{
    public static final String ID = makeID(Tanya.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte OVERSPEED = 0;
    private static final byte BEATDOWN = 1;
    private static final byte INTIMIDATE = 2;
    private static final byte LUPINE_ASSAULT = 3;
    private static final byte KICKS_AND_STOMPS = 4;
    private static final byte FISTICUFFS = 5;

    public final int overspeedHits = 3;
    public final int kicksAndStompsHits = 2;

    private static final int MASS_ATTACK_COOLDOWN = 2;
    private int massAttackCooldown = MASS_ATTACK_COOLDOWN;

    public final int BLOCK = calcAscensionTankiness(24);
    public final int INITIAL_PLATED_ARMOR = calcAscensionTankiness(20);
    public final int PLATED_ARMOR_GAIN = calcAscensionTankiness(18);
    public final int METALLICIZE_GAIN = calcAscensionTankiness(5);
    public final int STRENGTH = calcAscensionSpecial(2);
    public final int WEAK = calcAscensionSpecial(1);
    public final int GUTS_METALLICIZE_GAIN = calcAscensionTankiness(10);
    public final int GUTS_STRENGTH = calcAscensionSpecial(3);
    public Gebura gebura;

    public static final String POWER_ID = makeID("Guts");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Tanya() {
        this(0.0f, 0.0f);
    }

    public Tanya(final float x, final float y) {
        super(NAME, ID, 500, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppeteer/Spriter/Puppeteer.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(OVERSPEED, Intent.ATTACK, calcAscensionDamage(20), overspeedHits, true);
        addMove(BEATDOWN, IntentEnums.MASS_ATTACK, calcAscensionDamage(28));
        addMove(INTIMIDATE, Intent.DEFEND_BUFF);
        addMove(LUPINE_ASSAULT, Intent.ATTACK_DEFEND, calcAscensionDamage(22));
        addMove(KICKS_AND_STOMPS, Intent.ATTACK_BUFF, calcAscensionDamage(12), kicksAndStompsHits, true);
        addMove(FISTICUFFS, Intent.ATTACK_DEBUFF, calcAscensionDamage(26));

//        cardList.add(new PullingStrings(this));
//        cardList.add(new TuggingStrings(this));
//        cardList.add(new AssailingPulls(this));
//        cardList.add(new ThinStrings(this));
//        cardList.add(new Puppetry(this));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble2");
        AbstractDungeon.getCurrRoom().cannotLose = true;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Gebura) {
                gebura = (Gebura)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + GUTS_STRENGTH + POWER_DESCRIPTIONS[1] + GUTS_METALLICIZE_GAIN + POWER_DESCRIPTIONS[2];
            }
        });
        applyToTarget(this, this, new PlatedArmorPower(this, INITIAL_PLATED_ARMOR));
        block(this, INITIAL_PLATED_ARMOR);
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
            case OVERSPEED: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case BEATDOWN: {
                int[] damageArray = new int[AbstractDungeon.getMonsters().monsters.size() + 1];
                info.applyPowers(this, adp());
                damageArray[damageArray.length - 1] = info.output;
                for (int i = 0; i < AbstractDungeon.getMonsters().monsters.size(); i++) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    info.applyPowers(this, mo);
                    damageArray[i] = info.output;
                }
                atb(new DamageAllOtherCharactersAction(this, damageArray, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                resetIdle(1.0f);
                applyToTarget(this, this, new MetallicizePower(this, METALLICIZE_GAIN));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        massAttackCooldown = MASS_ATTACK_COOLDOWN + 1;
                        this.isDone = true;
                    }
                });
                break;
            }
            case INTIMIDATE: {
                AbstractPower platedArmor = getPower(PlatedArmorPower.POWER_ID);
                if (platedArmor != null) {
                    int amount = platedArmor.amount;
                    atb(new RemoveSpecificPowerAction(this, this, platedArmor));
                    applyToTarget(this, this, new MetallicizePower(this, amount));
                }
                applyToTarget(this, this, new PlatedArmorPower(target, PLATED_ARMOR_GAIN));
                resetIdle();
                break;
            }
            case LUPINE_ASSAULT: {
                blockAnimation();
                block(this, BLOCK);
                dmg(target, info);
                resetIdle();
                break;
            }
            case KICKS_AND_STOMPS: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        bluntAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                break;
            }
            case FISTICUFFS: {
                blockAnimation();
                dmg(target, info);
                applyToTarget(target, this, new WeakPower(target, WEAK, true));
                resetIdle();
                break;
            }
        }
    }

    private void bluntAnimation(AbstractCreature enemy) {
        animationAction("Blunt", "BluntHori", enemy, this);
    }

    private void pierceAnimation(AbstractCreature enemy) {
        animationAction("Pierce", "BluntBlow", enemy, this);
    }

    private void rangedAnimation(AbstractCreature enemy) {
        animationAction("Ranged", "PuppetBreak", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void buffAnimation() {
        animationAction("Special", null, this);
    }

    private void massAttackStartAnimation() {
        animationAction("Special", "PuppetStart", this);
    }

    private void massAttackFinishAnimation() {
        animationAction("Special", "PuppetStrongAtk", this);
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
            if (additionalIntent.targetTexture == null) {
                takeCustomTurn(additionalMove, adp());
            } else {
                takeCustomTurn(additionalMove, gebura);
            }
        }
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                massAttackCooldown--;
                this.isDone = true;
            }
        });
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (massAttackCooldown <= 0) {
            setMoveShortcut(BEATDOWN, MOVES[BEATDOWN], cardList.get(BEATDOWN).makeStatEquivalentCopy());
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(LUPINE_ASSAULT)) {
                possibilities.add(LUPINE_ASSAULT);
            }
            if (!this.lastMove(FISTICUFFS)) {
                possibilities.add(FISTICUFFS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(INTIMIDATE, moveHistory) && !this.lastMoveBefore(INTIMIDATE, moveHistory)) {
            setAdditionalMoveShortcut(INTIMIDATE, moveHistory, cardList.get(INTIMIDATE).makeStatEquivalentCopy());
        } else {
            if (!this.lastMove(LUPINE_ASSAULT, moveHistory)) {
                possibilities.add(LUPINE_ASSAULT);
            }
            if (!this.lastMove(FISTICUFFS, moveHistory)) {
                possibilities.add(FISTICUFFS);
            }
            if (!this.lastMove(KICKS_AND_STOMPS, moveHistory) && !this.lastMoveBefore(KICKS_AND_STOMPS)) {
                possibilities.add(KICKS_AND_STOMPS);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, gebura, gebura.allyIcon);
            }
        }
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (this.currentHealth <= 0 && !this.halfDead) {
            this.halfDead = true;
            Iterator var2 = this.powers.iterator();

            while (var2.hasNext()) {
                AbstractPower p = (AbstractPower) var2.next();
                p.onDeath();
            }

            var2 = AbstractDungeon.player.relics.iterator();

            while (var2.hasNext()) {
                AbstractRelic r = (AbstractRelic) var2.next();
                r.onMonsterDeath(this);
            }

            ArrayList<AbstractPower> powersToRemove = new ArrayList<>();
            for (AbstractPower power : this.powers) {
                if (!(power instanceof PlatedArmorPower) && !(power instanceof MetallicizePower) && !(power instanceof StrengthPower) && !(power instanceof GainStrengthPower)) {
                    powersToRemove.add(power);
                }
            }
            for (AbstractPower power : powersToRemove) {
                this.powers.remove(power);
            }

            atb(new HealAction(this, this, maxHealth));
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    halfDead = false;
                    AbstractDungeon.getCurrRoom().cannotLose = false;
                    this.isDone = true;
                }
            });
            applyToTarget(this, this, new StrengthPower(this, GUTS_STRENGTH));
            applyToTarget(this, this, new MetallicizePower(this, METALLICIZE_GAIN));
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            super.die(triggerRelics);
            gebura.onBossDeath();
        }
    }

}