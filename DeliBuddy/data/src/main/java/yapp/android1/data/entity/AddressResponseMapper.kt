package yapp.android1.data.entity

import yapp.android1.domain.entity.Address

object AddressResponseMapper {
    fun toAddress(keywordDocument: KeywordDocument): Address {
        keywordDocument.apply {
            return Address(
                addressName = placeName,
                roadAddress = roadAddressName,
                address = addressName,
                lat = y.toDouble(),
                lon = x.toDouble()
            )
        }
    }

    fun toAddress(addressDocument: AddressDocument): Address {
        addressDocument.apply {
            return Address(
                addressName = addressName,
                roadAddress = roadAddress.addressName,
                address = address.addressName,
                lat = y.toDouble(),
                lon = x.toDouble()
            )
        }
    }
}