package com.crowdspace.crowdspace.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Business(
        @DocumentId
        var id: String? = "",
        var name: String? = "",
        var hospitalId: String? = "",
        var queue: List<String?>? =  listOf(),
        var status: String? = "open",
        var avgTime: Int? = 1,
        var till: String? = "",
        var adminId: String? = ""
): Parcelable