package com.crowdspace.crowdspace.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Form(
        @DocumentId
        var id: String? = "",
        var uid: String? = "",
        var bid: String? = "",
        var bName: String? = "",
        var name: String? = "",
        var timeStamp: Date = Date(),
        var condition: String? = "",
        var url: String? = "",
        var visit: Int? = 0,
        var height: String? = "",
        var weight: String? = "",
        var contact: String? = "",
        var active: Boolean? = true
): Parcelable
