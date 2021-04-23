package com.crowdspace.crowdspace.model

import com.google.firebase.firestore.DocumentId

data class Form(
        @DocumentId
        var id: String? = "",
        var uid: String? = "",
        var bid: String? = "",
        var name: String? = "",
        var condition: String? = "",
        var url: String? = "",
        var visit: Int? = 0
)
