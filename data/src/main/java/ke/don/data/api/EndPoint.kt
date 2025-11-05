package ke.don.data.api

internal sealed class Endpoint(val route: String) {
    /**
     * The base URL for the API endpoint.
     */
    open val baseUrl = BASE_URL

    /**
     * The complete URL for the endpoint, constructed by appending the [route] to the [baseUrl].
     */
    val url: String
        get() = buildString {
            append(baseUrl)
            append(route)
        }

    sealed class Auth(route: String) : Endpoint(route) {
        override val baseUrl = "${BASE_URL}user/"

        object SignIn : Auth(route = "signin")

        object SignUp : Auth(route = "signup")
    }

    sealed class Patient(route: String) : Endpoint(route) {
        override val baseUrl = "${BASE_URL}patients/"

        object Register : Patient(route = "register")
        object List : Patient(route = "view")
        object View : Patient(route = "show")
    }

    sealed class Vitals(route: String) : Endpoint(route) {
        override val baseUrl = "${BASE_URL}vital/"

        object Add : Vitals(route = "add")
    }

    sealed class Visit(route: String) : Endpoint(route) {
        override val baseUrl = "${BASE_URL}visits/"

        object Add : Visit(route = "add")
    }

}

const val BASE_URL = "https://patientvisitapis.intellisoftkenya.com/api/"