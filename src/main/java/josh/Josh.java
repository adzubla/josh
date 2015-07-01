package josh;

/**
 * Integra todos os componentes com implementação default
 * - le opções da linha de comando
 * - configura shell
 * - invoca shell
 * <p/>
 * josh -c 'cmd arg1 arg2'
 * josh -f cmdlist.txt
 * josh -v key=value
 * josh
 */
public class Josh {
    public static void main(String args) {
        Shell js = new Shell();

        boolean interactive = true;

        js.setConsoleProvider(interactive
                ? new JLineProvider()
                : new JavaConsoleProvider());

        CommandOutcome co = js.run();
        System.exit(co.getExitCode());
    }
}
