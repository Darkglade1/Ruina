package ruina.monsters.act1;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractRuinaMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.DetailedIntent;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class ForsakenMurderer extends AbstractRuinaMonster
{
    public static final String ID = makeID(ForsakenMurderer.class.getSimpleName());

    private static final byte CHAINED_WRATH = 0;
    private static final byte METALLIC_RINGING = 1;

    private final int STRENGTH_LOSS = 1;
    private final int STRENGTH = calcAscensionSpecial(1);

    public static final String POWER_ID = makeID("Fear");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ForsakenMurderer() {
        this(0.0f, 0.0f);
    }

    public ForsakenMurderer(final float x, final float y) {
        super(ID, ID, 140, 0.0F, 0, 250.0f, 215.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Murderer/Spriter/Murderer.scml"));
        setHp(calcAscensionTankiness(43), calcAscensionTankiness(47));
        addMove(CHAINED_WRATH, Intent.ATTACK_BUFF, calcAscensionDamage(6));
        addMove(METALLIC_RINGING, Intent.ATTACK, calcAscensionDamage(6), 2);
    }

    @Override
    public void usePreBattleAction() {
        applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH_LOSS) {
            public void onUseCard(AbstractCard card, UseCardAction action) {
                if (card.type == AbstractCard.CardType.ATTACK) {
                    this.flash();
                    atb(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, -amount), -amount));
                    if (!this.owner.hasPower(ArtifactPower.POWER_ID)) {
                        atb(new ApplyPowerAction(this.owner, this.owner, new GainStrengthPower(this.owner, amount), amount));
                    }
                }
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
            case CHAINED_WRATH: {
                attackAnimation(adp());
                dmg(adp(), info);
                applyToTarget(this, this, new StrengthPower(this, STRENGTH));
                resetIdle();
                break;
            }
            case METALLIC_RINGING: {
                for (int i = 0; i < multiplier; i++) {
                    attackAnimation(adp());
                    dmg(adp(), info);
                    resetIdle(0.25f);
                    waitAnimation(0.25f);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (firstMove) {
            setMoveShortcut(METALLIC_RINGING);
        } else {
            ArrayList<Byte> possibilities = new ArrayList<>();
            if (!this.lastMove(CHAINED_WRATH)) {
                possibilities.add(CHAINED_WRATH);
            }
            if (!this.lastTwoMoves(METALLIC_RINGING)) {
                possibilities.add(METALLIC_RINGING);
            }
            byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
            setMoveShortcut(move);
        }
    }

    @Override
    protected ArrayList<DetailedIntent> getDetails(EnemyMoveInfo move, int intentNum) {
        ArrayList<DetailedIntent> detailsList = new ArrayList<>();
        switch (move.nextMove) {
            case CHAINED_WRATH: {
                DetailedIntent detail = new DetailedIntent(this, STRENGTH, DetailedIntent.STRENGTH_TEXTURE);
                detailsList.add(detail);
                break;
            }
        }
        return detailsList;
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntHori", enemy, this);
    }

}