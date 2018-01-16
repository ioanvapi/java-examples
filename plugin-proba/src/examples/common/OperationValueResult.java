package examples.common;

public interface OperationValueResult <T> extends OperationResult {
    void setValue(T value);
    T getValue();
}
