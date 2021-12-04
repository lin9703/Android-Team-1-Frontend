package yapp.android1.domain.repository

import yapp.android1.domain.NetworkResult
import yapp.android1.domain.entity.Address

interface CoordToAddressRepository {
    suspend fun convertCoordToAddress(lat: Double, lng: Double): NetworkResult<Address>
}