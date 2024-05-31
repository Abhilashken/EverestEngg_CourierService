package main.model

data class PackageDetail(
    var id: String = "",
    var weight: Int = 0,
    var distance: Int = 0,
    var offerCode: String = "",
    var discountAmount: Int = 0,
    var totalCostDelivery: Int = 0,
    var estimatedTime: Double = 0.0
) {
    constructor(id: String, weight: Int, distance: Int, offerCode: String) : this() {
        this.id = id
        this.weight = weight
        this.distance = distance
        this.offerCode = offerCode
    }

    fun getTranslatedPkgData(dataString: String): PackageDetail {
        val parts = dataString.trim().split(" ")
        if (parts.size != 4) {
            throw IllegalArgumentException("Invalid data format. Expected 4 parts: packageId, weight, distance, offerCode")
        }
        try {
            this.id = parts[0]
            this.weight = parts[1].toInt()
            this.distance = parts[2].toInt()
            this.offerCode = parts[3]
        } catch (e: Exception) {
            println("Invalid input. Please enter integers for weight and distance")
        }
        return this
    }

    override fun toString(): String {
        return super.toString()
    }

    fun toStringDeliveryTotalCostTimeEst() : String {
        return "$id $discountAmount $totalCostDelivery $estimatedTime"
    }

     fun toStringDeliveryCostEstimate(): String {
         return "$id $discountAmount $totalCostDelivery"
     }
}



