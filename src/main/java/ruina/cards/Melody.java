package ruina.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.powers.MelodyPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.monsterList;

@AutoAdd.Ignore
public class Melody extends AbstractRuinaCard {
    public final static String ID = makeID(Melody.class.getSimpleName());

    public Melody() {
        super(ID, -2, CardType.CURSE, CardRarity.CURSE, CardTarget.NONE, CardColor.CURSE);
        selfRetain = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    public void upp() {

    }

    @Override
    public void triggerOnExhaust() {
        Melody oldCard = this;
        atb(new MakeTempCardInHandAction(this));
        atb(new AbstractGameAction() {
            public void update() {
                Melody newCard = null;
                for (AbstractCard card : AbstractDungeon.player.hand.group) {
                    if (card instanceof Melody) {
                        newCard = (Melody)card;
                    }
                }
                if (newCard != null) {
                    for (AbstractMonster mo : monsterList()) {
                        if (mo.hasPower(MelodyPower.POWER_ID)) {
                            MelodyPower power = (MelodyPower)mo.getPower(MelodyPower.POWER_ID);
                            power.pointToNewCard(newCard);
                        }
                    }
                }
                AbstractDungeon.player.exhaustPile.removeCard(oldCard);
                this.isDone = true;
            }
        });
    }
}