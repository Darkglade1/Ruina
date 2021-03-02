package ruina.monsters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.relics.SlaversCollar;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.vfx.cardManip.CardDisappearEffect;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import ruina.monsters.eventBoss.core.AbstractBossDeckArchetype;
import ruina.monsters.eventBoss.core.AbstractRuinaBossCard;
import ruina.monsters.eventBoss.core.AbstractRuinaBossRelic;
import ruina.monsters.eventBoss.core.EnemyCardGroup;
import ruina.monsters.eventBoss.core.actions.util.*;
import ruina.monsters.eventBoss.core.manager.EnemyEnergyManager;
import ruina.monsters.eventBoss.core.ui.EnemyEnergyPanel;

import java.util.ArrayList;
import java.util.Comparator;

import static com.megacrit.cardcrawl.cards.CardGroup.DRAW_PILE_X;
import static com.megacrit.cardcrawl.cards.CardGroup.DRAW_PILE_Y;
import static ruina.monsters.eventBoss.core.enums.cardENUMS.CHR_ATTACK;
import static ruina.monsters.eventBoss.core.enums.cardENUMS.CHR_SETUP;

public class AbstractRuinaCardMonster extends AbstractRuinaMonster {

    public static AbstractRuinaCardMonster boss;
    public static boolean finishedSetup;

    public ArrayList<AbstractRuinaBossRelic> relics;
    public int masterHandSize;
    public int gameHandSize;
    public int mantraGained = 0;
    public boolean powerhouseTurn;
    public EnemyEnergyManager energy;
    public EnergyOrbInterface energyOrb;
    public EnemyEnergyPanel energyPanel;
    public CardGroup masterDeck;
    public CardGroup drawPile;
    public CardGroup hand;
    public CardGroup discardPile;
    public CardGroup exhaustPile;
    public CardGroup limbo;
    public AbstractCard cardInUse;
    public int damagedThisCombat;
    public int cardsPlayedThisTurn;
    public int attacksPlayedThisTurn;
    public AbstractPlayer.PlayerClass chosenClass;
    public AbstractBossDeckArchetype chosenArchetype = null;
    public boolean onSetupTurn = true;
    private static boolean debugLog = false;
    private static int attacksDrawnForAttackPhase = 0;
    private static int setupsDrawnForSetupPhase = 0;

    public String energyString = "[E]";

    public AbstractRuinaCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        finishedSetup = false;
        this.drawX = (float) Settings.WIDTH * 0.75F - 150F * Settings.xScale;
        this.type = EnemyType.BOSS;
        //enum later
        //this.chosenClass = playerClass;
        this.energyPanel = new EnemyEnergyPanel(this);
        this.hand = new EnemyCardGroup(CardGroupType.HAND, this);

        this.masterDeck = new EnemyCardGroup(CardGroupType.MASTER_DECK, this);
        this.drawPile = new EnemyCardGroup(CardGroupType.DRAW_PILE, this);
        this.discardPile = new EnemyCardGroup(CardGroupType.DISCARD_PILE, this);
        this.exhaustPile = new EnemyCardGroup(CardGroupType.EXHAUST_PILE, this);

