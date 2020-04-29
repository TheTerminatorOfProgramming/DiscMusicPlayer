package com.ttop.discmusicplayer.deezer

/**
 * @author Paolo Valerdi
 */
data class DeezerResponse(
        val `data`: List<Data>,
        val next: String,
        val total: Int
)