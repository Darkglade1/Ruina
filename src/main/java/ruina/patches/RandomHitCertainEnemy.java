package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import ruina.monsters.act1.blackSwan.BlackSwan;
import ruina.monsters.act3.bigBird.BigBird;
import ruina.powers.CenterOfAttention;

// A patch to make random effects always hit certain enemies in certain encounters
public class RandomHitCertainEnemy {

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "getRandomMonster",
            paramtypez = {
                    AbstractMonster.class,
                    boolean.class,
                    Random.class
            }
    )
    public static class HitThisGuyPatch1 {
        @SpirePostfixPatch()
        public static AbstractMonster HitThisGuy(AbstractMonster original, MonsterGroup instance, AbstractMonster exception, boolean aliveOnly, Random rng) {
            for (AbstractMonster mo : instance.monsters) {
                if (mo instanceof BigBird || mo instanceof BlackSwan || mo.hasPower(CenterOfAttention.POWER_ID)) {
                    if (!mo.isDeadOrEscaped()) {
                        return mo;
                    }
                }
            }
            return original;
        }
    }

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "getRandomMonster",
            paramtypez = {
                    AbstractMonster.class,
                    boolean.class
            }
    )
    public static class HitThisGuyPatch2 {
        @SpirePostfixPatch()
        public static AbstractMonster HitThisGuy(AbstractMonster original, MonsterGroup instance, AbstractMonster exception, boolean aliveOnly) {
            for (AbstractMonster mo : instance.monsters) {
                if (mo instanceof BigBird || mo instanceof BlackSwan || mo.hasPower(CenterOfAttention.POWER_ID)) {
                    if (!mo.isDeadOrEscaped()) {
                        return mo;
                    }
                }
            }
            return original;
        }
    }


}