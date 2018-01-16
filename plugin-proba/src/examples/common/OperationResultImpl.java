package examples.common;

import java.util.ArrayList;
import java.util.List;

public class OperationResultImpl implements OperationResult {
    private final List<String> errorMessages = new ArrayList<>();
    private final List<String> infoMessages = new ArrayList<>();

    @Override
    public void addInfo(String format, Object... args) {
        infoMessages.add(String.format(format, args));
    }

    @Override
    public void addError(String format, Object... args) {
        errorMessages.add(String.format(format, args));
    }

    @Override
    public boolean failed() {
        return !success();
    }

    @Override
    public boolean success() {
        return errorMessages.size() == 0;
    }

    @Override
    public boolean hasInfo() {
        return infoMessages.size() > 0;
    }

    @Override
    public String getErrorAsString() {
        StringBuilder builder = new StringBuilder();
        for(String message : errorMessages)
            builder.append(message + "\n");
        return builder.toString();
    }


    @Override
    public String getInfoAsString() {
        StringBuilder builder = new StringBuilder();
        for(String message : infoMessages)
            builder.append(message + "\n");
        return builder.toString();
    }
}
