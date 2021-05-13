package com.crowdspace.crowdspace.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class Business(
        @DocumentId
        var id: String? = "",
        var name: String? = "",
        var hospitalId: String? = "",
        var queue: List<String?>? = null,
        var status: String? = "",
        var adminId: String? = ""
): Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.createStringArrayList(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(id)
                parcel.writeString(name)
                parcel.writeString(hospitalId)
                parcel.writeStringList(queue)
                parcel.writeString(status)
                parcel.writeString(adminId)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Business> {
                override fun createFromParcel(parcel: Parcel): Business {
                        return Business(parcel)
                }

                override fun newArray(size: Int): Array<Business?> {
                        return arrayOfNulls(size)
                }
        }
}