package ruina.monsters.eventBoss.core;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.AbstractRuinaCardMonster;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.cards.CardGroup.DRAW_PILE_X;
import static com.megacrit.cardcrawl.cards.CardGroup.DRAW_PILE_Y;

public abstract class AbstractBossDeckManager {

    protected EnemyCardGroup masterDeck = new EnemyCardGroup(CardGroup.CardGroupType.MASTER_DECK);
    protected EnemyCardGroup drawPile = new EnemyCardGroup(CardGroup.CardGroupType.DRAW_PILE);
    protected EnemyCardGroup exhaustPile = new EnemyCardGroup(CardGroup.CardGroupType.EXHAUST_PILE);
    protected EnemyCardGroup discardPile = new EnemyCardGroup(CardGroup.CardGroupType.DISCARD_PILE);
    protected EnemyCardGroup handPile = new EnemyCardGroup(CardGroup.CardGroupType.HAND);

    private AbstractRuinaCardMonster currentBoss;

    public void addedPreBattle() {
        initializeBossPanel();
    }

    public int turn = 0;

    public EnemyCardGroup getThisTurnCards() { return handPile; }

    public void addToList(ArrayList<AbstractCard> c, AbstractCard q, boolean upgraded) {
        if (upgraded) q.upgrade();
        c.add(q);
    }

    public void initializeBossPanel() {
    }

    public void addToList(ArrayList<AbstractCard> c, AbstractCard q) {
        addToList(c, q, false);
    }

    private String ID;

    public AbstractBossDeckManager(String id) {
        this.ID = id;
        if (AbstractDungeon.actNum != 4) AbstractDungeon.lastCombatMetricKey = ID;
    }

    public void addRelic(AbstractRuinaBossRelic r) {
        r.instantObtain(AbstractRuinaCardMonster.boss);

    }

    protected abstract void initialize();
    protected void addCardToList(AbstractRuinaBossCard c) { this.masterDeck.addToBottom(c); }

    public void moveCardIntoDiscardPile(AbstractCard c){
        // argh, move stuff later
        AbstractRuinaCardMonster.boss.hand.moveToDiscardPile(c);
    }

    public void moveCardIntoExhaustPile(AbstractCard c){
        // argh, move stuff later
        AbstractRuinaCardMonster.boss.hand.moveToDiscardPile(c);
    }

    public EnemyCardGroup getDraw(){ return drawPile;}
    public EnemyCardGroup getDiscard(){ return discardPile;}
    public EnemyCardGroup getExhaustPile(){ return exhaustPile;}
    public EnemyCardGroup getMasterDeck(){ return masterDeck;}
    public EnemyCardGroup getHandPile(){ return handPile;}


    public void draw() {
        if (handPile.size() == 10) { return; }
        CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12f, 0.25f);
        draw(1);
        AbstractRuinaCardMonster.boss.onCardDrawOrDiscard();
    }

    public void draw(final int numCards) {
        for (int i = 0; i < numCards; ++i) {
            if (!this.drawPile.isEmpty()) {
                final AbstractCard c = drawPile.getTopCard();
                AbstractRuinaBossCard cB = (AbstractRuinaBossCard) c;
                cB.bossDarken();
                cB.destroyIntent();
                c.current_x = DRAW_PILE_X;
                c.current_y = DRAW_PILE_Y;
                c.setAngle(0.0f, true);
                c.lighten(false);
                c.drawScale = 0.12f;
                c.targetDrawScale = AbstractRuinaBossCard.HAND_SCALE;
                c.triggerWhenDrawn();
                handPile.addToHand(c);
                this.drawPile.removeCard(c);
                for (final AbstractPower p : AbstractRuinaCardMonster.boss.powers) {
                    p.onCardDraw(c);
                }
                for (final AbstractRelic r : AbstractRuinaCardMonster.boss.relics) {
                    r.onCardDraw(c);
                }
            }
        }
    }
}