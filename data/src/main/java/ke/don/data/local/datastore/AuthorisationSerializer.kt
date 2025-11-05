package ke.don.data.local.datastore

import ke.don.domain.model.tables.Authorisation
import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object AuthorisationSerializer : Serializer<Authorisation> {
    override val defaultValue: Authorisation
        get() = Authorisation()

    override suspend fun readFrom(input: InputStream): Authorisation {
        return try {
            Json.decodeFromString(
                deserializer = Authorisation.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Authorisation, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = Authorisation.serializer(),
                value = t,
            ).encodeToByteArray(),
        )
    }
}

val Context.authorisationDataStore by dataStore("authorisation.json", AuthorisationSerializer)

suspend fun Context.setAuthorisation(authorisation: Authorisation) {
    authorisationDataStore.updateData {
        authorisation
    }
}