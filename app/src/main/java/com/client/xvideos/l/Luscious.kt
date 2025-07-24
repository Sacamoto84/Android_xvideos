package com.client.xvideos.l

import com.client.xvideos.Secrets
import kotlinx.coroutines.runBlocking

fun main(){
    runBlocking {
        val luscious = Luscious(Secrets.lusciousEmail, Secrets.lusciousPassword)
        luscious.login()
        if (luscious.loggedIn) {

            // дальше можно делать авторизованные запросы через luscious.handler
            val a = luscious.getAlbum(374481)
            a

        }
    }
}

class Luscious(
    private val username: String? = null,
    private val password: String? = null,
    timeoutMillis: Long = 5000,
    maxRetries: Int = 5,
    retryStatusCodes: Set<Int> = setOf(413, 429, 500, 502, 503, 504),
    backoffFactor: Long = 1000
) {
    companion object {
        const val API = "https://members.luscious.net/graphql/nobatch/"
        const val HOME = "https://members.luscious.net"
        const val LOGIN = "https://members.luscious.net/accounts/login/"
    }

    private val handler = KtorRequestHandler(
        timeoutMillis = timeoutMillis,
        maxRetries = maxRetries,
        retryStatusCodes = retryStatusCodes,
        backoffFactor = backoffFactor
    )

    var loggedIn: Boolean = false
        private set

    suspend fun login() {
        if (username == null || password == null) {
            println("Username or password not provided")
            return
        }

        val formData = mapOf(
            "login" to username,
            "password" to password,
            "remember" to "on"
        )

        val response = try {
            handler.post(LOGIN, formData)
        } catch (e: Exception) {
            println("Login request failed: ${e.message}")
            return
        }

        if ("The username and/or password you specified are not correct." in response) {
            println("!!! Login failed. Please check your credentials")
            loggedIn = false
        } else {
            loggedIn = true
            println("Login successful")
        }
    }


    /**
     *
     *         Return an `Album` object based on albumInput
     *
     *         albumInput can either be an integer, being the album Id
     *         Example (NSFW)<https://www.luscious.net/albums/animated-gifs_374481/>'s Id being 374481
     *         Or it can be a string, the link itself
     *
     */
    fun getAlbum(albumInput: Any, download: Boolean = false): Album {

        val id = when (albumInput) {
            is Int -> albumInput.toString()
            is String -> extractIdFromUrl(albumInput) ?: albumInput // Если URL, извлекаем ID, иначе используем как есть
            else -> throw IllegalArgumentException("albumInput must be Int or String")
        }

        return Album(id.toInt(), download, handler)
    }


    // Вспомогательная функция для извлечения ID из URL
    private fun extractIdFromUrl(url: String): String? {
        val regex = Regex("/albums/[^_]+_(\\d+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }









}