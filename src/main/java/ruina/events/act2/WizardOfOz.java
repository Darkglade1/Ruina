package ruina.events.act2;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Clumsy;
import com.megacrit.cardcrawl.cards.red.Bludgeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.IceCream;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import ruina.RuinaMod;
import ruina.cardmods.DamageUpMod;
import ruina.cardmods.ExhaustMod;
import ruina.cards.FalseThrone;
import ruina.relics.FalsePresent;

import static ruina.RuinaMod.makeEventPath;
import static ruina.util.Wiz.adp;

public class WizardOfOz extends AbstractImageEvent {

    public static final String ID = RuinaMod.makeID("WizardOfOz");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("Wizard.png");

    private static final String BEGINNING = DESCRIPTIONS[0];
    private static final String SCARECROW = DESCRIPTIONS[1];
    private static final String WOODSMAN = DESCRIPTIONS[2];
    private static final String LION = DESCRIPTIONS[3];
    private static final String GIRL = DESCRIPTIONS[4];
    private static final String ALL_ACCEPT = DESCRIPTIONS[5];
    private static final String ALL_REFUSE = DESCRIPTIONS[6];
    private static final String MIX_RESPONSE = DESCRIPTIONS[7];
    private static final String STORY_END = DESCRIPTIONS[8];
    private static final String LEAVE_TEXT = DESCRIPTIONS[9];

    private static final String READ = OPTIONS[0];
    private static final String LEAVE_OPTION = OPTIONS[1];
    private static final String ACCEPT = OPTIONS[2];
    private static final String REFUSE = OPTIONS[3];
    private static final String REMOVE_CARD = OPTIONS[4];
    private static final String CURSED = OPTIONS[5];
    private static final String LOSE = OPTIONS[6];
    private static final String HP = OPTIONS[7];
    private static final String GAIN = OPTIONS[8];
    private static final String MAX_HP = OPTIONS[9];
    private static final String GOLD = OPTIONS[10];
    private static final String OBTAIN_POTION = OPTIONS[11];
    private static final String GIVE_CARD = OPTIONS[12];
    private static final String DAMAGE = OPTIONS[13];
    private static final String EXHAUST = OPTIONS[14];
    private static final String ACCEPT_PRESENT = OPTIONS[15];
    private static final String OBTAIN_RELIC = OPTIONS[16];
    private static final String OVERTHROW = OPTIONS[17];
    private static final String OBTAIN = OPTIONS[18];
    private static final String STOP_READING = OPTIONS[19];
    private static final String PERIOD = OPTIONS[20];
    private static final String LOCKED_ACCEPT = OPTIONS[21];
    private static final String LOCKED_REFUSE = OPTIONS[22];
    private static final String CHOOSE_CARD = OPTIONS[23];

    private static final int OPENING_SCREEN = 0;
    private static final int SCARECROW_SCENE = 1;
    private static final int WOODSMAN_SCENE = 2;
    private static final int CAT_SCENE = 3;
    private static final int GIRL_SCENE = 4;
    private static final int FINAL_SCENE = 5;
    private static final int END_SCENE = 6;
    private static final int LEAVE_SCENE = 7;

    private AbstractCard curse = new Clumsy();
    private static final int HP_LOSS = 6;
    private static final int MAX_HP_GAIN = 2;
    private static final int GOLD_LOSS = 50;
    private static final int NUM_POTIONS = 1;
    private static final int DAMAGE_BOOST = 1;
    private AbstractCardModifier cardModifier = new ExhaustMod();
    private AbstractCardModifier cardModifier2 = new DamageUpMod(DAMAGE_BOOST);
    private AbstractCard cardReward = new FalseThrone();
    private AbstractRelic relicReward = new FalsePresent();

    private int num_refusals = 0;
    private int num_accepts = 0;
    private int goldLoss = GOLD_LOSS;
    private boolean removeCard = false;
    private boolean damageUpCard = false;
    private int screenNum = 0;

