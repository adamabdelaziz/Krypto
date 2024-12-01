import kotlinx.coroutines.Job

fun Job?.cancelAndNull(): Job? {
    this?.cancel()
    return null
}