        this.limbo = new EnemyCardGroup(CardGroupType.UNSPECIFIED, this);
        this.masterHandSize = 5;
        this.gameHandSize = 5;
        this.relics = new ArrayList<>();
    }

    public AbstractRuinaCardMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        finishedSetup = false;
        this.drawX = (float) Settings.WIDTH * 0.75F - 150F * Settings.xScale;
        this.type = EnemyType.BOSS;
        //enum later
        //this.chosenClass = playerClass;
        this.energyPanel = new EnemyEnergyPanel(this);
        this.hand = new EnemyCardGroup(CardGroupType.HAND, this);

        this.masterDeck = new EnemyCardGroup(CardGroupType.MASTER_DECK, this);
        this.drawPile = new EnemyCardGroup(CardGroupType.DRAW_PILE, this);
        this.discardPile = new EnemyCardGroup(CardGroupType.DISCARD_PILE, this);
        this.exhaustPile = new EnemyCardGroup(CardGroupType.EXHAUST_PILE, this);
        this.limbo = new EnemyCardGroup(CardGroupType.UNSPECIFIED, this);
        this.masterHandSize = 5;
        this.gameHandSize = 5;
        this.relics = new ArrayList<>();
    }

    @Override
    public void init() {
        boss = this;
        this.setHp(this.maxHealth);
        this.energy.energyMaster = 4;
        this.generateAll();
        super.init();
        this.preBattlePrep();
        finishedSetup = true;
    }

    @Override
    public void getMove(int num) {
        this.setMove((byte) 0, Intent.NONE);
    }


    public void generateDeck() {
    }


    public void generateAll() {
        this.generateDeck();
        for (AbstractCard c : this.masterDeck.group) {
            ((AbstractRuinaBossCard) c).owner = this;
        }
        masterDeck.shuffle();
        ArrayList<AbstractCard> isInnateCard = new ArrayList<>();
        for (AbstractCard c : this.masterDeck.group) {
            if (c.isInnate) { isInnateCard.add(c); }
        }
        if (isInnateCard.size() > 0) {
            this.masterDeck.group.removeAll(isInnateCard);
            for (AbstractCard c : isInnateCard) { this.masterDeck.addToBottom(c); }
        }

        if (AbstractDungeon.ascensionLevel >= 20 && CardCrawlGame.dungeon instanceof com.megacrit.cardcrawl.dungeons.TheBeyond) {
            //new CBR_LizardTail().instantObtain(this);
            // new CBR_MagicFlower().instantObtain(this);
        }
        /*
        if (NeowBoss.neowboss != null) {
            switch (chosenArchetype.actNum) {
                case 1: {
                    chosenArchetype.maxHPModifier += 200;
                    break;
                }
                case 2: {
                    chosenArchetype.maxHPModifier += 100;
                    break;
                }
                case 3: {
                    chosenArchetype.maxHPModifier += 0;
                    break;
                }
            }
        }
        */
        //maxHealth += chosenArchetype.maxHPModifier;
        if (AbstractDungeon.ascensionLevel >= 9) {
            maxHealth = Math.round(maxHealth * 1.2F);
        }
        currentHealth = maxHealth;
        updateHealthBar();
    }

    public void usePreBattleAction() {
        this.energy.recharge();
        for (AbstractRuinaBossRelic r : this.relics) {
            r.atBattleStartPreDraw();
        }
        addToBot(new DelayedActionAction(new CharbossTurnstartDrawAction()));
        for (AbstractRuinaBossRelic r : this.relics) {
            r.atBattleStart();
        }

        playMusic();
        chosenArchetype.addedPreBattle();
    }

    public void playMusic() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();

        String musicKey;
        if (AbstractDungeon.actNum == 0) musicKey = "BOSS_BOTTOM";
        else if (AbstractDungeon.actNum == 1) musicKey = "BOSS_CITY";
        else if (AbstractDungeon.actNum == 2) musicKey = "BOSS_BEYOND";
        else musicKey = "MINDBLOOM";

        AbstractDungeon.getCurrRoom().playBgmInstantly(musicKey);
    }

    @Override
    public void takeTurn() {
        attacksDrawnForAttackPhase = 0;
        setupsDrawnForSetupPhase = 0;
        this.startTurn();
        //addToBot(new CharbossSortHandAction());
        addToBot(new CharbossMakePlayAction());
        this.onSetupTurn = !this.onSetupTurn;
    }

    public void makePlay() {
        for (AbstractCard _c : this.hand.group) {
            AbstractRuinaBossCard c = (AbstractRuinaBossCard) _c;
            if (c.canUse(AbstractDungeon.player, this) && c.getPriority(this.hand.group) > -100) {
                //SlimeboundMod.logger.info("Enemy using card: " + c.name + " energy = " + EnemyEnergyPanel.totalCount);
                this.useCard(c, this, EnemyEnergyPanel.totalCount);
                this.addToBot(new DelayedActionAction(new CharbossDoNextCardAction()));
                return;
            }

        }

    }

    @Override
    public void update() {
        super.update();
        for (AbstractRelic r : this.relics) {
            r.update();
        }

        this.combatUpdate();
    }

    @Override
    public void applyEndOfTurnTriggers() {
        if (hasPower(StunMonsterPower.POWER_ID)) chosenArchetype.turn--;

        this.energy.recharge();

        for (final AbstractPower p : AbstractRuinaCardMonster.boss.powers) {
            p.onEnergyRecharge();
        }

        for (final AbstractCard c : this.hand.group) {
            c.triggerOnEndOfTurnForPlayingCard();
        }
        addToBot(new EnemyDiscardAtEndOfTurnAction());
        /*
        for (final AbstractCard c : this.drawPile.group) {
            c.resetAttributes();
        }
        for (final AbstractCard c : this.discardPile.group) {
            c.resetAttributes();
        }
        */
        /*
        for (final AbstractCard c : this.hand.group) {
            c.resetAttributes();
        }

         */
        addToBot(new DelayedActionAction(new CharbossTurnstartDrawAction()));
    }


    public void startTurn() {
        ////SlimeboundMod.logger.info("Start Turn Triggered");
        this.cardsPlayedThisTurn = 0;
        this.attacksPlayedThisTurn = 0;
        for (AbstractCard c : hand.group) {
            ((AbstractRuinaBossCard) c).lockIntentValues = true;
        }
        this.applyStartOfTurnRelics();
        this.applyStartOfTurnPreDrawCards();
        this.applyStartOfTurnCards();
        //this.applyStartOfTurnPowers();

    }

    public ArrayList<AbstractCard> getThisTurnCards() {
        return chosenArchetype.getThisTurnCards();
    }

    class sortByNewPrio implements Comparator<AbstractRuinaBossCard> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(AbstractRuinaBossCard a, AbstractRuinaBossCard b) {
            return a.newPrio - b.newPrio;
        }
    }


    public void endTurnStartTurn() {
        if (!AbstractDungeon.getCurrRoom().isBattleOver) {
            addToBot(new EnemyDrawCardAction(this, this.gameHandSize, true));
            /*
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    isDone = true;
                    for (AbstractCard q : getThisTurnCards()) {
                        AbstractRuinaCardMonster.boss.hand.addToTop(q);
                        if (q instanceof AbstractRuinaBossCard) ((AbstractRuinaBossCard) q).bossDarken();
                        q.current_y = Settings.HEIGHT / 2F;
                        q.current_x = Settings.WIDTH;
                    }

                    ArrayList<AbstractRuinaBossCard> handAsBoss = new ArrayList<>();
                    for (AbstractCard c : AbstractRuinaCardMonster.boss.hand.group) {
                        handAsBoss.add((AbstractRuinaBossCard) c);
                    }

                    Collections.sort(handAsBoss, new sortByNewPrio());

                    ArrayList<AbstractCard> newHand = new ArrayList<>();
                    for (AbstractCard c : handAsBoss) {
                        newHand.add(c);
                        c.applyPowers();
                    }

                    AbstractRuinaCardMonster.boss.hand.group = newHand;

                    AbstractRuinaCardMonster.boss.hand.refreshHandLayout();
                }
            });

             */
            addToBot(new WaitAction(0.2f));
            this.applyStartOfTurnPostDrawRelics();
            this.applyStartOfTurnPostDrawPowers();
            if (!AbstractDungeon.player.hasRelic(RunicDome.ID)) {
                addToBot(new CharbossSortHandAction());
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        isDone = true;
                        int budget = energyPanel.getCurrentEnergy();
                        for (AbstractCard c : AbstractRuinaCardMonster.boss.hand.group) {
                            if (c.costForTurn <= budget && c.costForTurn != -2 && c instanceof AbstractRuinaBossCard) {
                                ((AbstractRuinaBossCard) c).createIntent();
                                ((AbstractRuinaBossCard) c).bossLighten();
                                budget -= c.costForTurn;
                                budget += ((AbstractRuinaBossCard) c).energyGeneratedIfPlayed;
                                if (budget < 0) budget = 0;
                            }
                            else if (c.costForTurn == -2 && c.type == AbstractCard.CardType.CURSE && c.color == AbstractCard.CardColor.CURSE) {
                                ((AbstractRuinaBossCard) c).bossLighten();
                            }
                        }
                        for (AbstractCard c : AbstractRuinaCardMonster.boss.hand.group) {
                            AbstractRuinaBossCard cB = (AbstractRuinaBossCard) c;
                            cB.refreshIntentHbLocation();
                        }
                    }
                });
            }

            this.cardsPlayedThisTurn = 0;
            this.attacksPlayedThisTurn = 0;
        }
    }


    public void preApplyIntentCalculations() {
        //boolean hasShuriken = hasRelic(CBR_Shuriken.ID);
        int attackCount = 0;
        int artifactCount = 0;

        if (AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID)) {
            artifactCount = AbstractDungeon.player.getPower(ArtifactPower.POWER_ID).amount;
        }

        //Reset all custom modifiers back to 0
        for (AbstractCard c : hand.group) {
            ((AbstractRuinaBossCard) c).manualCustomDamageModifier = 0;
            ((AbstractRuinaBossCard) c).manualCustomDamageModifierMult = 1;
        }

        for (int i = 0; i < hand.size(); i++) {
            AbstractRuinaBossCard c = (AbstractRuinaBossCard) hand.group.get(i);

            if (!c.lockIntentValues) {
                //Artifact Checks - calculates if any Artifact will be left
                if (c.artifactConsumedIfPlayed > 0) {
                    artifactCount -= c.artifactConsumedIfPlayed;
                }

                //Vulnerable Check - knows to check if any Artifact will be left
                if (c.vulnGeneratedIfPlayed > 0) {
                    if (artifactCount <= 0) {
                        for (int j = i + 1; j < hand.size(); j++) {
                            AbstractRuinaBossCard c2 = (AbstractRuinaBossCard) hand.group.get(j);
                            c2.manualCustomVulnModifier = true;
                        }
                    }
                }

            }
        }
        for (AbstractCard c : hand.group) {
            if (!((AbstractRuinaBossCard) c).bossDarkened) {
                ((AbstractRuinaBossCard) c).createIntent();
            }
        }

    }

    public void applyPowers() {
        super.applyPowers();

        preApplyIntentCalculations();

        this.hand.applyPowers();
        this.drawPile.applyPowers();
        this.discardPile.applyPowers();
        /*
        this.drawPile.genPreview();
        this.discardPile.genPreview();
        */
    }


    public void sortHand() {
        ArrayList<AbstractRuinaBossCard> cardsByValue = new ArrayList<AbstractRuinaBossCard>();
        ArrayList<AbstractRuinaBossCard> affordableCards = new ArrayList<AbstractRuinaBossCard>();
        ArrayList<AbstractRuinaBossCard> unaffordableCards = new ArrayList<AbstractRuinaBossCard>();
        ArrayList<AbstractCard> sortedCards = new ArrayList<AbstractCard>();
        for (AbstractCard _c : this.hand.group) {
            AbstractRuinaBossCard c = (AbstractRuinaBossCard) _c;
            if (cardsByValue.size() < 1) {
                cardsByValue.add(c);
            } else {
                boolean gotem = false;
                for (int i = 0; i < cardsByValue.size(); i++) {
                    int maxRange = 0;
                    if (boss.hasRelic(RunicDome.ID)) maxRange += 4;
                    if (cardsByValue.get(i).getPriority(this.hand.group) < c.getPriority(this.hand.group) + AbstractDungeon.aiRng.random(0, maxRange)) {
                        cardsByValue.add(i, c);
                        gotem = true;
                        break;
                    }
                }
                if (!gotem) {
                    cardsByValue.add(c);
                }
            }
        }
        int budget = energyPanel.getCurrentEnergy();
        ////SlimeboundMod.logger.info("Hand budget being calculated for the turn." + budget);
        for (int i = 0; i < cardsByValue.size(); i++) {
            AbstractRuinaBossCard c = cardsByValue.get(i);
            if (c.costForTurn <= budget && c.costForTurn != -2) {
                budget -= c.costForTurn;
                affordableCards.add(c);
            } else {
                unaffordableCards.add(c);
            }
        }
        for (int i = 0; i < affordableCards.size(); i++) {
            AbstractRuinaBossCard c = affordableCards.get(i);
            if (sortedCards.size() < 1) {
                sortedCards.add(c);
            } else {
                boolean gotem = false;
                for (int j = 0; j < sortedCards.size(); j++) {
                    if (((AbstractRuinaBossCard) sortedCards.get(j)).getPriority(this.hand.group) < c.getPriority(this.hand.group)) {
                        sortedCards.add(j, c);
                        gotem = true;
                        break;
                    }
                }
                if (!gotem) {
                    sortedCards.add(c);
                }
            }
        }
        for (AbstractRuinaBossCard c : unaffordableCards) {
            sortedCards.add(c);
        }
        budget = energyPanel.getCurrentEnergy();
        ////SlimeboundMod.logger.info("Hand budget being calculated for the turn." + budget);
        for (int i = 0; i < sortedCards.size(); i++) {
            AbstractRuinaBossCard c = (AbstractRuinaBossCard) sortedCards.get(i);
            if (c.costForTurn <= budget && c.costForTurn != -2 && c.getPriority(this.hand.group) > -100) {
                c.createIntent();
                c.bossLighten();
                budget -= c.costForTurn;
                budget += c.energyGeneratedIfPlayed;
                if (budget < 0) budget = 0;
            }
            if (c.type == AbstractCard.CardType.CURSE && boss.hasRelic("Blue Candle")) { c.createIntent();
            } else if (c.type == AbstractCard.CardType.STATUS && boss.hasRelic("Medical Kit")) { c.createIntent(); }
            this.hand.group = sortedCards;
            this.hand.refreshHandLayout();
            for (AbstractCard card : this.hand.group) {
                AbstractRuinaBossCard cB = (AbstractRuinaBossCard) card;
                cB.refreshIntentHbLocation();
            }
        }
    }


    public int getIntentDmg() {
        int totalIntentDmg = -1;
        for (AbstractCard c : this.hand.group) {
            AbstractRuinaBossCard cB = (AbstractRuinaBossCard) c;
            if (cB.intentDmg > 0 && (!cB.bossDarkened || AbstractDungeon.player.hasRelic(RunicDome.ID))) {
                if (totalIntentDmg == -1) {
                    totalIntentDmg = 0;
                }
                totalIntentDmg += cB.intentDmg;
            }
        }
        return totalIntentDmg;
    }

    public int getIntentBaseDmg() {
        return getIntentDmg();
    }

    /////////////////////////////////////////////////////////////////////////////
    ////////////[[[[[[[[PLAYER-MIMICING FUNCTIONS]]]]]]]]////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    public boolean hasRelic(final String targetID) {
        for (final AbstractRelic r : this.relics) {
            if (r.relicId.equals(targetID)) {
                return true;
            }
        }
        return false;
    }

    public AbstractRelic getRelic(final String targetID) {
        for (final AbstractRelic r : this.relics) {
            if (r.relicId.equals(targetID)) {
                return r;
            }
        }
        return null;
    }

    public void gainEnergy(final int e) {
        EnemyEnergyPanel.addEnergy(e);
        this.hand.glowCheck();

    }

    public void loseEnergy(final int e) {
        EnemyEnergyPanel.useEnergy(e);
    }


    public void draw() {
        if (this.hand.size() == 10) { return; }
        CardCrawlGame.sound.playAV("CARD_DRAW_8", -0.12f, 0.25f);
        this.draw(1);
        this.onCardDrawOrDiscard();
    }

    private AbstractCard findReplacementCardInDraw(ArrayList<AbstractCard> drawPile, boolean attack, boolean setup) {
        for (AbstractCard c : drawPile) {
            //Skip the top card.
            if (drawPile.get(0) != c) {
                if (attack && c.hasTag(CHR_ATTACK)) {
                    //if (debugLog) //SlimeboundMod.logger.info("Attack replacement was requested. Returning: " + c.name);
                    return c;
                }
                if (setup && c.hasTag(CHR_SETUP)) {
                    //if (debugLog) //SlimeboundMod.logger.info("Setup replacement was requested. Returning: " + c.name);
                    return c;
                }
                if (!setup && !attack && !c.hasTag(CHR_ATTACK) && !c.hasTag(CHR_SETUP)) { return c; }
            }
        }
        //if (debugLog) //SlimeboundMod.logger.info("Replacement was requested, but no card was valid. Returning null");
        return null;
    }

    private AbstractCard performCardSearch(ArrayList<AbstractCard> drawPile, DrawTypes firstPriority, DrawTypes secondPriority, DrawTypes thirdPriority) {
        AbstractCard replacementCard = null;

        if (firstPriority == secondPriority || firstPriority == thirdPriority || secondPriority == thirdPriority) {
            //SlimeboundMod.logger.info("ERROR! performCardSearch has two parameters as the same priority! These all need to be different! First priority:" + firstPriority + "Second priority: " + secondPriority + "Third priority: " + thirdPriority);

            return null;
        }

        ////SlimeboundMod.logger.info("Replacement search requested.  First priority: " + firstPriority);

        if (firstPriority == DrawTypes.Setup) {
            replacementCard = findReplacementCardInDraw(drawPile, false, true);
        } else if (firstPriority == DrawTypes.Attack) {
            replacementCard = findReplacementCardInDraw(drawPile, true, false);
        } else {
            replacementCard = findReplacementCardInDraw(drawPile, false, false);
        }

        if (replacementCard != null) {
            //SlimeboundMod.logger.info("First priority search successful. Returning " + replacementCard.name);
            return replacementCard;
        }

        //SlimeboundMod.logger.info("First priority failed.  Second priority: " + secondPriority);

        if (secondPriority == DrawTypes.Setup) {
            replacementCard = findReplacementCardInDraw(drawPile, false, true);
        } else if (secondPriority == DrawTypes.Attack) {
            replacementCard = findReplacementCardInDraw(drawPile, true, false);
        } else {
            replacementCard = findReplacementCardInDraw(drawPile, false, false);
        }

        if (replacementCard != null) {
            //SlimeboundMod.logger.info("Second priority search successful. Returning " + replacementCard.name);
            return replacementCard;
        }

        //SlimeboundMod.logger.info("Second priority failed.  Third priority: " + thirdPriority);

        if (thirdPriority == DrawTypes.Setup) {
            replacementCard = findReplacementCardInDraw(drawPile, false, true);
        } else if (thirdPriority == DrawTypes.Attack) {
            replacementCard = findReplacementCardInDraw(drawPile, true, false);
        } else {
            replacementCard = findReplacementCardInDraw(drawPile, false, false);
        }

        if (replacementCard != null) {
            //SlimeboundMod.logger.info("Third priority search successful. Returning " + replacementCard.name);
            return replacementCard;
        }

        //SlimeboundMod.logger.info("Priority search yielded no result. Returning null.");
        return null;
    }


    public void draw(final int numCards) {
        for (int i = 0; i < numCards; ++i) {
            if (!this.drawPile.isEmpty()) {
                final AbstractCard c = this.drawPile.getTopCard();
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
                this.hand.addToHand(c);
                this.drawPile.removeCard(c);
                for (final AbstractPower p : this.powers) { p.onCardDraw(c); }
                for (final AbstractRelic r : this.relics) { r.onCardDraw(c); }
            }
        }
    }

    public void onCardDrawOrDiscard() {
        for (final AbstractPower p : this.powers) { p.onDrawOrDiscard(); }
        for (final AbstractRelic r : this.relics) { r.onDrawOrDiscard(); }
        if (this.hasPower(CorruptionPower.POWER_ID)) {
            for (final AbstractCard c : this.hand.group) {
                if (c.type == AbstractCard.CardType.SKILL && c.costForTurn != 0) {
                    c.modifyCostForCombat(-9);
                }
            }
        }
        this.hand.applyPowers();
        this.hand.glowCheck();
    }

    public void useCard(final AbstractCard c, AbstractRuinaCardMonster monster, final int energyOnUse) {
        if (monster == null) {
            monster = this;
        }
        if (c.type == AbstractCard.CardType.ATTACK) {
            this.attacksPlayedThisTurn++;
            this.useFastAttackAnimation();

            if (c.damage > MathUtils.random(20)) {
                this.onPlayAttackCardSound();
            }
        }
        this.cardsPlayedThisTurn++;
        c.calculateCardDamage(monster);
        if (c.cost == -1 && EnemyEnergyPanel.totalCount < energyOnUse && !c.ignoreEnergyOnUse) {
            c.energyOnUse = EnemyEnergyPanel.totalCount;
        }
        if (c.cost == -1 && c.isInAutoplay) {
            c.freeToPlayOnce = true;
        }
        c.use(AbstractDungeon.player, monster);
        addToBot(new EnemyUseCardAction(c, monster));
        if (!c.dontTriggerOnUseCard) {
            this.hand.triggerOnOtherCardPlayed(c);
        }
        this.hand.removeCard(c);
        this.cardInUse = c;
        c.target_x = (float) (Settings.WIDTH / 2);
        c.target_y = (float) (Settings.HEIGHT / 2);
        if (c.costForTurn > 0 && !c.freeToPlay() && !c.isInAutoplay && (!this.hasPower("Corruption") || c.type != AbstractCard.CardType.SKILL)) {
            this.energy.use(c.costForTurn);
        }

        AbstractRuinaBossCard cB = (AbstractRuinaBossCard) c;
        cB.showIntent = false;
    }

    public void combatUpdate() {
        if (this.cardInUse != null) {
            this.cardInUse.update();
        }
        this.energyPanel.update();
        this.limbo.update();
        this.exhaustPile.update();
        this.drawPile.update();
        this.discardPile.update();

        this.hand.update();
        this.hand.updateHoverLogic();
        for (final AbstractPower p : this.powers) {
            p.updateParticles();
        }
    }

    public void onPlayAttackCardSound() {
    }


    @Override
    public void damage(final DamageInfo info) {
        int damageAmount = info.output;
        boolean hadBlock = true;
        if (this.currentBlock == 0) {
            hadBlock = false;
        }
        if (damageAmount < 0) {
            damageAmount = 0;
        }
        if (damageAmount > 1 && this.hasPower(IntangiblePower.POWER_ID)) {
            damageAmount = 1;
        }
        final boolean weakenedToZero = damageAmount == 0;
        damageAmount = this.decrementBlock(info, damageAmount);
        ////SlimeboundMod.logger.info(info.owner + " pre damage about to apply relics");
        if (info.owner == this) {
            for (final AbstractRelic r : this.relics) {
                ////SlimeboundMod.logger.info(r.name + " onAttackToChange firing");
                damageAmount = r.onAttackToChangeDamage(info, damageAmount);


            }
        }
        if (info.owner == AbstractDungeon.player) {
            for (final AbstractRelic r : AbstractDungeon.player.relics) {
                damageAmount = r.onAttackToChangeDamage(info, damageAmount);
            }
        }
        if (info.owner != null) {
            for (final AbstractPower p : info.owner.powers) {
                damageAmount = p.onAttackToChangeDamage(info, damageAmount);
            }
        }
        for (final AbstractRelic r : this.relics) {
            damageAmount = r.onAttackedToChangeDamage(info, damageAmount);
        }
        for (final AbstractPower p : this.powers) {
            damageAmount = p.onAttackedToChangeDamage(info, damageAmount);
        }
        if (info.owner == this) {
            for (final AbstractRelic r : this.relics) {
                r.onAttack(info, damageAmount, this);
            }
        }
        if (info.owner == AbstractDungeon.player) {
            for (final AbstractRelic r : AbstractDungeon.player.relics) {
                r.onAttack(info, damageAmount, this);
            }
        }
        if (info.owner != null) {
            for (final AbstractPower p : info.owner.powers) {
                p.onAttack(info, damageAmount, this);
            }
            for (final AbstractPower p : this.powers) {
                damageAmount = p.onAttacked(info, damageAmount);
            }
            for (final AbstractRelic r : this.relics) {
                damageAmount = r.onAttacked(info, damageAmount);
            }
        }
        for (final AbstractRelic r : this.relics) {
            damageAmount = r.onLoseHpLast(damageAmount);
        }
        this.lastDamageTaken = Math.min(damageAmount, this.currentHealth);
        final boolean probablyInstantKill = this.currentHealth == 0;
        if (damageAmount > 0 || probablyInstantKill) {
            for (final AbstractPower p : this.powers) {
                damageAmount = p.onLoseHp(damageAmount);
            }
            for (final AbstractPower p : this.powers) {
                p.wasHPLost(info, damageAmount);
            }
            for (final AbstractRelic r : this.relics) {
                r.wasHPLost(damageAmount);
            }
            if (info.owner != null) {
                for (final AbstractPower p : info.owner.powers) {
                    p.onInflictDamage(info, damageAmount, this);
                }
            }
            if (info.owner != this) {
                this.useStaggerAnimation();
            }
            if (damageAmount > 0) {
                for (final AbstractRelic r : this.relics) {
                    r.onLoseHp(damageAmount);
                }
                if (info.owner != this) {
                    this.useStaggerAnimation();
                }
                if (damageAmount >= 99 && !CardCrawlGame.overkill) {
                    CardCrawlGame.overkill = true;
                }
                this.currentHealth -= damageAmount;
                if (!probablyInstantKill) {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, damageAmount));
                }
                if (this.currentHealth < 0) {
                    this.currentHealth = 0;
                }
                this.healthBarUpdatedEvent();
                this.updateCardsOnDamage();
            }
            if (this.currentHealth <= this.maxHealth / 2.0f && !this.isBloodied) {
                this.isBloodied = true;
                for (final AbstractRelic r : this.relics) {
                    if (r != null) {
                        r.onBloodied();
                    }
                }
            }
            if (this.currentHealth < 1) {
                /*
                if (!this.hasRelic("Mark of the Bloom")) {
                    if (this.hasRelic(CBR_LizardTail.ID) && ((CBR_LizardTail) this.getRelic(CBR_LizardTail.ID)).counter == -1) {
                        this.currentHealth = 0;
                        this.getRelic(CBR_LizardTail.ID).onTrigger();
                        return;
                        }
                 */
                this.die();
                if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.cleanCardQueue();
                    AbstractDungeon.effectList.add(new DeckPoofEffect(64.0f * Settings.scale, 64.0f * Settings.scale, true));
                    AbstractDungeon.effectList.add(new DeckPoofEffect(Settings.WIDTH - 64.0f * Settings.scale, 64.0f * Settings.scale, false));
                    AbstractDungeon.overlayMenu.hideCombatPanels();
                }
                if (this.currentBlock > 0) {
                    this.loseBlock();
                    AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0f + AbstractMonster.BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0f + AbstractMonster.BLOCK_ICON_Y));
                }

                for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    if ((!m.isDead) && (!m.isDying) && m.hasPower(MinionPower.POWER_ID)) {
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.utility.HideHealthBarAction(m));
                        AbstractDungeon.actionManager.addToTop(new com.megacrit.cardcrawl.actions.common.SuicideAction(m));
                    }
                }
            }
        } else if (!probablyInstantKill) {
            if (weakenedToZero && this.currentBlock == 0) {
                if (hadBlock) {
                    AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, AbstractMonster.TEXT[30]));
                } else {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
                }
            } else if (Settings.SHOW_DMG_BLOCK) {
                AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, AbstractMonster.TEXT[30]));
            }
        }
    }

    @Override
    public void die() {
        if (this.currentHealth <= 0) {
            useFastShakeAnimation(5.0F);
            CardCrawlGame.screenShake.rumble(4.0F);
            //onBossVictoryLogic();
        }

        AbstractRuinaCardMonster.boss = null;
        AbstractRuinaCardMonster.finishedSetup = false;
        hand.clear();
        /*
        drawPile.clear();
        discardPile.clear();
        exhaustPile.clear();
        */
        limbo.clear();
        super.die();


    }


    @Override
    protected void onFinalBossVictoryLogic() {

        //AbstractDungeon.ascensionLevel = storedAsc;
    }

    private void updateCardsOnDamage() {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            for (final AbstractCard c : this.hand.group) {
                c.tookDamage();
            }
            for (final AbstractCard c : this.discardPile.group) { c.tookDamage(); }
            for (final AbstractCard c : this.drawPile.group) { c.tookDamage(); }
        }
    }


    public void updateCardsOnDiscard() {
        for (final AbstractCard c : this.hand.group) {
            c.didDiscard();
        }
        for (final AbstractCard c : this.discardPile.group) { c.didDiscard(); }
        for (final AbstractCard c : this.drawPile.group) { c.didDiscard(); }
    }

    @Override
    public void heal(final int healAmount) {
        int amt = healAmount;
        for (final AbstractRelic r : this.relics) {
            amt = r.onPlayerHeal(amt);
        }
        super.heal(amt);
        if (this.currentHealth > this.maxHealth / 2.0f && this.isBloodied) {
            this.isBloodied = false;
            for (final AbstractRelic r : this.relics) {
                r.onNotBloodied();
            }
        }
    }

    public void preBattlePrep() {
        this.damagedThisCombat = 0;
        this.cardsPlayedThisTurn = 0;
        this.attacksPlayedThisTurn = 0;
        this.isBloodied = (this.currentHealth <= this.maxHealth / 2);
        AbstractPlayer.poisonKillCount = 0;
        this.gameHandSize = this.masterHandSize;
        this.cardInUse = null;


        this.drawPile.initializeDeck(this.masterDeck);
        this.drawPile.clear();
        CardGroup copy = new CardGroup(this.masterDeck, CardGroupType.DRAW_PILE);
        for (AbstractCard c : copy.group) { this.drawPile.addToBottom(c); }
        AbstractDungeon.overlayMenu.endTurnButton.enabled = false;
        this.hand.clear();
        this.discardPile.clear();
        this.exhaustPile.clear();
        if (this.hasRelic("SlaversCollar")) { ((SlaversCollar) this.getRelic("SlaversCollar")).beforeEnergyPrep(); }
        this.energy.prep();
        this.powers.clear();
        this.healthBarUpdatedEvent();
        this.applyPreCombatLogic();
    }

    public ArrayList<String> getRelicNames() {
        final ArrayList<String> arr = new ArrayList<String>();
        for (final AbstractRelic relic : this.relics) {
            arr.add(relic.relicId);
        }
        return arr;
    }

    public void applyPreCombatLogic() {
        for (final AbstractRelic r : this.relics) {
            if (r != null) {
                r.atPreBattle();
            }
        }
    }

    public void applyStartOfCombatLogic() {

        for (final AbstractRelic r : this.relics) {
            if (r != null) {
                r.atBattleStart();
            }
        }
    }

    public void applyStartOfCombatPreDrawLogic() {
        for (final AbstractRelic r : this.relics) {
            if (r != null) {
                r.atBattleStartPreDraw();
            }
        }
    }

    public void applyStartOfTurnRelics() {
        for (final AbstractRelic r : this.relics) {
            if (r != null) {
                r.atTurnStart();
            }
        }
    }

    public void applyStartOfTurnPostDrawRelics() {
        for (final AbstractRelic r : this.relics) {
            if (r != null) {
                r.atTurnStartPostDraw();
            }
        }
    }

    public void applyStartOfTurnPreDrawCards() {
        for (final AbstractCard c : this.hand.group) {
            if (c != null) {
                c.atTurnStartPreDraw();
            }
        }
    }

    public void applyStartOfTurnCards() {

        for (final AbstractCard c : this.drawPile.group) {
            if (c != null) {
                c.atTurnStart();
            }
        }
        for (final AbstractCard c : this.hand.group) {
            if (c != null) {
                c.atTurnStart();
                c.triggerWhenDrawn();
            }
        }
        for (final AbstractCard c : this.discardPile.group) {
            if (c != null) {
                c.atTurnStart();
            }
        }
    }

    public boolean relicsDoneAnimating() {
        for (final AbstractRelic r : this.relics) {
            if (!r.isDone) {
                return false;
            }
        }
        return true;
    }

    public void addBlock(final int blockAmount) {
        float tmp = (float) blockAmount;
        for (final AbstractRelic r : this.relics) { tmp = (float) r.onPlayerGainedBlock(tmp); }
        if (tmp > 0.0f) {
            for (final AbstractPower p : this.powers) { p.onGainedBlock(tmp); }
        }
        super.addBlock((int) Math.floor(tmp));
    }

    /////////////////////////////////////////////////////////////////////////////
    ////////////[[[[[[[[THE ALMIGHTY RENDERING]]]]]]]]///////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void render(final SpriteBatch sb) {
        super.render(sb);
        if (!this.isDead) {
            this.renderHand(sb);
            for (AbstractRelic r : this.relics) {
                r.render(sb);
            }
            this.energyPanel.render(sb);
        }
    }

    public void renderHand(final SpriteBatch sb) {
        /*if (this.hoveredCard != null) {
            int aliveMonsters = 0;
            this.hand.renderHand(sb, this.hoveredCard);
            this.hoveredCard.renderHoverShadow(sb);
            if ((this.isDraggingCard || this.inSingleTargetMode) && this.isHoveringDropZone) {
                if (this.isDraggingCard && !this.inSingleTargetMode) {
                    AbstractMonster theMonster = null;
                    for (final AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (!m.isDying && m.currentHealth > 0) {
                            ++aliveMonsters;
                            theMonster = m;
                        }
                    }
                    if (aliveMonsters == 1 && this.hoveredMonster == null) {
                        this.hoveredCard.calculateCardDamage(theMonster);
                        this.hoveredCard.render(sb);
                        this.hoveredCard.genPreview();
                    }
                    else {
                        this.hoveredCard.render(sb);
                    }
                }
                if (!AbstractDungeon.getCurrRoom().isBattleEnding()) {
                    this.renderHoverReticle(sb);
                }
            }
            if (this.hoveredMonster != null) {
                this.hoveredCard.calculateCardDamage(this.hoveredMonster);
                this.hoveredCard.render(sb);
                this.hoveredCard.genPreview();
            }
            else if (aliveMonsters != 1) {
                this.hoveredCard.render(sb);
            }
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
            this.hand.render(sb);
        }
        else {
            this.hand.renderHand(sb, this.cardInUse);
        }*/
        this.hand.renderHand(sb, this.cardInUse);
        if (this.cardInUse != null && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT && !PeekButton.isPeeking) {
            this.cardInUse.render(sb);
            if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                AbstractDungeon.effectList.add(new CardDisappearEffect(this.cardInUse.makeCopy(), this.cardInUse.current_x, this.cardInUse.current_y));
                this.cardInUse = null;
            }
        }
        this.limbo.render(sb);
    }

    private enum DrawTypes {
        Attack,
        Setup,
        EitherPhase;

        DrawTypes() { }
    }

}
