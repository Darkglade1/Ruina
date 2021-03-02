package ruina.monsters.eventBoss.bosses.redmist.monster;


import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbRed;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.act3.bigBird.Sage;
import ruina.monsters.eventBoss.bosses.redmist.deck.RedMistDeck;
import ruina.monsters.eventBoss.core.AbstractBossDeckArchetype;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;
import ruina.monsters.eventBoss.core.manager.EnemyEnergyManager;

import static ruina.RuinaMod.makeMonsterPath;

public class RedMist extends AbstractRuinaCardMonster {
    public static final String ID = RuinaMod.makeID(RedMist.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    public static boolean posStorage = false;

    private static final int HEALTH = 500;

    public RedMist() {
        super(NAME, ID, HEALTH, -4.0f, -16.0f, 240.0f, 290.0f, null, 100.0f, -20.0f);
        this.energyOrb = new EnergyOrbRed();
        this.energy = new EnemyEnergyManager(4);
        setHp(HEALTH);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("ScytheApostle/Spriter/ScytheApostle.scml"));
        this.type = EnemyType.ELITE;
        this.energyString = "[R]";

    }

    @Override
    public void generateDeck() {
        RedMistDeck archetype = new RedMistDeck();
        archetype.initialize();
        currentHealth = maxHealth;
        chosenArchetype = archetype;
        for(AbstractRuinaBossCard c: archetype.getCards()){ masterDeck.addToBottom(c); }
    }


    @Override
    public void onPlayAttackCardSound() {
        // no
    }

}