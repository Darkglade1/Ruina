package ruina.monsters.act3;

import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.red.Defend_Red;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BarricadePower;
import ruina.BetterSpriterAnimation;
import ruina.monsters.AbstractDeckMonster;
import ruina.powers.act3.Imposter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.*;

public class Pinocchio extends AbstractDeckMonster
{
    public static final String ID = makeID(Pinocchio.class.getSimpleName());

    private static final byte LEARN = 0;

    public final int MAX_DAMAGE = calcAscensionSpecial(28);
    public final int ARTIFACT = calcAscensionSpecial(2);
    public final int BLOCK = calcAscensionTankiness(20);
    public final int STRENGTH = calcAscensionSpecial(2);

    protected Map<Byte, Integer> blockMoveValues = new HashMap<>();

    public Pinocchio() {
        this(0.0f, 0.0f);
    }

    public Pinocchio(final float x, final float y) {
        super(ID, ID, 170, -5.0F, 0, 250.0f, 255.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Pinocchio/Spriter/Pinocchio.scml"));
        this.setHp(calcAscensionTankiness(170));
        setNumAdditionalMoves(1);

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

    @Override
    public void takeCustomTurn(EnemyMoveInfo move, AbstractCreature target, int whichMove) {
        super.takeCustomTurn(move, target, whichMove);
        switch (move.nextMove) {
            case LEARN: {
                blockAnimation();
                applyToTarget(this, this, new Imposter(this, STRENGTH));
                block(this, BLOCK);
                resetIdle();
                break;
            }
            default: {
                int blockValue = getBlockForMove(move.nextMove);
                if (info.base > -1) {
                    attackAnimation(adp());
                    if (blockValue > 0) {
                        block(this, blockValue);
                    }
                    dmg(adp(), info);
                } else {
                    blockAnimation();
                    if (blockValue > 0) {
                        block(this, blockValue);
                    }
                }
                resetIdle();
                break;
            }
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        atb(new RollMoveAction(this));
    }

    private void attackAnimation(AbstractCreature enemy) {
        animationAction("Attack", "BluntBlow", enemy, this);
    }

    private void blockAnimation() {
        animationAction("Block", null, this);
    }

    @Override
    protected void getMove(final int num) {
        if (this.firstMove) {
            setMoveShortcut(LEARN);
        } else {
            AbstractCard c = topDeckCardForMoveAction();
            setMoveShortcut((byte) c.cardID.hashCode(), c.name, c);
        }
    }

    @Override
    public void getAdditionalMoves(int num, int whichMove) {
        if (!this.firstMove) {
            createAdditionalMoveFromCard(topDeckCardForMoveAction(), moveHistory = additionalMovesHistory.get(whichMove));
        }
    }

    @Override
    protected void createDeck() {
        CardStrings damageString = CardCrawlGame.languagePack.getCardStrings(Strike_Red.ID);
        CardStrings blockString = CardCrawlGame.languagePack.getCardStrings(Defend_Red.ID);
        for (AbstractCard card : adp().masterDeck.group) {
            if ((card.baseDamage > 0 || card.baseBlock > 0) && !(card.rarity == AbstractCard.CardRarity.BASIC) && card.baseDamage <= MAX_DAMAGE) {
                AbstractCard cardCopy = card.makeStatEquivalentCopy();
                Intent intent;
                if (card.baseDamage > 0 && card.baseBlock > 0) {
                    intent = Intent.ATTACK_DEFEND;
                    cardCopy.rawDescription = blockString.DESCRIPTION + " NL " + damageString.DESCRIPTION;
                } else if (card.baseBlock > 0) {
                    intent = Intent.DEFEND;
                    cardCopy.rawDescription = blockString.DESCRIPTION;
                } else {
                    intent = Intent.ATTACK;
                    cardCopy.rawDescription = damageString.DESCRIPTION;
                }
                cardCopy.name += "?";
                cardCopy.initializeDescription();
                masterDeck.addToBottom(cardCopy);
                addMove((byte) card.cardID.hashCode(), intent, card.baseDamage);
                if (card.baseBlock > 0) {
                    blockMoveValues.put((byte) card.cardID.hashCode(), card.baseBlock);
                }
            }
        }
    }

    private int getBlockForMove(byte move) {
        if (blockMoveValues.containsKey(move)) {
            return blockMoveValues.get(move);
        }
        return -1;
    }

    protected void createAdditionalMoveFromCard(AbstractCard c, ArrayList<Byte> moveHistory) { setAdditionalMoveShortcut((byte) c.cardID.hashCode(), moveHistory, c); }
}