package ruina.cards.EGO.act2.FadedMemories;

import basemod.AutoAdd;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.UUID;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

@AutoAdd.Ignore
public class FadedMemories2 extends FadedMemories {
    public final static String ID = makeID(FadedMemories2.class.getSimpleName());

    private UUID parentID;

    public FadedMemories2(UUID parentID) {
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
        this.parentID = parentID;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    public void onChoseThisOption(){
        atb(new DrawCardAction(adp(), secondMagicNumber));
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
            if (action instanceof UseCardAction) {
                UseCardAction useCardAction = (UseCardAction) action;
                AbstractCard card = ReflectionHacks.getPrivate(useCardAction, UseCardAction.class, "targetCard");
                if (card.uuid == parentID) {
                    useCardAction.exhaustCard = true;
                }
            }
        }
    }
}