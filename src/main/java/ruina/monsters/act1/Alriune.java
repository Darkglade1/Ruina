package ruina.monsters.act1;

import actlikeit.dungeons.CustomDungeon;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.LaurelWreathMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Alriune extends AbstractRuinaMonster
{
    public static final String ID = makeID(Alriune.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte SPRINGS_GENESIS = 0;
    private static final byte FULL_BLOOM = 1;
    private static final byte MAGNIFICENT_END = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEX_DOWN = calcAscensionSpecial(1);
    private final int PARALYSIS = calcAscensionSpecial(1);
    private final int DAMAGE_REDUCTION = calcAscensionSpecial(50);

    public static final String POWER_ID = makeID("WintersInception");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Alriune() {
        this(0.0f, 0.0f);
    }

    public Alriune(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alriune/Spriter/Alriune.scml"));
        this.type = EnemyType.ELITE;
        setHp(calcAscensionTankiness(77), calcAscensionTankiness(81));
        addMove(SPRINGS_GENESIS, Intent.DEBUFF);
        addMove(FULL_BLOOM, Intent.ATTACK_DEBUFF, calcAscensionDamage(11));
        addMove(MAGNIFICENT_END, Intent.ATTACK, calcAscensionDamage(16));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.ELITE;
    }

    @Override
    public void usePreBattleAction() {
        CustomDungeon.playTempMusicInstantly("Warning2");
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, DAMAGE_REDUCTION) {
            boolean triggered = false;

            @Override
            public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
                if (type == DamageInfo.DamageType.NORMAL && triggered && (card == null || !CardModifierManager.hasModifier(card, LaurelWreathMod.ID))) {
                    return damage * (1.0f - ((float)amount / 100));
                } else {
                    return damage;
                }
            }

            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (!triggered && card.type == AbstractCard.CardType.ATTACK) {
                    triggered = true;
                    this.flash();
                    LaurelWreathMod mod = new LaurelWreathMod();
                    if (!CardModifierManager.hasModifier(card, LaurelWreathMod.ID)) {
                        CardModifierManager.addModifier(card, mod.makeCopy());
                    }
                }
            }

            @Override
            public void atEndOfRound() {
                triggered = false;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
            }
        });
    }

    @Override
    public void takeTurn() {
        DamageInfo info = new DamageInfo(this, this.moves.get(nextMove).baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = this.moves.get(nextMove).multiplier;

        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SPRINGS_GENESIS: {
                blockAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEX_DOWN));
                resetIdle(1.0f);
                break;
            }
            case FULL_BLOOM: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(adp(), this, new Paralysis(adp(), PARALYSIS));
                resetIdle();
                break;
            }
            case MAGNIFICENT_END: {
                attackAnimation(adp());
                dmg(adp(), info);
                resetIdle();
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(SPRINGS_GENESIS) && !this.lastMoveBefore(SPRINGS_GENESIS)) {
            possibilities.add(SPRINGS_GENESIS);
        }
        if (!this.lastMove(FULL_BLOOM)) {
            possibilities.add(FULL_BLOOM);
        }
        if (!this.lastMove(MAGNIFICENT_END)) {
            possibilities.add(MAGNIFICENT_END);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "AlriuneHori", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Special", "AlriuneGuard", this);
    }

}