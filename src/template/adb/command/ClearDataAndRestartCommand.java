package template.adb.command;

public class ClearDataAndRestartCommand extends CommandList {
    public ClearDataAndRestartCommand() {
        super(new ClearDataCommand(), new StartDefaultActivityCommand(false));
    }
}
