package ruina.patches;


import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;

@SpirePatch(clz = ProceedButton.class, method = "update")
public class ProceedButtonPatch {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(Instanceof i) throws CannotCompileException {
                try {
                    if (i.getType().getName().equals(Mushrooms.class.getName())) {
                        i.replace("$_ = $proceed($$) || currentRoom.event instanceof ruina.events.act3.RedMistRecollection || currentRoom.event instanceof ruina.events.act3.DistortedYan || currentRoom.event instanceof ruina.events.act1.StreetlightOffice || currentRoom.event instanceof ruina.events.act2.WanderingSamurai;");
                    }
                } catch (NotFoundException e) { }
            }
        };
    }
}