    public WizardOfOz() {
        super(NAME, BEGINNING, IMG);
        noCardsInRewards = true;
        this.imageEventText.setDialogOption(READ, cardReward, relicReward);
        this.imageEventText.setDialogOption(LEAVE_OPTION);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case OPENING_SCREEN:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(SCARECROW);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(ACCEPT + FontHelper.colorString(REMOVE_CARD, "g") + " " + FontHelper.colorString(CURSED + curse.name + PERIOD, "r"), relicReward);
                        this.imageEventText.setDialogOption(REFUSE, cardReward);
                        screenNum = SCARECROW_SCENE;
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(LEAVE_TEXT);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(LEAVE_OPTION);
                        screenNum = LEAVE_SCENE;
                        break;
                }
                break;
            case SCARECROW_SCENE:
                this.imageEventText.updateBodyText(WOODSMAN);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(ACCEPT + FontHelper.colorString(LOSE + HP_LOSS + HP, "r") + " " + FontHelper.colorString(GAIN + MAX_HP_GAIN + MAX_HP, "g"), relicReward);
                this.imageEventText.setDialogOption(REFUSE, cardReward);
                screenNum = WOODSMAN_SCENE;
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, REMOVE_CARD, false);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        removeCard = true;
                        num_accepts++;
                        break;
                    case 1:
                        num_refusals++;
                        break;
                }
                break;
            case WOODSMAN_SCENE:
                this.imageEventText.updateBodyText(LION);
                this.imageEventText.clearAllDialogs();
                if (adp().gold < goldLoss) {
                    goldLoss = adp().gold;
                }
                this.imageEventText.setDialogOption(ACCEPT + FontHelper.colorString(LOSE + goldLoss + GOLD, "r") + " " + FontHelper.colorString(OBTAIN_POTION, "g"), relicReward);
                this.imageEventText.setDialogOption(REFUSE, cardReward);
                screenNum = CAT_SCENE;
                switch (buttonPressed) {
                    case 0:
                        CardCrawlGame.sound.play("BLUNT_FAST");  // Play a hit sound
                        adp().damage(new DamageInfo(null, HP_LOSS));
                        adp().increaseMaxHp(MAX_HP_GAIN, true);
                        num_accepts++;
                        break;
                    case 1:
                        num_refusals++;
                        break;
                }
                break;
            case CAT_SCENE:
                this.imageEventText.updateBodyText(GIRL);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(ACCEPT + FontHelper.colorString(GIVE_CARD + DAMAGE_BOOST + DAMAGE + EXHAUST, "g"), relicReward);
                this.imageEventText.setDialogOption(REFUSE, cardReward);
                screenNum = GIRL_SCENE;
                switch (buttonPressed) {
                    case 0:
                        adp().loseGold(goldLoss);
                        AbstractDungeon.getCurrRoom().rewards.clear();
                        for (int i = 0; i < NUM_POTIONS; i++) {
                            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(PotionHelper.getRandomPotion()));
                        }
                        AbstractDungeon.combatRewardScreen.open();
                        num_accepts++;
                        break;
                    case 1:
                        num_refusals++;
                        break;
                }
                break;
            case GIRL_SCENE:
                AbstractDungeon.combatRewardScreen.clear(); //clears potion if they don't take it
                switch (buttonPressed) {
                    case 0:
                        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, CHOOSE_CARD, false);
                        damageUpCard = true;
                        num_accepts++;
                        setUpFinalScene();
                        break;
                    case 1:
                        num_refusals++;
                        setUpFinalScene();
                        break;
                }
                break;
            case FINAL_SCENE:
                this.imageEventText.updateBodyText(STORY_END);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(LEAVE_OPTION);
                screenNum = END_SCENE;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                switch (buttonPressed) {
                    case 0:
                        if (!adp().hasRelic(relicReward.relicId)) {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, relicReward);
                        } else {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, new Circlet());
                        }
                        break;
                    case 1:
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(cardReward, (float)Settings.WIDTH * 0.3F, (float)Settings.HEIGHT / 2.0F));
                        break;
                    case 2:
                        break;
                }
                break;
            default:
                this.openMap();
        }
    }

    private void setUpFinalScene() {
        if (num_accepts > num_refusals) {
            this.imageEventText.updateBodyText(ALL_ACCEPT);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(ACCEPT_PRESENT + FontHelper.colorString(OBTAIN_RELIC, "g"), relicReward);
            this.imageEventText.setDialogOption(LOCKED_REFUSE, true);
        } else if (num_refusals > num_accepts) {
            this.imageEventText.updateBodyText(ALL_REFUSE);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(LOCKED_ACCEPT, true);
            this.imageEventText.setDialogOption(OVERTHROW + FontHelper.colorString(OBTAIN + cardReward.name + PERIOD, "g"), cardReward);
        } else {
            this.imageEventText.updateBodyText(MIX_RESPONSE);
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(LOCKED_ACCEPT, true);
            this.imageEventText.setDialogOption(LOCKED_REFUSE, true);
        }
        this.imageEventText.setDialogOption(STOP_READING);
        screenNum = FINAL_SCENE;
    }

    @Override
    public void update() {
        super.update();
        if (removeCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                    AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                    AbstractDungeon.player.masterDeck.removeCard(c);
                    AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
                }
                removeCard = false;
            }
        }
        if (damageUpCard) {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (int i = 0; i < AbstractDungeon.gridSelectScreen.selectedCards.size(); i++) {
                    AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(i);
                    CardModifierManager.addModifier(c, cardModifier);
                    CardModifierManager.addModifier(c, cardModifier2);
                    AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
                }
                damageUpCard = false;
            }
        }
    }

}
