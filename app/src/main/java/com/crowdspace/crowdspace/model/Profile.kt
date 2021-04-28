package com.crowdspace.crowdspace.model

import com.google.firebase.firestore.DocumentId


data class Profile(
        @DocumentId
        var id: String? = "",
        var name: String? = "",
        var weight: String? = "",
        var height: String? = "",
        var bloodGroup: String? = "",
        var uid: String? = "",
)