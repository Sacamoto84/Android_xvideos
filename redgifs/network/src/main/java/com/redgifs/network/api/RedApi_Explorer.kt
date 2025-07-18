package com.redgifs.network.api

import com.redgifs.network.http.ApiClient
import com.redgifs.network.http.Route
import com.redgifs.model.NichesResponse
import com.redgifs.model.Order

class RedApi_Explorer(val api: ApiClient) {

    /**
    ### subscribers
    ```
    https://api.redgifs.com/v2/niches?order=subscribers&previews=yes&sort=desc&page=1&count=30
    ```

    ### posts
    ```
    https://api.redgifs.com/v2/niches?order=posts&previews=yes&sort=desc&page=1&count=30
    ````

    ### name a-z
    ```
    https://api.redgifs.com/v2/niches?order=name&previews=yes&sort=asc&page=1&count=30
    ```
    ### name z-a
    ```
    https://api.redgifs.com/v2/niches?order=name&previews=yes&sort=desc&page=1&count=30
    ```
     */
    suspend fun getExplorerNiches(
        order: Order = Order.NICHES_SUBSCRIBERS_D,
        count: Int = 100,
        page: Int = 1
    ): NichesResponse {

        val sort = when (order) {
            Order.NICHES_NAME_A_Z -> "asc"
            else -> "desc"
        }
        val route = Route(
            method = "GET",
            path = "/v2/niches?&order={order}&previews=yes&sort={sort}&page={page}&count={count}",
            "order" to order.value,
            "sort" to sort,
            "page" to page,
            "count" to count
        )

        return api.request(route)
    }
}