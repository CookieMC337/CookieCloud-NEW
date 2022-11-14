import de.cookiemc.script.api.IScript;
import de.cookiemc.script.api.IScriptLoader;
import de.cookiemc.script.api.impl.DefaultScriptLoader;
import de.cookiemc.script.defaults.DefaultModifyCommand;
import de.cookiemc.script.defaults.DefaultPrintCommand;
import de.cookiemc.script.defaults.DefaultRunCommand;
import de.cookiemc.script.defaults.DefaultVarCommand;

import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) {
        IScriptLoader loader = new DefaultScriptLoader();

        loader.registerCommand(new DefaultPrintCommand());
        loader.registerCommand(new DefaultRunCommand());
        loader.registerCommand(new DefaultVarCommand());
        loader.registerCommand(new DefaultModifyCommand());

        IScript script = loader.loadScript(Paths.get("cloud.script"));
        if (script == null) {
            System.out.println("Couldn't load script!");
            return;
        }
        script.execute();
    }


}
