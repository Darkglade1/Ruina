package ruina.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import ruina.RuinaMod;
import ruina.cards.Melody;
import ruina.monsters.uninvitedGuests.bremen.Bremen;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.applyToTarget;

public class MelodyPower extends AbstractEasyPower {
    public static final String POWER_ID = RuinaMod.makeID("Melody");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private final ArrayList<AbstractCard.CardType> sequence = new ArrayList<>();
    private final ArrayList<AbstractCard.CardType> currentProgress = new ArrayList<>();
    boolean completed = false;
    private Melody melodyCard;
    private Bremen bremen;
    private final int MELODY_PLAYER_STR;
    private final int MELODY_BOSS_STR;

    public MelodyPower(AbstractCreature owner, int amount, int playerStr, int bossStr, Melody melodyCard, Bremen bremen) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, amount);
        MELODY_BOSS_STR = bossStr;
        MELODY_PLAYER_STR = playerStr;
        this.melodyCard = melodyCard;
        this.bremen = bremen;
        generateSequence();
    }

    @Override
    public void onInitialApplication() {

    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (!completed) {
            currentProgress.add(card.type);
            boolean correct = true;
            for (int i = 0; i < currentProgress.size(); i++) {
                AbstractCard.CardType sequenceType = sequence.get(i);
                AbstractCard.CardType progressType = currentProgress.get(i);
                if (sequenceType != progressType) {
                    currentProgress.clear();
                    correct = false;
                    break;
                }
            }
            if (correct && currentProgress.size() == sequence.size()) {
                completed = true;
            }
        }
    }

    @Override
    public void atEndOfRound() {
        flash();
        if (completed) {
            applyToTarget(adp(), adp(), new StrengthPower(adp(), MELODY_PLAYER_STR));
        } else {
            applyToTarget(owner, owner, new StrengthPower(owner, MELODY_BOSS_STR));
        }
        generateSequence();
    }

    private void generateSequence() {
        completed = false;
        boolean pickedPowerAlready = false;
        sequence.clear();
        currentProgress.clear();
        for (int i = 0; i < amount; i++) {
            //don't include powers for now
            if (AbstractDungeon.monsterRng.randomBoolean(1.0f) && !pickedPowerAlready) {
                if (AbstractDungeon.monsterRng.randomBoolean(0.5f)) {
                    sequence.add(AbstractCard.CardType.ATTACK);
                } else {
                    sequence.add(AbstractCard.CardType.SKILL);
                }
            } else {
                pickedPowerAlready = true;
                sequence.add(AbstractCard.CardType.POWER);
            }
        }
        updateMelodyText();
    }

    private void updateMelodyText() {
        String[] descriptions = languagePack.getCardStrings(ruina.cards.Melody.ID).EXTENDED_DESCRIPTION;
        melodyCard.rawDescription = descriptions[0];
        String ATTACK = descriptions[1];
        String SKILL = descriptions[2];
        String POWER = descriptions[3];
        String COLORED_ATTACK = descriptions[5];
        String COLORED_SKILL = descriptions[6];
        String COLORED_POWER = descriptions[7];
        for (int i = 0; i < currentProgress.size(); i++) {
            AbstractCard.CardType type = currentProgress.get(i);
            if (type == AbstractCard.CardType.ATTACK) {
                melodyCard.rawDescription += COLORED_ATTACK;
            } else if (type == AbstractCard.CardType.SKILL) {
                melodyCard.rawDescription += COLORED_SKILL;
            } else {
                melodyCard.rawDescription += COLORED_POWER;
            }
        }
        if (currentProgress.size() < sequence.size()) {
            melodyCard.rawDescription += "*";
            for (int i = currentProgress.size(); i < sequence.size(); i++) {
                AbstractCard.CardType type = sequence.get(i);
                if (type == AbstractCard.CardType.ATTACK) {
                    melodyCard.rawDescription += ATTACK;
                } else if (type == AbstractCard.CardType.SKILL) {
                    melodyCard.rawDescription += SKILL;
                } else {
                    melodyCard.rawDescription += POWER;
                }
            }
        }
        if (completed) {
            melodyCard.rawDescription += descriptions[4];
        }
        melodyCard.initializeDescription();
    }

    public void pointToNewCard(Melody card) {
        this.melodyCard = card;
        bremen.melodyCard = card;
        updateMelodyText();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + MELODY_PLAYER_STR + DESCRIPTIONS[1] + MELODY_BOSS_STR + DESCRIPTIONS[2];
    }
}
