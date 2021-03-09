package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.MoveNameEffect;
import ruina.BetterSpriterAnimation;
import ruina.actions.BetterIntentFlashAction;
import ruina.monsters.AbstractDeckMonster;
import ruina.powers.AbstractLambdaPower;
import ruina.util.AdditionalIntent;
import ruina.vfx.VFXActionButItCanFizzle;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Pinocchio extends AbstractDeckMonster
{
    public static final String ID = makeID(Pinocchio.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte LEARN = 0;

    public final int MAX_COST = calcAscensionSpecial(2);
    public final int ARTIFACT = calcAscensionSpecial(2);
    public final int BLOCK = calcAscensionTankiness(20);
    public final int STRENGTH = calcAscensionSpecial(2);

    public static final String POWER_ID = makeID("Imposter");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Pinocchio() {
        this(0.0f, 0.0f);
    }

    public Pinocchio(final float x, final float y) {
        super(NAME, ID, 170, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pinocchio/Spriter/Pinocchio.scml"));
        this.type = EnemyType.NORMAL;
        this.setHp(calcAscensionTankiness(maxHealth));

        maxAdditionalMoves = 1;
        for (int i = 0; i < maxAdditionalMoves; i++) {
            additionalMovesHistory.add(new ArrayList<>());
        }
        numAdditionalMoves = maxAdditionalMoves;

        addMove(LEARN, Intent.UNKNOWN);
        AbstractCard fillerCard = new Madness(); //in case deck somehow has no cards
        addMove((byte) fillerCard.cardID.hashCode(), Intent.ATTACK, 12);
    }

    @Override
    public void usePreBattleAction() {
        initializeDeck();
        applyToTarget(this, this, new BarricadePower(this));
        applyToTarget(this, this, new ArtifactPower(this, ARTIFACT));
    }

    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, AbstractCard card) {
        DamageInfo info = new DamageInfo(this, move.baseDamage, DamageInfo.DamageType.NORMAL);
        int multiplier = move.multiplier;
        if (info.base > -1) {
            info.applyPowers(this, target);
        }
        switch (move.nextMove) {
            case LEARN: {
                blockAnimation();
                applyToTarget(this, this, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, this, STRENGTH) {
                    @Override
                    public void atEndOfRound() {
                        this.flash();
                        applyToTarget(owner, owner, new StrengthPower(owner, amount));
                    }

                    @Override
                    public void updateDescription() {
                        description = POWER_DESCRIPTIONS[0] + amount + POWER_DESCRIPTIONS[1];
                    }
                });
                block(this, BLOCK);
                resetIdle();
                break;
            }
            default: {
                if (info.base > -1) {
                    attackAnimation(adp());
                    if (card.baseBlock > 0) {
                        block(this, card.baseBlock);
                    }
                    dmg(adp(), info);
                } else {
                    blockAnimation();
                    if (card.baseBlock > 0) {
                        block(this, card.baseBlock);
                    }
                }
                resetIdle();
                break;
            }
        }
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (this.firstMove) {
            firstMove = false;
        }
        takeCustomTurn(this.moves.get(nextMove), adp(), enemyCard);
        for (int i = 0; i < additionalMoves.size(); i++) {
            EnemyMoveInfo additionalMove = additionalMoves.get(i);
            AdditionalIntent additionalIntent = additionalIntents.get(i);
            atb(new VFXActionButItCanFizzle(this, new MoveNameEffect(hb.cX - animX, hb.cY + hb.height / 2.0F, additionalIntent.enemyCard.name)));
            atb(new BetterIntentFlashAction(this, additionalIntent.intentImg));
            takeCustomTurn(additionalMove, adp(), additionalIntent.enemyCard);
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    additionalIntent.usePrimaryIntentsColor = true;
                    this.isDone = true;
                }
            });
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove) {
            setMoveShortcut(LEARN, MOVES[LEARN]);
        } else {
            AbstractCard c = topDeckCardForMoveAction();
            setMoveShortcut((byte) c.cardID.hashCode(), c.name, c);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (!this.firstMove) {
            createMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove));
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
                applyPowersToAdditionalIntent(additionalMove, additionalIntent, adp(), null);
            }
        }
    }

    @Override
    protected void createDeck() {
        for (AbstractCard card : adp().masterDeck.group) {
            if ((card.baseDamage > 0 || card.baseBlock > 0) && !(card.rarity == AbstractCard.CardRarity.BASIC) && card.costForTurn <= MAX_COST) {
                masterDeck.addToBottom(card.makeStatEquivalentCopy());
                Intent intent;
                if (card.baseDamage > 0 && card.baseBlock > 0) {
                    intent = Intent.ATTACK_DEFEND;
                } else if (card.baseBlock > 0) {
                    intent = Intent.DEFEND;
                } else {
                    intent = Intent.ATTACK;
                }
                addMove((byte) card.cardID.hashCode(), intent, card.baseDamage);
            }
        }
    }

    @Override
    protected void createMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) {
        setAdditionalMoveShortcut((byte) c.cardID.hashCode(), moveHistory, c);
    }
}