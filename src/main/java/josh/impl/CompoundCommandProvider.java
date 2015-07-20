package josh.impl;

import josh.api.CommandProvider;

/**
 * Permite juntar comandos de várias fontes
 */
public class CompoundCommandProvider {
    void CompoundCommandProvider(CommandProvider... providers) {

        System.out.println(providers);
    }
}
