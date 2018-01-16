package examples.common;

public class OperationValueResultImpl <T> extends OperationResultImpl implements OperationValueResult<T> {
    private T value;

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }
}
