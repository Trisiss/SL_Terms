package com.example.sl_terms.models

import com.squareup.moshi.Json

/**
 * Created by Trisiss on 21.05.2020
 */
data class Question (
        val id: Int,
        val text: String,
        val type: Int,
        @Json(name = "id_picture") val idPicture: Int?
)