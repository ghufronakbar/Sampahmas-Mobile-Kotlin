package com.example.sampahmasgabungan

import android.os.Parcel
import android.os.Parcelable

data class ProductVariable(var id: String, var title: String, var image: String, var price: String, var description: String, var quantity: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductVariable> {
        override fun createFromParcel(parcel: Parcel): ProductVariable {
            return ProductVariable(parcel)
        }

        override fun newArray(size: Int): Array<ProductVariable?> {
            return arrayOfNulls(size)
        }
    }
}
