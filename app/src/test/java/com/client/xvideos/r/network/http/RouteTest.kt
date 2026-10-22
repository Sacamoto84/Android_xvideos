package com.client.xvideos.r.network.http

import org.junit.Assert.assertEquals
import org.junit.Test

class RouteTest {

    @Test
    fun `encodes percent, spaces, reserved and non-ascii characters`() {
        val route = Route("GET", "/v2/search/suggest?query={text}", "text" to "100% a&b ü")
        assertEquals("https://api.redgifs.com/v2/search/suggest?query=100%25%20a%26b%20%C3%BC", route.url)
    }

    @Test
    fun `non-string parameters are inserted as is`() {
        val route = Route("GET", "/v2/gifs/search?count={count}&page={page}", "count" to 100, "page" to 2)
        assertEquals("https://api.redgifs.com/v2/gifs/search?count=100&page=2", route.url)
    }

    @Test
    fun `joined tags keep commas encoded`() {
        val route = Route("GET", "/v2/users/{username}/search?tags={tags}", "username" to "user_1", "tags" to "Amateur,Armpit")
        assertEquals("https://api.redgifs.com/v2/users/user_1/search?tags=Amateur%2CArmpit", route.url)
    }
}
