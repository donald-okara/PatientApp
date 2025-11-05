package ke.don.data.local.datastore

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ke.don.domain.model.tables.Authorisation
import java.io.IOException

class AuthorisationStore(private val context: Context) {

    private val dataStore = context.authorisationDataStore

    /**
     * Observe authorisation as a flow
     */
    val authorisation: Flow<Authorisation> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(Authorisation()) else throw e
        }
        .map { it }

    /**
     * Update the authorisation data
     */
    suspend fun save(authorisation: Authorisation) {
        dataStore.updateData { authorisation }
    }

    /**
     * Clear stored authorisation
     */
    suspend fun clear() {
        dataStore.updateData { Authorisation() }
    }

    /**
     * Get the current authorisation synchronously
     */
    suspend fun current(): Authorisation = authorisation.first()
}
