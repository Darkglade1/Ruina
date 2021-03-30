package ruina.monsters.uninvitedGuests.elena;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractCardMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Bleed;
import ruina.powers.InvisibleBarricadePower;
import ruina.powers.Protection;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Elena extends AbstractCardMonster
{
    public static final String ID = makeID(Elena.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte CIRCULATION = 0;
    private static final byte SANGUINE_NAILS = 1;
    private static final byte SIPHON = 2;
    private static final byte BLOODSPREADING = 3;
    private static final byte INJECT = 4;

    public final int sanguineNailsHits = 2;

    public final int PROTECTION = calcAscensionSpecial(3);
    public final int STRENGTH = calcAscensionSpecial(3);
    public final int FRAIL = calcAscensionSpecial(2);
    public final int BLEED = calcAscensionSpecial(10);
    public Binah binah;
    public VermilionCross vermilionCross;

    public static final String POWER_ID = makeID("BloodRed");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Elena() {
        this(0.0f, 0.0f);
    }

    public Elena(final float x, final float y) {
        super(NAME, ID, 400, -5.0F, 0, 160.0f, 245.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Puppeteer/Spriter/Puppeteer.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 1;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(maxHealth));

        addMove(CIRCULATION, Intent.BUFF);
        addMove(SANGUINE_NAILS, Intent.ATTACK, calcAscensionDamage(9), sanguineNailsHits, true);
        addMove(SIPHON, Intent.ATTACK_DEBUFF, calcAscensionDamage(12));
        addMove(BLOODSPREADING, Intent.ATTACK, calcAscensionDamage(36));
        addMove(INJECT, Intent.STRONG_DEBUFF);

//        cardList.add(new PullingStrings(this));
//        cardList.add(new TuggingStrings(this));
//        cardList.add(new AssailingPulls(this));
//        cardList.add(new ThinStrings(this));
//        cardList.add(new Puppetry(this));
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble3");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Binah) {
                binah = (Binah)mo;
            }
            if (mo instanceof VermilionCross) {
                vermilionCross = (VermilionCross)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, -1) {
            @Override
            public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
                if (info.type == DamageInfo.DamageType.NORMAL && info.owner == owner && damageAmount > 0) {
                    att(new HealAction(owner, owner, damageAmount));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
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
            case CIRCULATION: {
                AbstractCreature buffTarget = vermilionCross;
                if (buffTarget.isDeadOrEscaped()) {
                    buffTarget = this;
                }
                applyToTarget(buffTarget, this, new StrengthPower(buffTarget, STRENGTH));
                applyToTarget(buffTarget, this, new Protection(buffTarget, PROTECTION));
                resetIdle();
                break;
            }
            case SANGUINE_NAILS: {
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
            case SIPHON: {
                rangedAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new FrailPower(target, FRAIL, true));
                resetIdle();
                break;
            }
            case BLOODSPREADING: {
                dmg(target, info);
                resetIdle(1.0f);
                break;
            }
            case INJECT: {
                buffAnimation();
                applyToTarget(target, this, new Bleed(target, BLEED));
                resetIdle(1.0f);
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
                takeCustomTurn(additionalMove, binah);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SANGUINE_NAILS) && !this.lastMoveBefore(SANGUINE_NAILS)) {
            possibilities.add(SANGUINE_NAILS);
        }
        if (!this.lastMove(SIPHON) && !this.lastMoveBefore(SIPHON)) {
            possibilities.add(SIPHON);
        }
        if (!this.lastMove(CIRCULATION) && !this.lastMoveBefore(CIRCULATION)) {
            possibilities.add(CIRCULATION);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], cardList.get(move).makeStatEquivalentCopy());
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BLOODSPREADING, moveHistory) && !this.lastMoveBefore(BLOODSPREADING)) {
            possibilities.add(BLOODSPREADING);
        }
        if (!this.lastMove(INJECT, moveHistory) && !this.lastMoveBefore(INJECT)) {
            possibilities.add(INJECT);
        }
        if (!this.lastMove(SANGUINE_NAILS, moveHistory) && !this.lastMoveBefore(SANGUINE_NAILS)) {
            possibilities.add(SANGUINE_NAILS);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setAdditionalMoveShortcut(move, moveHistory, cardList.get(move).makeStatEquivalentCopy());
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
                if (additionalMove.nextMove == SANGUINE_NAILS) {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, binah, binah.allyIcon);
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        binah.onBossDeath();
    }

    public void onVermilionDeath() {
        if (!this.isDeadOrEscaped()) {
            atb(new TalkAction(this, DIALOG[1]));
        }
    }

}