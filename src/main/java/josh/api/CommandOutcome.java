package josh.api;

public class CommandOutcome {

    private int exitCode;
    private boolean exitRequest;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        if (exitCode < 0 || exitCode > 255) {
            exitCode = 255;
        }
        this.exitCode = exitCode;
    }

    public void setExitRequest() {
        this.exitRequest = true;
        this.exitCode = 0;
    }

    public boolean isExitRequest() {
        return exitRequest;
    }

    public boolean isErrorState() {
        return exitCode != 0;
    }
}
