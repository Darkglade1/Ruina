package ruina.monsters.theHead;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.BetterTalkAction;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Chain;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Fairy;
import ruina.monsters.uninvitedGuests.normal.elena.binahCards.Pillar;
import ruina.powers.act5.SingularityF;
import ruina.vfx.WaitEffect;

import static ruina.util.Wiz.applyToTarget;
import static ruina.util.Wiz.atb;

public class BinahHead extends Binah {
    public Baral baral;
    public Zena zena;

    private boolean usedPreBattleAction = false;

    public BinahHead(final float x, final float y) {
        super(x, y);
        addMove(DEGRADED_PILLAR, Intent.ATTACK_DEFEND, 30);
        addMove(DEGRADED_CHAIN, Intent.ATTACK_DEBUFF, 24);
        addMove(DEGRADED_FAIRY, Intent.ATTACK, 16, fairyHits);

        cardList.clear();

        AbstractCard pillar = new Pillar(this);
        pillar.upgrade();
        cardList.add(pillar);

        AbstractCard chain = new Chain(this);
        chain.upgrade();
        cardList.add(chain);

        AbstractCard fairy = new Fairy(this);
        fairy.upgrade();
        cardList.add(fairy);
    }

    @Override
    public void usePreBattleAction() {
        if (!usedPreBattleAction) {
            usedPreBattleAction = true;
            addPower(new SingularityF(this, 1));
            super.usePreBattleAction();
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (mo instanceof Baral) {
                    target = baral = (Baral) mo;
                }
                if (mo instanceof Zena) {
                    zena = (Zena) mo;
                }
            }
        }
    }

    public void onEntry() {
        animationAction("Blunt", "BinahArrive", null, this);
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CustomDungeon.playTempMusicInstantly("Binah3");
                isDone = true;
            }
        });
        waitAnimation(1.0f);
    }

    public void dialogue() {
    }

    public void onBossDeath() {
        if (!isDead && !isDying) {
            atb(new BetterTalkAction(this, DIALOG[2], true));
            atb(new VFXAction(new WaitEffect(), 1.0F));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    disappear();
                    this.isDone = true;
                }
            });
        }
    }

}
