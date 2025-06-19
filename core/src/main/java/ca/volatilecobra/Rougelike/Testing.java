package ca.volatilecobra.Rougelike;

import ca.volatilecobra.Rougelike.Mods.*;
import ca.volatilecobra.Rougelike.Utils.TaggedPrintStream;
import ca.volatilecobra.Rougelike.Utils.PrintStreamTypes;


public class Testing {
    public static void main(String[] args){
        System.setOut(new TaggedPrintStream(System.out, PrintStreamTypes.INFO));
        System.setErr(new TaggedPrintStream(System.err, PrintStreamTypes.ERR));
        System.out.println(System.getProperty("user.dir") + "/mods");
        Modloader.SearchForMods(System.getProperty("user.dir") + "/mods");
    }
}
