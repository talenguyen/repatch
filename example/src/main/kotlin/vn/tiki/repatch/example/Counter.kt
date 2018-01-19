package vn.tiki.repatch.example

import vn.tiki.repatch.Store
import vn.tiki.repatch.logger

data class CounterState(var count: Int = 0)

fun main(args: Array<String>) {
  val store = Store(CounterState())
    .addMiddleware(logger())

  val unsubscribe = store.subscribe { println(it.count) }
  store.subscribe { println(it) }

  println("start app")

  store.dispatch {
    it.copy(count = it.count + 1)
  }

  store.dispatch {
    it.copy(count = it.count + 1)
  }

  unsubscribe()

  store.dispatch {
    it.copy(count = it.count + 10)
  }
}
