package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import ruina.monsters.act1.blackSwan.BlackSwan;
import ruina.monsters.act3.bigBird.BigBird;

// A patch to make random effects always hit big bird
public class RandomHitBigBirdPatch {

    @SpirePatch(
            clz = MonsterGroup.class,
            method = "getRandomMonster",
            paramtypez = {
                    AbstractMonster.class,
                    boolean.class,
                    Random.class
            }
    )
    public static class HitBigBirdPatch1 {
        @SpirePostfixPatch()
        public static AbstractMonster HitBigBird(AbstractMonster original, MonsterGroup instance, AbstractMonster exception, boolean aliveOnly, Random rng) {
            for (AbstractMonster mo : instance.monsters) {
                if (mo instanceof BigBird || mo instanceof BlackSwan) {
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
    public static class HitBigBirdPatch2 {
        @SpirePostfixPatch()
        public static AbstractMonster HitBigBird(AbstractMonster original, MonsterGroup instance, AbstractMonster exception, boolean aliveOnly) {
            for (AbstractMonster mo : instance.monsters) {
                if (mo instanceof BigBird || mo instanceof BlackSwan) {
                    if (!mo.isDeadOrEscaped()) {
                        return mo;
                    }
                }
            }
            return original;
        }
    }


}