package org.example.project.application.plugins

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import org.example.project.application.dtos.errorResponse
import org.example.project.application.models.FirebaseUserPrincipal
import java.net.URL
import java.util.concurrent.TimeUnit

fun Application.configureAuth(config: ApplicationConfig) {
    val firebaseProjectId = config.propertyOrNull("firebase.projectId")?.getString()
        ?: throw IllegalArgumentException("Firebase project ID is not configured")
    val jwtIssuer = "https://securetoken.google.com/$firebaseProjectId"
    val jwksUri = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"

    install(Authentication) {
        jwt("firebase_auth") {
            realm = "Ktor Firebase Realm"

            verifier(
                JwkProviderBuilder(URL(jwksUri))
                    .cached(10, 24, TimeUnit.HOURS)
                    .build()
            ) {
                withIssuer(jwtIssuer)
                withAudience(firebaseProjectId)
                acceptLeeway(60)
            }

            validate { credential ->
                val uid = credential.payload.subject
                if (uid != null) {
                    FirebaseUserPrincipal(
                        uid = uid,
                        email = credential.payload.getClaim("email")?.asString(),
                        name = credential.payload.getClaim("name")?.asString(),
                    )
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    errorResponse("Unauthorized access")
                )
            }
        }
    }
}