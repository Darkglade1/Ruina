package ruina.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import ruina.RuinaMod;
import ruina.cards.Melody;
import ruina.monsters.blackSilence.blackSilence3.Angelica;
import ruina.monsters.uninvitedGuests.normal.bremen.Bremen;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;
import static ruina.util.Wiz.*;

public class BlackSilence3 extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(BlackSilence3.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    boolean completed = false;

    private static final int CARD_AMOUNT_NEEDED = 3;
    private final int TURNS_FOR_WALTZ = 2;

    public BlackSilence3(AbstractCreature owner, Melody melodyCard) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, CARD_AMOUNT_NEEDED);
        amount2 = TURNS_FOR_WALTZ;
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw(){
        completed = false;
        if(amount2 - 1 == 0) {
            amount2 = -1;
            // set Activate card to Waltz.
        }
        else if(amount2 == -1) { amount2 = TURNS_FOR_WALTZ; }
        else { amount2 -= 1; }
        amount = CARD_AMOUNT_NEEDED;
    }
    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if(card.type.equals(AbstractCard.CardType.ATTACK) || card.type.equals(AbstractCard.CardType.POWER))
        {
            if(amount - 1 == 0) {
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        amount = -1;
                        completed = true;
                    }
                });
            }
            else { amount -= 1; }
        }
    }

    public void atStartOfTurn() {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flashWithoutSound();
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    if(!completed){
                        // do thing
                    }
                    isDone = true;
                }
            });
        }
    }


    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], CARD_AMOUNT_NEEDED, TURNS_FOR_WALTZ);
    }
}