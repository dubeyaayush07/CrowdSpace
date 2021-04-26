package com.crowdspace.crowdspace.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class User(
        @DocumentId
        var id: String? = "",
        var uid: String? = "",
        var name: String? = "",
        var token: String? = "",
        var profileId: String? = "",
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
                parcel.writeString(uid)
                parcel.writeString(name)
                parcel.writeString(token)
                parcel.writeString(profileId)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<User> {
                override fun createFromParcel(parcel: Parcel): User {
                        return User(parcel)
                }

                override fun newArray(size: Int): Array<User?> {
                        return arrayOfNulls(size)
                }
        }
}
