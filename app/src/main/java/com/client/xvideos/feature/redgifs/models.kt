package com.client.xvideos.feature.redgifs

import java.time.ZonedDateTime

/**
 * Информация об изображении RedGifs, полученная из API.
 *
 * Все имена полей совпадают с ключами JSON, чтобы сериализация/десериализация
 * работала «из коробки». Аннотация `@SerializedName` даёт возможность держать
 * camelCase-код-стайл и при этом не ломать схему.
 */
data class Image(

    /** Идентификатор изображения. */
    val id: String,
    val create_date: ZonedDateTime? = null,    //Дата публикации (UTC) или null, если API не прислал значение.
    val width: Int = 100,                      // Ширина изображения в пикселях.
    val height: Int = 100,                     // Высота изображения в пикселях. */

    /** Количество лайков. */

    val likes: Int,

    /** Список тегов GIF-а. */
    val tags: List<String> = emptyList(),

    /** Автор — подтверждённый (verified) создатель? */
   // @SerializedName("verified")
    val verified: Boolean,

    /** Количество просмотров или null, если данных нет. */
   // @Serie("views")
    val views: Int? = null,

    /** Опубликовано ли изображение. */
    //@Sere("published")
    val published: Boolean,

    /** Набор разных URL-адресов для изображения (SD, HD, превью и т.д.). */
    //@Se("urls")
    val urls: URL,

    /** Имя пользователя-публикатора. */
    val username: String,

    /** Неизвестный числовой тип (согласно API). */
    val type: Int,
    val avg_color: String
)

/**
 * Различные типы URL, получаемые от RedGifs.
 *
 * ⚠️ Внимание: поля `sd`, `hd`, `poster`, `thumbnail`, `vthumbnail` содержат прямые ссылки,
 * раскрывающие ваш IP-адрес при обращении. Для показа пользователям лучше использовать `web_url` или `embed_url`.
 */
data class URL(

    val sd: String,             // SD-ссылка на медиафайл.
    val hd: String? = null,     // HD-ссылка на медиафайл (может отсутствовать).
    val poster: String? = null, // Постер (предпросмотр перед воспроизведением).

    /** Миниатюра изображения. */
    //@SerializedName("thumbnail")
    val thumbnail: String,


    val vthumbnail: String? = null, /** Видео-миниатюра (может отсутствовать). */

    /** Ссылка на веб-страницу с медиа. */
    //@SerializedName("web_url")
    val web_url: String,


    val file_url: String? = null, /** Прямая ссылка на файл (может отсутствовать). */
    val embed_url: String? = null // Встраиваемая ссылка для безопасного показа
)



/**
 * Информация о пользователе RedGifs.
 *
 * @property creationTime   Дата создания аккаунта (UTC) или null.
 * @property description    Описание профиля.
 * @property followers      Кол-во подписчиков.
 * @property following      Кол-во подписок.
 * @property gifs           Всего GIF-ов.
 * @property name           Отображаемое имя.
 * @property profileImageUrlURL аватар.
 * @property profileUrl     “Сайт” из профиля.
 * @property publishedCollections Кол-во опубликованных коллекций.
 * @property publishedGifs  Кол-во опубликованных GIF-ов.
 * @property status         Статус (bio-строка).
 * @property subscription   Тип подписки (до уточнения оставляем Int).
 * @property url            URL профиля на redgifs.com.
 * @property username       Никнейм.
 * @property verified       Подтверждённый автор?
 * @property views          Суммарные просмотры всех GIF-ов.
 * @property poster         URL постера.
 * @property preview        URL превью.
 * @property thumbnail      URL миниатюры.
 * @property links          Ссылки в профиле (список пар “title → url”).
 */
data class User(
    val creation_time: ZonedDateTime?        = null,
    val description: String?               = null,
    val followers: Int                     = 0,
    val following: Int                     = 0,
    val gifs: Int                          = 0,
    val name: String?                      = null,
    val profile_image_url: String?           = null,
    val profile_url: String?                = null,
    val published_collections: Int?         = null,
    val published_gifs: Int                 = 0,
    val status: String?                    = null,
    val subscription: Int                  = 0,
    val url: String                        = "",
    val username: String                   = "",
    val verified: Boolean                  = false,
    val views: Int                         = 0,
    val poster: String?                    = null,
    val preview: String?                   = null,
    val thumbnail: String?                 = null,
    val links: List<Map<String, String>>?  = null
)

data class GIF(
    val id: String,
    val create_date: ZonedDateTime? = null,
    val has_audio: Boolean,
    val width: Int,
    val height: Int,
    val likes: Int,                         /** Количество лайков. */
    val tags: List<String> = emptyList(),   /** Список тегов. */
    val verified: Boolean,                  /** Автор – подтверждённый (verified) создатель? */
    val views: Int? = null,                 /** Количество просмотров или `null`, если данных нет. */
    val duration: Float,                    /** Длительность ролика в секундах. */
    val published: Boolean,                 /** Опубликован ли GIF. */
    val urls: URL,                          /** Различные ссылки (sd/hd/poster и т.д.). */
    val username: String,                   /** Имя пользователя-публикатора. */
    val type: Int,                          /** Неизвестный числовой тип (см. TODO в оригинальном коде). */
    val avg_color: String                   /** Средний цвет кадра в HEX (например, `"#aabbcc"`). */
)

// Результат вашего поиска. Он возвращается в :meth:`~redgifs.API.search()`.
data class SearchResult(
    val searched_for: String = "searched_for",  // Что было найдено (может отличаться от исходного запроса query).
    val page: Int = 0,                          // Номер текущей страницы.
    val pages: Int = 0,                         // Общее количество страниц по данному запросу.
    val total: Int = 0,                         // Общее количество найденных GIF-файлов.
    val gifs: List<GIF>? = null,                // Список GIF-файлов, подходящих под поисковый запрос, если есть.
    val images: List<Image>? = null,            // Список изображений, подходящих под поисковый запрос, если есть.
    val users: List<User> = emptyList(),        // Список пользователей, релевантных поисковому запросу.
    val tags: List<String> = emptyList()        // Список тегов, связанных с поисковым запросом и результатами.
)

// Результаты поиска по создателям.
data class CreatorsResult(
    val items: List<User> = emptyList(), // Список авторов (пользователей).
    val page: Int = 0,                   // Номер текущей страницы.
    val pages: Int = 0,                  // Общее количество доступных страниц.
    val total: Int = 0                   // Общее количество авторов (пользователей).
)

// Результат поиска создателя.
data class CreatorResult(
    val creator: User,                     // Детали автора/пользователя.
    val page: Int = 0,                     // Текущий номер страницы.
    val pages: Int = 0,                    // Общее количество доступных страниц.
    val total: Int = 0,                    // Общее количество GIF, созданных этим автором/пользователем.
    val gifs: List<GIF> = emptyList(),     // Список GIF, загруженных этим автором.
    val images: List<Image> = emptyList()  // Список изображений, загруженных этим автором.
)

// Результат предложения тега.
data class TagSuggestion(
    val name: String = "",    //Название тега
    val count: Int = 0        //Количество GIF-файлов с этим тегом
)