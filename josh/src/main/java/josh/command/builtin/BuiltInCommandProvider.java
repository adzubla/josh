package josh.command.builtin;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import josh.command.CommandDescriptor;
import josh.command.CommandNotFound;
import josh.command.CommandOutcome;
import josh.command.CommandProvider;
import josh.command.HelpFormatter;
import josh.shell.Shell;
import josh.shell.ShellAwareCommandProvider;
import josh.shell.jline.JLineConsoleProvider;

/**
 * Implements some simple generic commands.
 */
public class BuiltInCommandProvider implements CommandProvider, ShellAwareCommandProvider {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInCommandProvider.class);

    protected Map<String, CommandDescriptor> commands;

    protected Shell shell;

    protected HelpFormatter helpFormatter;

    public void setCustomHelpFormatter(HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }

    @Override
    public void initialize() {
        commands = new HashMap<String, CommandDescriptor>();

        addDescriptor("date", "Display current date.");
        addDescriptor("props", "Display Java system properties.");
        addDescriptor("setp", "Set Java system property.");
        addDescriptor("env", "Display environment variables.");
        addDescriptor("pwd", "Display working directory.");
        addDescriptor("help", "Display commands.");
        addDescriptor("clear", "Clear screen.");
        addDescriptor("sleep", "Sleep in milliseconds.");
        addDescriptor("os", "Display operating system information.");
        addDescriptor("jvm", "Display JVM information.");
        addDescriptor("mem", "Display JVM memory information.");
        addDescriptor("pools", "Display JVM memory pools.");
        addDescriptor("gc", "Display JVM garbage collector information.");
        addDescriptor("exit", "Exit shell.");

        LOG.debug("commands = {}", commands);

        if (this.helpFormatter == null) {
            helpFormatter = new BuiltInHelpFormatter();
        }
    }

    protected void addDescriptor(String name, String description) {
        CommandDescriptor descriptor = new CommandDescriptor();
        descriptor.setCommandName(name);
        descriptor.setCommandDescription(description);
        commands.put(descriptor.getCommandName(), descriptor);
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean isValidCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    @Override
    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Override
    public HelpFormatter getHelpFormatter(String commandName) {
        return helpFormatter;
    }

    @Override
    public Map<String, CommandDescriptor> getCommands() {
        return new HashMap<String, CommandDescriptor>(commands);
    }

    @Override
    public CommandOutcome execute(List<String> tokens) throws CommandNotFound {
        String name = tokens.get(0);
        List<String> arguments = tokens.subList(1, tokens.size());

        CommandDescriptor commandDescriptor = commands.get(name);
        if (commandDescriptor == null) {
            throw new CommandNotFound(name);
        }

        return invokeCommand(commandDescriptor, arguments);
    }

    protected CommandOutcome invokeCommand(CommandDescriptor commandDescriptor, List<String> arguments) {
        LOG.debug("invokeCommand {}, {}", commandDescriptor.getCommandName(), arguments);

        CommandOutcome commandOutcome = new CommandOutcome();
        if ("date".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(dateCommand(arguments));
        }
        else if ("env".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(envCommand());
        }
        else if ("props".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(propertiesCommand());
        }
        else if ("setp".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(setpCommand(arguments));
        }
        else if ("pwd".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(pwdCommand());
        }
        else if ("clear".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(clearCommand());
        }
        else if ("sleep".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(sleepCommand(arguments));
        }
        else if ("os".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(osCommand());
        }
        else if ("jvm".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(jvmCommand());
        }
        else if ("mem".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(memCommand());
        }
        else if ("pools".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(poolsCommand());
        }
        else if ("gc".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitCode(gcCommand());
        }
        else if ("help".equals(commandDescriptor.getCommandName())) {
            HelpCommand helpCommand = new HelpCommand(shell);
            commandOutcome.setExitCode(helpCommand.run(arguments));
        }
        else if ("exit".equals(commandDescriptor.getCommandName())) {
            commandOutcome.setExitRequest();
        }
        return commandOutcome;
    }

    protected class BuiltInHelpFormatter implements HelpFormatter {

        private String NEW_LINE = "\n";

        @Override
        public String formatHelpMessage(CommandDescriptor cd) {
            StringBuilder sb = new StringBuilder();
            sb.append(cd.getCommandDescription()).append(NEW_LINE);
            sb.append("Usage: ").append(cd.getCommandName());
            if ("date".equals(cd.getCommandName())) {
                sb.append(" [FORMAT]").append(NEW_LINE);
            }
            else if ("help".equals(cd.getCommandName())) {
                sb.append(" [COMMAND]").append(NEW_LINE);
            }
            else if ("sleep".equals(cd.getCommandName())) {
                sb.append(" MILLISECONDS").append(NEW_LINE);
            }
            else if ("setp".equals(cd.getCommandName())) {
                sb.append(" NAME VALUE").append(NEW_LINE);
            }
            sb.append(NEW_LINE);
            return sb.toString();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Commands
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected int pwdCommand() {
        System.out.println(System.getProperty("user.dir"));
        return Shell.EXIT_CODE_OK;
    }

    protected int propertiesCommand() {
        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }
        return Shell.EXIT_CODE_OK;
    }

    protected int setpCommand(List<String> arguments) {
        if (arguments.size() != 2) {
            System.err.println("Expected the key and value for the property");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        String key = arguments.get(0);
        String value = arguments.get(1);
        System.setProperty(key, value);
        return Shell.EXIT_CODE_OK;
    }

    protected int envCommand() {
        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            String value = env.get(key);
            System.out.println(key + "=" + value);
        }
        return Shell.EXIT_CODE_OK;
    }

    protected int clearCommand() {
        try {
            JLineConsoleProvider jLineConsoleProvider = (JLineConsoleProvider)shell.getConsoleProvider();
            jLineConsoleProvider.getConsole().clearScreen();
            return Shell.EXIT_CODE_OK;
        }
        catch (IOException e) {
            LOG.error("clear errror", e);
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }
    }

    public int dateCommand(List<String> arguments) {
        Date currentDate = new Date();

        String format = "yyyy-MM-dd'T'HH:mm:ss";
        if (arguments.size() == 1) {
            format = arguments.get(0);
        }
        else if (!arguments.isEmpty()) {
            System.err.println("Expected date format");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        System.out.println(sdf.format(currentDate));
        return Shell.EXIT_CODE_OK;
    }

    protected int sleepCommand(List<String> arguments) {
        try {
            if (arguments.size() == 1) {
                Thread.sleep(Long.valueOf(arguments.get(0)));
                return Shell.EXIT_CODE_OK;
            }
        }
        catch (Exception e) {
            // ignore
        }
        shell.getConsoleProvider().displayError("Expected parameter in milliseconds");
        return Shell.EXIT_CODE_GENERAL_ERROR;
    }

    protected int osCommand() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean == null) {
            System.out.println("Couldn't get the MXBean!");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        System.out.println("Name: " + operatingSystemMXBean.getName());
        System.out.println("Version: " + operatingSystemMXBean.getVersion());
        System.out.println("Architecture: " + operatingSystemMXBean.getArch());
        System.out.println("Processors: " + operatingSystemMXBean.getAvailableProcessors());
        System.out.println("System load average for the last minute: " + operatingSystemMXBean.getSystemLoadAverage());
        return Shell.EXIT_CODE_OK;
    }

    protected int jvmCommand() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        if (runtimeMXBean == null) {
            System.out.println("Couldn't get the MXBean!");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        System.out.println("Name: " + runtimeMXBean.getName());
        System.out.println("Vm Name: " + runtimeMXBean.getVmName());
        System.out.println("Vm Vendor: " + runtimeMXBean.getVmVendor());
        System.out.println("Vm Version: " + runtimeMXBean.getVmVersion());
        System.out.println("Start time in milliseconds: " + runtimeMXBean.getStartTime());
        System.out.println("Uptime in milliseconds: " + runtimeMXBean.getUptime());
        System.out.println("Input arguments: " + runtimeMXBean.getInputArguments());
        System.out.println("Boot class path: " + runtimeMXBean.getBootClassPath());
        System.out.println("Library path: " + runtimeMXBean.getLibraryPath());
        System.out.println("Class path: " + runtimeMXBean.getClassPath());
        return Shell.EXIT_CODE_OK;
    }

    protected int memCommand() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        if (memoryMXBean == null) {
            System.out.println("Couldn't get the MXBean!");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        System.out.println("Memory usage of the heap: " + memoryMXBean.getHeapMemoryUsage());
        System.out.println("Memory usage of non-heap memory: " + memoryMXBean.getNonHeapMemoryUsage());
        System.out.println("Objects for which finalization is pending: " + memoryMXBean.getObjectPendingFinalizationCount());

        return Shell.EXIT_CODE_OK;
    }

    protected int poolsCommand() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        if (memoryPoolMXBeans == null) {
            System.out.println("Couldn't get the MXBean!");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            System.out.println("Memory pool: " + memoryPoolMXBean.getName());
            System.out.println("  MemoryManagerNames: " + Arrays.toString(memoryPoolMXBean.getMemoryManagerNames()));
            System.out.println("  isValid: " + memoryPoolMXBean.isValid());
            System.out.println("  Type: " + memoryPoolMXBean.getType());
            System.out.println("  PeakUsage: " + memoryPoolMXBean.getPeakUsage());
            System.out.println("  Usage: " + memoryPoolMXBean.getUsage());
            if (memoryPoolMXBean.isUsageThresholdSupported()) {
                System.out.println("  UsageThreshold: " + memoryPoolMXBean.getUsageThreshold());
                System.out.println("  UsageThresholdCount: " + memoryPoolMXBean.getUsageThresholdCount());
                System.out.println("  isUsageThresholdExceeded: " + memoryPoolMXBean.isUsageThresholdExceeded());
            }
            System.out.println("  CollectionUsage: " + memoryPoolMXBean.getCollectionUsage());
            if (memoryPoolMXBean.isCollectionUsageThresholdSupported()) {
                System.out.println("  CollectionUsageThreshold: " + memoryPoolMXBean.getCollectionUsageThreshold());
                System.out.println("  CollectionUsageThresholdCount: " + memoryPoolMXBean.getCollectionUsageThresholdCount());
                System.out.println("  isCollectionUsageThresholdExceeded: " + memoryPoolMXBean.isCollectionUsageThresholdExceeded());
            }
        }

        return Shell.EXIT_CODE_OK;
    }

    protected int gcCommand() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        if (garbageCollectorMXBeans == null) {
            System.out.println("Couldn't get the MXBean!");
            return Shell.EXIT_CODE_GENERAL_ERROR;
        }

        for (GarbageCollectorMXBean gc : garbageCollectorMXBeans) {
            System.out.println("Memory manager name: " + gc.getName());
            System.out.println("  Collection count: " + gc.getCollectionCount());
            System.out.println("  Collection time: " + gc.getCollectionTime());

            System.out.print("  Memory pools:");

            String[] memoryPoolNames = gc.getMemoryPoolNames();
            for (String memoryPoolName : memoryPoolNames) {
                System.out.print(" [" + memoryPoolName + "]");
            }
            System.out.println("\n");
        }

        return Shell.EXIT_CODE_OK;
    }

}
