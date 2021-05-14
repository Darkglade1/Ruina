package ruina.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import ruina.powers.PlayerBlackSilence;
import ruina.relics.d49.Director;

import java.util.Iterator;

import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.att;

public class DirectorRelicPatch {
    @SpirePatch2(
            clz = EnergyManager.class,
            method="recharge",
            optional = true
    )
    public static class EnergyGoByeBye {
        public static SpireReturn Prefix(EnergyManager __instance) {
            for(AbstractRelic r: adp().relics){
                if(r instanceof Director){
                        if (EnergyPanel.totalCount > 0) {
                            r.flash();
                            att(new RelicAboveCreatureAction(adp(), r));
                        }
                        EnergyPanel.addEnergy(1);
                        return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }


}