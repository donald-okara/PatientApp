package ke.don.domain.model

sealed interface PatientResult<out D, out E : PatientError> {
    data class Success<out D>(val data: D) : PatientResult<D, Nothing>
    data class Error<out E : PatientError>(val error: E) : PatientResult<Nothing, E>
}

/**
 * Transforms the successful result's data using the provided `map` function.
 * If the result is an error, it returns the original error.
 *
 * @param map A function to apply to the successful result's data.
 * @return A new [PatientResult] with the transformed data if successful, or the original error if not.
 * @param T The type of the original successful data.
 * @param E The type of the error, which must extend [PatientError].
 * @param R The type of the transformed successful data.
 */
inline fun <T, E : PatientError, R> PatientResult<T, E>.map(map: (T) -> R): PatientResult<R, E> {
    return when (this) {
        is PatientResult.Error -> PatientResult.Error(error)
        is PatientResult.Success -> PatientResult.Success(map(data))
    }
}

/**
 * Converts a [PatientResult] to an [EmptyResult] by discarding the success data.
 * If the original result was a [PatientResult.Success], the new result will be a [PatientResult.Success] with `Unit` data.
 * If the original result was a [PatientResult.Error], the new result will be a [PatientResult.Error] with the same error.
 *
 * @param T The type of the success data in the original result.
 * @param E The type of the error in the original result.
 * @return An [EmptyResult] which is a [PatientResult] with `Unit` as its success data type.
 */
fun <T, E : PatientError> PatientResult<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

/**
 * Executes the given [action] if this [PatientResult] is a [PatientResult.Success].
 * Returns the original [PatientResult] unchanged.
 *
 * @param T The type of the successful data.
 * @param E The type of the error.
 * @param action The action to execute with the successful data.
 * @return The original [PatientResult].
 */
inline fun <T, E : PatientError> PatientResult<T, E>.onSuccess(action: (T) -> Unit): PatientResult<T, E> {
    return when (this) {
        is PatientResult.Error -> this
        is PatientResult.Success -> {
            action(data)
            this
        }
    }
}

/**
 * Executes the given [action] if this [PatientResult] is an [PatientResult.Error].
 * The original [PatientResult] is returned, regardless of whether it's an error or success.
 *
 * @param action The action to be executed with the error of type [E] if this is an [PatientResult.Error].
 * @return The original [PatientResult] instance.
 */
inline fun <T, E : PatientError> PatientResult<T, E>.onError(action: (E) -> Unit): PatientResult<T, E> {
    return when (this) {
        is PatientResult.Error -> {
            action(error)
            this
        }
        is PatientResult.Success -> this
    }
}

/**
 * Checks if the [PatientResult] is a [PatientResult.Success].
 * @return `true` if the result is a success, `false` otherwise.
 */
fun PatientResult<*, *>.isSuccess(): Boolean {
    return when (this) {
        is PatientResult.Error<*> -> false
        is PatientResult.Success<*> -> true
    }
}

typealias EmptyResult<E> = PatientResult<Unit, E>