package ke.don.data.local.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ke.don.data.PatientDatabase

class DatabaseFactory(
    private val context: Context,
)  {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            PatientDatabase.Schema,
            context,
            "patient.db",
        )
    }
}