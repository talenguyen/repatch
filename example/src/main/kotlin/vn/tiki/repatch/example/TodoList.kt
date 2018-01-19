package vn.tiki.repatch.example

import vn.tiki.repatch.Reducer
import vn.tiki.repatch.logger

data class Todo(val text: String, val completed: Boolean = false)
data class TodoList(val todos: List<Todo>, val empty: Boolean = false)

fun main(args: Array<String>) {
  val store = vn.tiki.repatch.Store(
    TodoList(emptyList()),
    Reducer {
      val empty = it.todos.isEmpty()
        || it.todos
        .map { it.completed }
        .reduce { acc, completed -> acc && completed }
      it.copy(empty = empty)
    })
    .addMiddleware(logger())

  val addTodo: (String) -> Reducer<TodoList> = { text ->
    Reducer { state ->
      state.copy(todos = state.todos.push(Todo(text)))
    }
  }

  val toggleTodo: (Int) -> Reducer<TodoList> = { index ->
    Reducer { state ->
      state.copy(todos = state.todos.mapIndexed { i, todo ->
        if (i == index) {
          todo.copy(completed = !todo.completed)
        } else {
          todo
        }
      })
    }
  }

  val todoView: (Todo) -> Unit = { todo -> println(" [${if (todo.completed) "x" else " "}] ${todo.text}") }

  val todoListView: (List<Todo>) -> Unit = { todos ->
    todos.forEach { todo ->
      todoView(todo)
    }
  }

  val emptyView: (Boolean) -> Unit = { empty ->
    if (empty) {
      println("NO TASKS ")
    }
  }

  val appView: (TodoList) -> Unit = { state ->
    println("================")
    println("==== RENDER ====")
    println("================")
    todoListView(state.todos)
    emptyView(state.empty)
    println("\n")
  }

  val unsubscribe = store.subscribe { state ->
    appView(state)
  }

  store.dispatch(addTodo("re-patch"))
  store.dispatch(addTodo("todo example"))
  store.dispatch(toggleTodo(0))
  store.dispatch(toggleTodo(1))
  store.dispatch(addTodo("test todo example"))
  store.dispatch(toggleTodo(1))
  store.dispatch(toggleTodo(2))

  unsubscribe()
}