package ruina.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import ruina.CustomIntent.IntentEnums;
import ruina.monsters.AbstractAllyMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.act2.HermitStaff;
import ruina.monsters.act2.Mountain;
import ruina.monsters.act3.bigBird.Sage;
import ruina.monsters.uninvitedGuests.normal.eileen.GearsWorshipper;
import ruina.monsters.uninvitedGuests.normal.philip.CryingChild;
import ruina.monsters.uninvitedGuests.normal.puppeteer.Puppet;
import ruina.util.AdditionalIntent;

public class MintyDamageSummationCompatibilityPatch {
    //patch for multi-intent enemies
    @SpirePatch(cls = "mintySpire.patches.ui.RenderIncomingDamagePatches$TIDHook", method = "hook", optional = true)
    public static class enemyPatch {
        @SpireInsertPatch(locator = enemyPatch.Locator.class, localvars = {"dmg", "c", "m"})
        public static void patch(AbstractDungeon fakeInstance, SpriteBatch sb, @ByRef int[] dmg, @ByRef int[] c, AbstractMonster m) {
            if (m instanceof AbstractMultiIntentMonster) {
                AbstractMultiIntentMonster multiIntentMonster = (AbstractMultiIntentMonster) m;
                for (AdditionalIntent additionalIntent : multiIntentMonster.additionalIntents) {
                    if (additionalIntent.targetTexture == null && additionalIntent.damage > 0) {
                        int damageSum;
                        if (additionalIntent.numHits > 0) {
                            damageSum = additionalIntent.damage * additionalIntent.numHits;
                        } else {
                            damageSum = additionalIntent.damage;
                        }
                        c[0]++;
                        dmg[0] += damageSum;
                    }
                }
                //stop minty from double counting the highest intent if the enemy isn't attacking
                //with their main intent
                if (multiIntentMonster.getRealIntentBaseDmg() < 0) {
                    dmg[0] -= multiIntentMonster.getIntentDmg();
                    c[0]--;
                }
            }
            //hardcode these cases where the enemy attacks NPC with primary intent
            if (m instanceof Mountain) {
                if (((Mountain) m).currentStage == Mountain.STAGE1) {
                    dmg[0] -= m.getIntentDmg();
                    c[0]--;
                }
            }
            if (m instanceof HermitStaff) {
                dmg[0] -= m.getIntentDmg();
                c[0]--;
            }
            if (m instanceof Puppet) {
                if (((Puppet) m).attackingAlly) {
                    int damage = m.getIntentDmg();
                    if ((Boolean) ReflectionHacks.getPrivate(m, AbstractMonster.class, "isMultiDmg")) {
                        damage *= (Integer)ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
                    }
                    dmg[0] -= damage;
                    c[0]--;
                }
            }
            if (m instanceof CryingChild) {
                if (((CryingChild) m).attackingAlly) {
                    int damage = m.getIntentDmg();
                    if ((Boolean) ReflectionHacks.getPrivate(m, AbstractMonster.class, "isMultiDmg")) {
                        damage *= (Integer)ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
                    }
                    dmg[0] -= damage;
                    c[0]--;
                }
            }
            if (m instanceof GearsWorshipper) {
                if (((GearsWorshipper) m).attackingAlly) {
                    int damage = m.getIntentDmg();
                    if ((Boolean) ReflectionHacks.getPrivate(m, AbstractMonster.class, "isMultiDmg")) {
                        damage *= (Integer)ReflectionHacks.getPrivate(m, AbstractMonster.class, "intentMultiAmt");
                    }
                    dmg[0] -= damage;
                    c[0]--;
                }
            }

            //hardcode this ally who isn't halfdead and thus not ignored by default
            if (m instanceof Sage) {
                dmg[0] -= m.getIntentDmg();
                c[0]--;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "getIntentDmg");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    //patch for allies with mass attacks
    @SpirePatch(cls = "mintySpire.patches.ui.RenderIncomingDamagePatches$TIDHook", method = "hook", optional = true)
    public static class allyPatch {
        @SpireInsertPatch(locator = allyPatch.Locator.class, localvars = {"dmg", "c"})
        public static void patch(AbstractDungeon fakeInstance, SpriteBatch sb, @ByRef int[] dmg, @ByRef int[] c) {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (mo instanceof AbstractAllyMonster) {
                    if (mo.intent == IntentEnums.MASS_ATTACK && ((AbstractAllyMonster) mo).massAttackHitsPlayer) {
                        int tmp;
                        c[0]++;
                        int multiAmt;
                        multiAmt = ReflectionHacks.getPrivate(mo, AbstractMonster.class, "intentMultiAmt");
                        tmp = mo.getIntentDmg();
                        if (multiAmt > 1) {
                            tmp *= multiAmt;
                        }
                        if (tmp > 0) {
                            dmg[0] += tmp;
                        }
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getMonsters");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

}