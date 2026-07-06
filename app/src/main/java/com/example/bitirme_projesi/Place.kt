package com.example.bitirme_projesi

import com.google.firebase.firestore.PropertyName

data class Place(
    val name: String = "",
    val location: String = "",
    val distance: String = "",
    val rating: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val imageUrl: String = "",

    // Açıklamalar (Çoklu Dil)
    val descTr: String = "",
    val descEn: String = "",
    val descDe: String = "",
    val descFr: String = "",
    val descEs: String = "",

    // Saatler
    val hoursTr: String = "",
    val hoursEn: String = ""
)