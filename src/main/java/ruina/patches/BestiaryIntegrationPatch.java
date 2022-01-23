package ruina.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpirePatch(
        cls = "Bestiary.database.MonsterDatabase",
        method = "load",
        optional = true
)
public class BestiaryIntegrationPatch {
    private static final String MONSTERS_FILE = "ruinaResources/bestiary/bestiary.json";
    public static final Logger logger = LogManager.getLogger(BestiaryIntegrationPatch.class.getName());

    @SpireInsertPatch(locator = BestiaryIntegrationPatch.Locator.class, localvars = {"arr"})
    public static void AddMonsters(Object __instance, JsonArray arr) {
        logger.info("Loading Bestiary entries for modded monsters");
        InputStream in = BestiaryIntegrationPatch.class.getClassLoader().getResourceAsStream(MONSTERS_FILE);

        if (in == null) {
            logger.error("Failed to load bestiary.json (not found?)");
            return;
        }

        try {
            String content = resourceStreamToString(in);

            JsonObject o = new JsonParser().parse(content).getAsJsonObject();

            if (o.has("monsters") && o.get("monsters").isJsonArray()) {
                JsonArray additionalMonsters = o.getAsJsonArray("monsters");
                logger.info("Adding additional monsters: " + additionalMonsters.size());

                arr.addAll(additionalMonsters);
            }
        } catch (IOException e) {
            logger.error("Failed to load resource stream to string");
            e.printStackTrace();
        }
    }
    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(JsonObject.class, "getAsJsonArray");
            int[] lines = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            // We need to insert on the line after the call, so add 1
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i] + 1;
            }
            return lines;
        }
    }

    // Builds a string from an input stream
    private static String resourceStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String read;
        while ((read=br.readLine()) != null) {
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }
}