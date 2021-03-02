package ruina.monsters.eventBoss.core.actions.util;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;
import ruina.monsters.AbstractRuinaCardMonster;

import java.util.ArrayList;

public class EnemyDrawCardAction extends AbstractGameAction {
    public static ArrayList<AbstractCard> drawnCards;

    static {
        EnemyDrawCardAction.drawnCards = new ArrayList<AbstractCard>();
    }

    private boolean shuffleCheck;
    private boolean clearDrawHistory;
    private AbstractGameAction followUpAction;
    private AbstractRuinaCardMonster boss;

    public EnemyDrawCardAction(final AbstractRuinaCardMonster target, final AbstractCreature source, final int amount, final boolean endTurnDraw) {
        this.shuffleCheck = false;
        this.clearDrawHistory = true;
        this.followUpAction = null;
        if (endTurnDraw) {
            AbstractDungeon.topLevelEffects.add(new PlayerTurnEffect());
        }
        this.setValues(target, source, amount);
        this.boss = target;
        this.actionType = ActionType.DRAW;
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FASTER;
        }
    }

    public EnemyDrawCardAction(final AbstractRuinaCardMonster boss, final int amount) {
        this(boss, boss, amount, false);
    }

    public EnemyDrawCardAction(final AbstractRuinaCardMonster boss, final int amount, final boolean clearDrawHistory) {
        this(boss, amount);
        this.clearDrawHistory = clearDrawHistory;
    }

    public EnemyDrawCardAction(final AbstractRuinaCardMonster boss, final int amount, final AbstractGameAction action) {
        this(boss, amount, action, true);
    }

    public EnemyDrawCardAction(final AbstractRuinaCardMonster boss, final int amount, final AbstractGameAction action, final boolean clearDrawHistory) {
        this(boss, amount, clearDrawHistory);
        this.followUpAction = action;
    }

    @Override
    public void update() {
        if (this.clearDrawHistory) {
            this.clearDrawHistory = false;
            EnemyDrawCardAction.drawnCards.clear();
        }
        if (this.boss.hasPower("No Draw")) {
            this.boss.getPower("No Draw").flash();
            this.endActionWithFollowUp();
            return;
        }
        if (this.amount <= 0) {
            this.endActionWithFollowUp();
            return;
        }
        final int deckSize = this.boss.chosenArchetype.getDraw().size();
        final int discardSize = this.boss.chosenArchetype.getDiscard().size();
        if (SoulGroup.isActive()) {
            return;
        }
        if (deckSize + discardSize == 0) {
            this.endActionWithFollowUp();
            return;
        }
        if (this.boss.hand.size() == 10) {
            this.endActionWithFollowUp();
            return;
        }
        if (!this.shuffleCheck) {
            if (this.amount + this.boss.hand.size() > 10) {
                final int handSizeAndDraw = 10 - (this.amount + this.boss.hand.size());
                this.amount += handSizeAndDraw;
            }
            if (this.amount > deckSize) {
                final int tmp = this.amount - deckSize;
                this.addToTop(new EnemyDrawCardAction(this.boss, tmp, this.followUpAction, false));
                this.addToTop(new EnemyEmptyShuffleDeckAction(this.boss));
                if (deckSize != 0) {
                    this.addToTop(new EnemyDrawCardAction(this.boss, deckSize, false));
                }
                this.amount = 0;
                this.isDone = true;
                return;
            }
            this.shuffleCheck = true;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.amount != 0 && this.duration < 0.0f) {
            if (Settings.FAST_MODE) {
                this.duration = Settings.ACTION_DUR_XFAST;
            } else {
                this.duration = Settings.ACTION_DUR_FASTER;
            }
            --this.amount;
            if (!this.boss.chosenArchetype.getDraw().isEmpty()) {
                EnemyDrawCardAction.drawnCards.add(this.boss.chosenArchetype.getDraw().getTopCard());
                this.boss.chosenArchetype.draw();
                this.boss.hand.refreshHandLayout();
            } else {
                //EnemyDrawCardAction.logger.warn("Player attempted to draw from an empty drawpile mid-DrawAction?MASTER DECK: " + AbstractDungeon.player.masterDeck.getCardNames());
                this.endActionWithFollowUp();
            }
            if (this.amount == 0) {
                this.endActionWithFollowUp();
            }
        }
    }

    private void endActionWithFollowUp() {
        this.isDone = true;
        if (this.followUpAction != null) {
            this.addToTop(this.followUpAction);
        }
    }
}
