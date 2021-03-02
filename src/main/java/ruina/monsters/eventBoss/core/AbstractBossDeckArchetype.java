package ruina.monsters.eventBoss.core;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.AbstractRuinaCardMonster;

import java.util.ArrayList;

public abstract class AbstractBossDeckArchetype {

    private ArrayList<AbstractRuinaBossCard> classGlobalCards;
    private AbstractRuinaCardMonster currentBoss;

    public void addedPreBattle() {
        initializeBossPanel();
    }

    public int turn = 0;

    public ArrayList<AbstractCard> getThisTurnCards() { return new ArrayList<>(); }

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

    public AbstractBossDeckArchetype(String id) {
        this.ID = id;
        if (AbstractDungeon.actNum != 4) AbstractDungeon.lastCombatMetricKey = ID;
        this.classGlobalCards = new ArrayList<>();
    }

    public void addRelic(AbstractRuinaBossRelic r) {
        r.instantObtain(AbstractRuinaCardMonster.boss);

    }

    protected abstract void initialize();
    protected void addCardToList(AbstractRuinaBossCard c) { this.classGlobalCards.add(c); }
    public ArrayList<AbstractRuinaBossCard> getCards() { return classGlobalCards; }
}