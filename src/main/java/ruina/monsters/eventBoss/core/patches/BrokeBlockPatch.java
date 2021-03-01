package ruina.monsters.eventBoss.core.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import ruina.monsters.AbstractRuinaCardMonster;
import ruina.monsters.eventBoss.core.AbstractRuinaBossRelic;

@SpirePatch(clz = AbstractCreature.class, method = "brokeBlock")
public class BrokeBlockPatch {

    @SpirePrefixPatch
    public static void Prefix(AbstractCreature instance) {

        if (instance instanceof AbstractPlayer) {
            if (AbstractDungeon.getMonsters().monsters.size() > 0){
                if (AbstractDungeon.getMonsters().monsters.get(0) instanceof AbstractRuinaCardMonster) {
                    AbstractRuinaCardMonster cB = (AbstractRuinaCardMonster)AbstractDungeon.getMonsters().monsters.get(0);

                    for (AbstractRuinaBossRelic abstractCharbossRelic : cB.relics) {
                        AbstractRelic r = abstractCharbossRelic;
                        r.onBlockBroken(instance);
                    }
                }
            }

        }
    }
}