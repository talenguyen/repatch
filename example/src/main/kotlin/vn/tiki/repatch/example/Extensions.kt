package vn.tiki.repatch.example

fun <T> List<T>.push(t: T): List<T> {
  val list = toMutableList()
  list.add(t)
  return list
}
