package ruina.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import ruina.monsters.uninvitedGuests.normal.greta.FreshMeat;
import ruina.monsters.uninvitedGuests.normal.greta.Greta;

import static ruina.util.Wiz.*;

public class GretaStealCardAction extends AbstractGameAction {
    private Greta owner;
    private float startingDuration;
    private AbstractCard card = null;

    public GretaStealCardAction(Greta owner) {
        this.owner = owner;// 18
        this.duration = Settings.ACTION_DUR_LONG;// 19
        this.startingDuration = Settings.ACTION_DUR_LONG;// 20
        this.actionType = ActionType.WAIT;// 21
    }// 22

    public void update() {
        if ((AbstractDungeon.player.drawPile.isEmpty() && AbstractDungeon.player.discardPile.isEmpty()) || owner.isDeadOrEscaped()) {// 26
            this.isDone = true;// 27
        } else {
            if (this.duration == this.startingDuration) {// 31
                if (AbstractDungeon.player.drawPile.isEmpty()) {// 32
                    this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.RARE);// 33
                    if (this.card == null) {// 36
                        this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.UNCOMMON);// 37
                        if (this.card == null) {// 40
                            this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.COMMON);// 41
                            if (this.card == null) {// 44
                                this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng);// 45
                            }
                        }
                    }

                    AbstractDungeon.player.discardPile.removeCard(this.card);// 49
                } else {
                    this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.RARE);// 51
                    if (this.card == null) {// 54
                        this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.UNCOMMON);// 55
                        if (this.card == null) {// 58
                            this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, CardRarity.COMMON);// 59
                            if (this.card == null) {// 62
                                this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng);// 63
                            }
                        }
                    }

                    AbstractDungeon.player.drawPile.removeCard(this.card);// 67
                }

                AbstractDungeon.player.limbo.addToBottom(this.card);// 70
                this.card.setAngle(0.0F);// 71
                this.card.targetDrawScale = 0.75F;// 72
                this.card.target_x = (float)Settings.WIDTH / 2.0F;// 73
                this.card.target_y = (float)Settings.HEIGHT / 2.0F;// 74
                this.card.lighten(false);// 75
                this.card.unfadeOut();// 76
                this.card.unhover();// 77
                this.card.untip();// 78
                this.card.stopGlowing();// 79
            }

            this.tickDuration();// 82
            if (this.isDone && this.card != null) {// 83
                FreshMeat meat = new FreshMeat(-150.0f, 0.0f);
                att(new ShowCardAction(this.card));// 85
                applyToTargetTop(meat, meat, new ruina.powers.act4.FreshMeat(meat, meat.HEAL, card));
                att(new SpawnMonsterAction(meat, true));
            }

        }
    }// 28 87
}
