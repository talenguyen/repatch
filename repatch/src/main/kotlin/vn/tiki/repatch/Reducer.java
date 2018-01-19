package vn.tiki.repatch;

public interface Reducer<T> {
    T invoke(T state);
}
