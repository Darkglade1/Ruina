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
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.cardmods.LaurelWreathMod;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.powers.Paralysis;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Alriune extends AbstractRuinaMonster
{
    public static final String ID = makeID(Alriune.class.getSimpleName());

    private static final byte SPRINGS_GENESIS = 0;
    private static final byte FULL_BLOOM = 1;
    private static final byte MAGNIFICENT_END = 2;

    private final int STRENGTH = calcAscensionSpecial(2);
    private final int DEX_DOWN = calcAscensionSpecial(1);
    private final int PARALYSIS = calcAscensionSpecial(1);
    private final int DAMAGE_REDUCTION = calcAscensionSpecial(50);

    private static final int DEBUFF_COOLDOWN = 2;
    private int cooldown = DEBUFF_COOLDOWN;

    public static final String POWER_ID = makeID("WintersInception");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Alriune() {
        this(0.0f, 0.0f);
    }

    public Alriune(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alriune/Spriter/Alriune.scml"));
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
                if (!CardModifierManager.hasModifier(card, LaurelWreathMod.ID)) {
                    if (!triggered && card.type == AbstractCard.CardType.ATTACK) {
                        triggered = true;
                        this.flash();
                        LaurelWreathMod mod = new LaurelWreathMod();
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
        super.takeTurn();
        switch (this.nextMove) {
            case SPRINGS_GENESIS: {
                blockAnimation();
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                applyToTarget(adp(), this, new DexterityPower(adp(), -DEX_DOWN));
                resetIdle(1.0f);
                cooldown = DEBUFF_COOLDOWN + 1;
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
        cooldown--;
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (cooldown <= 0) {
            setMoveShortcut(SPRINGS_GENESIS);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(FULL_BLOOM)) {
                possibilities.add(FULL_BLOOM);
            }
            if (!this.lastMove(MAGNIFICENT_END)) {
                possibilities.add(MAGNIFICENT_END);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case SPRINGS_GENESIS: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                DetailedIntent detail2 = new DetailedIntent(this, -DEX_DOWN, DetailedIntent.DEXTERITY_TEXTURE);
                detailsList.add(detail2);
                break;
            }
            case FULL_BLOOM: {
                DetailedIntent detail = new DetailedIntent(this, PARALYSIS, DetailedIntent.PARALYSIS_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "AlriuneHori", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Special", "AlriuneGuard", this);
    }

}