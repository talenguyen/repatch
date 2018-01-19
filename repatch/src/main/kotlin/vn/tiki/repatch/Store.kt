package vn.tiki.repatch

import kotlin.properties.Delegates

class Store<T>(initialState: T, private val reducer: Reducer<T>? = null) {

  private var computing = false
  private var dispatching = false
  private val listeners = mutableListOf<(T) -> Unit>()

  var dispatch: Dispatcher<T> = Dispatcher { reducer ->
    if (dispatching) throw IllegalStateException("Can't dispatch now")
    dispatching = true

    try {
      state = reducer(state)
    } finally {
      dispatching = false
    }
  }

  var state: T by Delegates.observable(
    initialState,
    { _, old, new ->
      if (old == new || computing) {
        return@observable
      }

      if (reducer != null) {
        computing = true
        try {
          val nextState = reducer.invoke(state)
          if (nextState != this.state) {
            this.state = nextState
          }
        } finally {
          computing = false
        }
      }

      listeners.forEach { it.invoke(state) }
    })
    private set

  fun addMiddleware(middleware: Middleware<T>): Store<T> {
    this.dispatch = middleware(this, this.dispatch)
    return this
  }

  fun subscribe(listener: (T) -> Unit): () -> Unit {
    listener.invoke(state) // notify current value
    listeners.add(listener)
    return {
      listeners.remove(listener)
    }
  }
}

fun <T> logger(): Middleware<T> = middleware { state, nextState ->
  println("pre state\t: [$state]")
  println("next state\t: [$nextState]")
}

fun <T> middleware(func: (T, T) -> Unit): Middleware<T> {
  return Middleware { store, next ->
    Dispatcher { reducer ->
      val state = store.state
      val nextCounterState = reducer(state)
      func(state, nextCounterState)
      next({ nextCounterState })
    }
  }
}
