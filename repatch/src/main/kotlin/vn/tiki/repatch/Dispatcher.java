package vn.tiki.repatch;

public interface Dispatcher<T> {
    void invoke(Reducer<T> reducer);
}
