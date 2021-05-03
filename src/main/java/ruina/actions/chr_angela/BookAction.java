package ruina.actions.chr_angela;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.cards.abs_angela_card;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ruina.RuinaMod.*;

public class BookAction extends AbstractGameAction {
    private final Predicate<AbstractCard> filterCriteria;
    private final AbstractGameAction followUpAction;
    public static final ArrayList<AbstractCard> bookedCards = new ArrayList<>();
    private final Predicate<AbstractCard> noFreezeFilter = c -> !(c instanceof abs_angela_card || !(((abs_angela_card) c).unbookable));
    private AbstractCard card;

    public BookAction(int amount, Predicate<AbstractCard> filterFn, AbstractGameAction followUpAction) {
        super();
        setValues(AbstractDungeon.player, AbstractDungeon.player, amount);
        startDuration = duration = Settings.ACTION_DUR_FASTER;
        filterCriteria = filterFn;
        this.followUpAction = followUpAction;
    }

    public BookAction(int amount, Predicate<AbstractCard> filterFn) {
        this(amount, filterFn, null);
    }

    public BookAction(int amount) {
        this(amount, null, null);
    }

    public BookAction(AbstractCard specificCard) {
        this(-1, null, null);
        card = specificCard;
    }

    private static Optional<CardGroup> findCurrentGroup(AbstractCard card) {
        AbstractPlayer p = AbstractDungeon.player;
        CardGroup[] groups = {
                bookPile,
                p.drawPile,
                p.hand,
                p.discardPile,
                p.limbo
        };
        return Arrays.stream(groups).filter(cg -> cg.contains(card)).findFirst();
    }

    @Override
    public void update() {
        if (duration == startDuration) {
            isDone = true;

            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
                return;
            }

            CardGroup drawPile = AbstractDungeon.player.drawPile;
            if (card != null) {
                Optional<CardGroup> currentGroup = findCurrentGroup(card);
                if (currentGroup.isPresent()) {
                    cardsToBook.add(card);
                    currentGroup.get().moveToDeck(card, false);
                    cardsToBook.remove(card);
                } else {
                    AbstractCard newCard = card.makeSameInstanceOf();
                    // add some book logic later.


                    bookPile.addToTop(newCard);
                    bookedThisCombat.add(newCard.makeStatEquivalentCopy());
                }
            } else {
                bookedCards.clear();
                List<AbstractCard> eligibleCards = drawPile.group.stream()
                        .filter(noFreezeFilter)
                        .filter(Optional.ofNullable(filterCriteria).orElseGet(() -> c -> true))
                        .collect(Collectors.toList());
                Collections.reverse(eligibleCards);
                eligibleCards.stream()
                        .limit(amount)
                        .forEachOrdered(c -> {
                            cardsToBook.add(c);
                            bookedCards.add(c);
                        });
                bookedCards.forEach(c -> drawPile.moveToDeck(c, false));
                cardsToBook.removeIf(bookedCards::contains);
                endActionWithFollowUp();
            }
        }
    }

    private void endActionWithFollowUp() {
        isDone = true;
        if (followUpAction != null) {
            addToTop(followUpAction);
        }
    }

}