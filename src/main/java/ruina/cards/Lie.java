package ruina.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.cardmods.LieMod;
import ruina.monsters.AbstractRuinaMonster;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;

public class Lie extends AbstractRuinaCard {
    public final static String ID = makeID(Lie.class.getSimpleName());
    private static final int COST = 1;

    public Lie() {
        super(ID, COST, CardType.STATUS, CardRarity.SPECIAL, CardTarget.NONE);
        exhaust = true;
    }

    public static boolean canDisguiseAs(AbstractCard target) {
        return target.cost >= 1 && !target.cardID.equals(ID) && !CardModifierManager.hasModifier(target, LieMod.ID);
    }

    public void disguise() {
        List<AbstractCard> possibilities = adp().masterDeck.group.stream().filter(Lie::canDisguiseAs).collect(Collectors.toList());
        if (!possibilities.isEmpty()) {
            CardGroup sourceGroup = findLieGroup();
            if (sourceGroup != null) {
                int index = sourceGroup.group.indexOf(Lie.this);
                sourceGroup.removeCard(Lie.this);
                Collections.shuffle(possibilities, AbstractDungeon.miscRng.random);
                AbstractCard disguise = possibilities.get(0).makeStatEquivalentCopy();
                CardModifierManager.addModifier(disguise, new LieMod(Lie.this));
                if (index > 0) {
                    sourceGroup.group.add(index, disguise);
                } else {
                    sourceGroup.addToRandomSpot(disguise);
                }
            }
        }
    }

    private CardGroup findLieGroup() {
        if (adp().drawPile.contains(this)) {
            return adp().drawPile;
        }
        if (adp().discardPile.contains(this)) {
            return adp().discardPile;
        }
        if (adp().hand.contains(this)) {
            return adp().hand;
        }
        return null;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractRuinaMonster.playSoundAnimation("PinoFail");
    }

    @Override
    public void upp() {
    }
}