package ruina.cards.EGO.act1;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.actions.OurGalaxyAction;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class OurGalaxy extends AbstractEgoCard {
    public final static String ID = makeID(OurGalaxy.class.getSimpleName());

    public OurGalaxy() {
        super(ID, -1, CardType.SKILL, CardTarget.NONE);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                int effect = EnergyPanel.totalCount;
                if (energyOnUse != -1) {
                    effect = energyOnUse;
                }

                if (adp().hasRelic(ChemicalX.ID)) {
                    effect += 2;
                    adp().getRelic(ChemicalX.ID).flash();
                }

                if (effect >= 0) {
                    att(new OurGalaxyAction(effect));

                    if (!freeToPlayOnce) {
                        adp().energy.use(EnergyPanel.totalCount);
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        exhaust = false;
    }
}