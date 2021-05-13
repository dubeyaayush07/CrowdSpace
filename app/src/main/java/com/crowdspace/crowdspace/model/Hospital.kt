package com.crowdspace.crowdspace.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class Hospital(
        @DocumentId
        var id: String? = "",
        var name: String? = "",
        var photoUrl: String? = "",
        var address: String? = "",
        var adminId: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(photoUrl)
        parcel.writeString(address)
        parcel.writeString(adminId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Hospital> {
        override fun createFromParcel(parcel: Parcel): Hospital {
            return Hospital(parcel)
        }

        override fun newArray(size: Int): Array<Hospital?> {
            return arrayOfNulls(size)
        }
    }
}

