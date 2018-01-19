package vn.tiki.repatch;

public interface Middleware<T> {
    Dispatcher<T> invoke(Store<T> store, Dispatcher<T> dispatch);
}
