package com.client.xvideos.l.net.graphQl


fun getAlbumListGraphQL(
    page: Int = 3,
    itemsPerPage: Int = 30,
    display: String = "rating_14_days",
    audienceIds: String = "+1+10+12+2+3+5+6+8+9",
    languageIds: String = "+1+100+101+2+3+4+5+6+8+9+99"
): String {
    val query = """
        query AlbumList(${'$'}input: AlbumListInput!) {
          album {
            list(input: ${'$'}input) {
              info { ...FacetCollectionInfo }
              items { ...AlbumInSearchList }
            }
          }
        }

        fragment FacetCollectionInfo on FacetCollectionInfo {
          page
          has_next_page
          has_previous_page
          total_items
          total_pages
          items_per_page
          url_complete
        }

        fragment AlbumInSearchList on Album {
          __typename
          id
          title
          description
          created
          modified
          like_status
          moderation_status
          number_of_favorites
          number_of_dislikes
          number_of_pictures
          number_of_animated_pictures
          number_of_duplicates
          slug
          is_manga
          url
          download_url
          labels
          permissions
          cover { width height size url }
          created_by { id url name display_name user_title avatar_url }
          language { id title url }
          tags { category text url count }
          genres { id title slug url }
        }
    """.trimIndent()
        .replace("\n", "\\n")  // превращаем новые строки в \n для JSON
        .replace("\"", "\\\"")  // экранируем кавычки

    return """
        {
          "id": "5",
          "operationName": "AlbumList",
          "query": "$query",
          "variables": {
            "input": {
              "items_per_page": $itemsPerPage,
              "display": "$display",
              "filters": [
                { "name": "audience_ids", "value": "$audienceIds" },
                { "name": "language_ids", "value": "$languageIds" }
              ],
              "page": $page
            }
          }
        }
    """.trimIndent()
}

//fun albumSearchQuery(
//    searchQuery: String,
//    page: Int = 1,
//    display: String = "rating_all_time",
//    albumType: String = "All",
//    contentType: String = "0"
//): Map<String, Any> {
//    val query = """
//        query AlbumList(${'$'}input: AlbumListInput!) {
//            album {
//                list(input: ${'$'}input) {
//                    info { ...FacetCollectionInfo }
//                    items { ...AlbumMinimal }
//                }
//            }
//        }
//        fragment FacetCollectionInfo on FacetCollectionInfo {
//            page has_next_page has_previous_page total_items total_pages items_per_page url_complete
//        }
//        fragment AlbumMinimal on Album {
//            __typename id title number_of_pictures number_of_animated_pictures
//        }
//    """.trimIndent()
//
//    return mapOf(
//        "query" to query,
//        "variables" to mapOf(
//            "input" to mapOf(
//                "display" to display,
//                "filters" to listOf(
//                    mapOf("name" to "restrict_genres", "value" to "loose"),
//                    mapOf("name" to "audience_ids", "value" to "+1+2+3+5+6+8+9+10"),
//                    mapOf("name" to "album_type", "value" to albumType),
//                    mapOf("name" to "search_query", "value" to searchQuery),
//                    mapOf("name" to "content_id", "value" to contentType)
//                ),
//                "page" to page
//            )
//        )
//    )
//}