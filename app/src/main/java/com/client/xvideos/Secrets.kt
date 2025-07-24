package com.client.xvideos

import java.io.File
import java.util.*

object Secrets {
    private val props: Properties by lazy {
        val file = File(System.getProperty("user.dir"), "local.properties")
        Properties().apply {
            if (file.exists()) {
                file.inputStream().use { load(it) }
            } else {
                println("⚠️ local.properties not found at: ${file.absolutePath}")
            }
        }
    }

    val lusciousEmail: String = props.getProperty("luscious_email")
    val lusciousPassword: String = props.getProperty("luscious_password")
}