package ruina.monsters.uninvitedGuests.normal.greta;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
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
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.actions.GretaStealCardAction;
import ruina.actions.VampireDamageActionButItCanFizzle;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.uninvitedGuests.normal.greta.gretaCards.*;
import ruina.powers.*;
import ruina.util.AdditionalIntent;
import ruina.vfx.FlexibleStanceAuraEffect;
import ruina.vfx.FlexibleWrathParticleEffect;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Greta extends AbstractCardMonster
{
    public static final String ID = makeID(Greta.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte BREAK_EGG = 0;
    private static final byte SLAP = 1;
    private static final byte MINCE = 2;
    private static final byte SEASON = 3;
    private static final byte TRIAL = 4;
    private static final byte SACK = 5;

    public final int minceHits = 2;

    public final int STRENGTH = calcAscensionSpecial(2);
    public final int PARALYSIS = calcAscensionSpecial(2);
    public final int BLEED = calcAscensionSpecial(4);
    public final int BLOCK = calcAscensionTankiness(20);
    public final int DEBUFF = calcAscensionSpecial(2);
    public final int damageReduction = 50;
    public final int debuffCleanseTurns = 3;

    public Hod hod;
    public FreshMeat meat;

    private float particleTimer;
    private float particleTimer2;

    public static final String POWER_ID = makeID("Sharkskin");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Greta() {
        this(0.0f, 0.0f);
    }

    public Greta(final float x, final float y) {
        super(NAME, ID, 750, -5.0F, 0, 200.0f, 265.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Greta/Spriter/Greta.scml"));
        this.type = EnemyType.BOSS;
        numAdditionalMoves = 2;
        for (int i = 0; i < numAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        this.setHp(calcAscensionTankiness(750));

        addMove(BREAK_EGG, Intent.ATTACK_DEBUFF, calcAscensionDamage(20));
        addMove(SLAP, Intent.DEFEND_DEBUFF);
        addMove(MINCE, Intent.ATTACK, calcAscensionDamage(9), minceHits, true);
        addMove(SEASON, Intent.DEBUFF);
        addMove(TRIAL, Intent.ATTACK_BUFF, calcAscensionDamage(19));
        addMove(SACK, Intent.STRONG_DEBUFF);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Ensemble1");
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo instanceof Hod) {
                hod = (Hod)mo;
            }
        }
        atb(new TalkAction(this, DIALOG[0]));
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onInitialApplication() {
                amount2 = damageReduction;
                updateDescription();
            }

            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type) {
                if (type == DamageInfo.DamageType.NORMAL && !hasDebuff()) {
                    return damage * (1.0f - ((float)amount2 / 100));
                } else {
                    return damage;
                }
            }

            private boolean hasDebuff() {
                for (AbstractPower po : owner.powers) {
                    if (po.type == PowerType.DEBUFF) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void atEndOfRound() {
                amount++;
                if (amount >= debuffCleanseTurns) {
                    amount = 0;
                    flash();
                    atb(new RemoveDebuffsAction(owner));
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount2 + POWER_DESCRIPTIONS[1] + debuffCleanseTurns + POWER_DESCRIPTIONS[2];
            }
        });
        applyToTarget(this, this, new InvisibleBarricadePower(this));
        applyToTarget(this, this, new StrengthPower(this, 1)); //hacky solution again LOL
        applyToTarget(this, this, new CenterOfAttention(this));
    }

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;

        if(info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case BREAK_EGG: {
                bluntAnimation(target);
                dmg(target, info);
                applyToTarget(target, this, new Paralysis(target, PARALYSIS));
                resetIdle();
                break;
            }
            case SLAP: {
                blockAnimation();
                block(this, BLOCK);
                if (target == adp()) {
                    applyToTarget(target, this, new Bleed(target, BLEED));
                } else {
                    applyToTarget(target, this, new Bleed(target, BLEED, true));
                }
                resetIdle();
                break;
            }
            case MINCE: {
                for (int i = 0; i < multiplier; i++) {
                    if (i % 2 == 0) {
                        pierceAnimation(target);
                    } else {
                        slashAnimation(target);
                    }
                    dmg(target, info);
                    resetIdle();
                }
                break;
            }
            case SEASON: {
                debuffAnimation();
                applyToTarget(target, this, new WeakPower(target, DEBUFF, true));
                applyToTarget(target, this, new VulnerablePower(target, DEBUFF, true));
                resetIdle();
                break;
            }
            case TRIAL: {
                specialAttackAnimation(target);
                atb(new VampireDamageActionButItCanFizzle(target, info, AbstractGameAction.AttackEffect.NONE));
                resetIdle();
                break;
            }
            case SACK: {
                sackAnimation(target);
                atb(new GretaStealCardAction(this));
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
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

    private void slashAnimation(AbstractCreature enemy) {
        animationAction("Slash", "BluntVert", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    private void specialAttackAnimation(AbstractCreature enemy) {
        animationAction("Special", "GretaEat", enemy, this);
    }

    private void sackAnimation(AbstractCreature enemy) {
        animationAction("Sack", null, enemy, this);
    }

    private void debuffAnimation() {
        animationAction("Range", null, this);
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
            } else if (i == 1 && meat != null) {
                takeCustomTurn(additionalMove, meat);
            } else {
                takeCustomTurn(additionalMove, hod);
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (moveHistory.size() >= 3) {
            moveHistory.clear();
        }
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(BREAK_EGG) && !this.lastMoveBefore(BREAK_EGG)) {
            possibilities.add(BREAK_EGG);
        }
        if (!this.lastMove(MINCE) && !this.lastMoveBefore(MINCE)) {
            possibilities.add(MINCE);
        }
        if (!this.lastMove(SLAP) && !this.lastMoveBefore(SLAP)) {
            possibilities.add(SLAP);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move], getMoveCardFromByte(move));
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        ArrayList<Byte> moveHistory = additionalMovesHistory.get(whichMove);
        if (whichMove == 0) {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastTwoMoves(MINCE, moveHistory)) {
                possibilities.add(MINCE);
            }
            if (!this.lastMove(SLAP, moveHistory) && !this.lastMoveBefore(SLAP, moveHistory)) {
                possibilities.add(SLAP);
            }
            if (!this.lastMove(SEASON, moveHistory) && !this.lastMoveBefore(SEASON, moveHistory)) {
                possibilities.add(SEASON);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setAdditionalMoveShortcut(move, moveHistory, getMoveCardFromByte(move));
        } else {
            if (meat != null) {
                setAdditionalMoveShortcut(TRIAL, moveHistory, getMoveCardFromByte(TRIAL));
            } else {
                setAdditionalMoveShortcut(SACK, moveHistory, getMoveCardFromByte(SACK));
            }
        }
    }

    protected AbstractCard getMoveCardFromByte(Byte move) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(new BreakEgg(this));
        list.add(new Slap(this));
        list.add(new Mince(this));
        list.add(new Season(this));
        list.add(new Trial(this));
        list.add(new Sack(this));
        return list.get(move);
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
                if (i == 1) {
                    if (meat != null) {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, meat, FreshMeat.icon);
                    } else {
                        applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
                    }
                } else {
                    applyPowersToAdditionalIntent(additionalMove, additionalIntent, hod, hod.icon);
                }
            }
        }
    }

    @Override
    public void die(boolean triggerRelics) {
        super.die(triggerRelics);
        for (AbstractMonster mo : monsterList()) {
            if (mo instanceof FreshMeat) {
                atb(new SuicideAction(mo));
            }
        }
        hod.onBossDeath();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID)) {
            if (this.getPower(POWER_ID).amount >= debuffCleanseTurns - 1) {
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