package main.features

import main.model.CostMetadata
import main.model.FleetInfo
import main.model.PackageDetail
import main.model.VehicleInfo
import main.utils.ShipmentSetComparatorWeight
import java.util.*

class DeliveryTimeEstimation {

    /**
     * Prompts the user to enter base delivery cost and number of packages,
     * then calculates and prints the delivery time for each package.
     */
    fun calculateDeliveryTimePrompt()
    {
        val scanner = Scanner(System.`in`)
        var validInput = false
        lateinit var parsedCostMeta: CostMetadata

        while (!validInput) {
            println("Enter base delivery cost(number) and number of packages (separated by space):")
            val inputBaseCostPackagesCount = scanner.nextLine().trim()  // Trim whitespace
            val pkgCostMetadata = inputBaseCostPackagesCount.split(" ")

            try {
                parsedCostMeta = parseCostMetadata(pkgCostMetadata )
                validInput = true
            } catch (e : NumberFormatException) {
                println("Invalid Input")
                validInput = false
            }

            if (pkgCostMetadata.size != 2) {
                println("Invalid input format. Please enter base delivery cost and number of packages separated by space.")
            } else {
                try {
                    validInput = true

                    val deliveryCostCalculator = DeliveryCostCalculator()

                    val packageDetailsList = deliveryCostCalculator.getPkgDataCodeFromUser(scanner, parsedCostMeta.packageCount)

                    for (packg in packageDetailsList) {
                        deliveryCostCalculator.pkgDiscCostCalculate(packg, parsedCostMeta.baseCost)
                    }
                    val fleetInfo = getFleetDataCodeFromUser(scanner)

                    val shipmentsSortedList = groupPackagesIntoShipments(packageDetailsList, fleetInfo.maxVehicleLoad)

                    val distributedShipmentToFleet = doDistributionDeliveryTask(fleetInfo, shipmentsSortedList)

                    val finalResultString = reorderPackages(packageDetailsList, distributedShipmentToFleet)

                    finalResultString.forEach {
                        println(
                            "${it.id} ${it.discountAmount} ${it.totalCostDelivery} ${
                                String.format("%.2f", it.estimatedTime)
                            }"
                        )
                    }
                } catch (e: NumberFormatException) {
                    println("Invalid input. Please enter integers for base delivery cost and number of packages.")
                } catch (e: IllegalArgumentException) {
                    println(e.message)  // Print specific error for invalid package or vehicle format
                }
            }
        }
    }

    /**
     * Parses the cost metadata from the user input.
     *
     * @param pkgCostMetadata List of strings containing base delivery cost and number of packages.
     * @return CostMetadata object containing parsed base delivery cost and number of packages.
     * @throws Exception if input format is invalid or cannot be parsed as integers.
     */
    private fun parseCostMetadata(pkgCostMetadata: List<String>): CostMetadata {
        if (pkgCostMetadata.size != 2) {
            throw Exception("Invalid input format. Please enter base delivery cost and number of packages separated by space.")
        }

        try {
            return CostMetadata(pkgCostMetadata[0].toInt(), pkgCostMetadata[1].toInt())
        } catch (exception: NumberFormatException) {
            throw Exception("Invalid input. Please enter valid integers for base delivery cost and number of packages.")
        }
    }

    /**
     * Prompts the user to enter fleet details.
     *
     * @param scanner Scanner object to read user input.
     * @return FleetInfo object containing parsed fleet information.
     */
    fun getFleetDataCodeFromUser(scanner: Scanner): FleetInfo {
        var validInput = false
        while (!validInput) {
            println("Enter number of vehicles, max speed, and max carriable weight (separated by space):")
            val inputVehicleDetails = scanner.nextLine().trim().split(" ")
            if (inputVehicleDetails.size != 3) {
                println("Invalid input format. Please enter three integers separated by space (number of vehicles, max speed, max weight).")
            } else {
                try {
                    val noOfVehicle = inputVehicleDetails[0].toInt()
                    val maxSpeed = inputVehicleDetails[1].toInt()
                    val maxVehicleLoad = inputVehicleDetails[2].toInt()
                    validInput = true
                    return FleetInfo(noOfVehicle, maxSpeed, maxVehicleLoad)
                } catch (e: NumberFormatException) {
                    println("Invalid input. Please enter integers only.")
                }
            }
        }
        return FleetInfo(0, 0, 0)
    }

    /**
     * Groups packages into shipments based on the maximum vehicle load.
     *
     * @param packages List of PackageDetail objects to be grouped.
     * @param maxWeight Maximum load that a vehicle can carry.
     * @return List of sets of PackageDetail objects grouped into shipments.
     */
    fun groupPackagesIntoShipments(packages: List<PackageDetail>, maxWeight: Int): List<Set<PackageDetail>> {
        val sortedPkg = packages.sortedByDescending { it.weight }

        val listOfShipment: MutableList<Set<PackageDetail>> = ArrayList()
        val used = BooleanArray(packages.size)

        while (true) {
            val currentShipment: MutableSet<PackageDetail> = HashSet()
            var currentLoad = 0
            var addedAny = false

            for (i in sortedPkg.indices) {
                if (!used[i] && currentLoad + sortedPkg[i].weight <= maxWeight) {
                    currentShipment.add(sortedPkg[i])
                    currentLoad += sortedPkg[i].weight
                    used[i] = true
                    addedAny = true
                }
            }

            if (!addedAny) {
                break
            }
            listOfShipment.add(currentShipment)
        }
        Collections.sort(listOfShipment, ShipmentSetComparatorWeight())
        return listOfShipment
    }

    /**
     * Distributes the shipments to the available vehicles and calculates the delivery time.
     *
     * @param vehicleDetails FleetInfo object containing vehicle information.
     * @param shipmentsSortedList List of sets of PackageDetail objects grouped into shipments.
     * @return List of PackageDetail objects with updated estimated delivery times.
     */
    fun doDistributionDeliveryTask(
        vehicleDetails: FleetInfo,
        shipmentsSortedList: List<Set<PackageDetail>>
    ): List<PackageDetail> {

        val processedPackageList = mutableListOf<PackageDetail>()
        val vehicleInfoList = mutableListOf<VehicleInfo>()
        for (i in 1..vehicleDetails.noOfVehicle) {
            vehicleInfoList.add(VehicleInfo(i))
        }

        for (index in shipmentsSortedList.indices) {
            val shipmentsSet = shipmentsSortedList[index]
            val availableVehicle = getAvailableVehicle(vehicleInfoList)
            val vehicleID = availableVehicle.vehicleID
            val availableVehicleIndex = vehicleInfoList.indexOfFirst { it.vehicleID == vehicleID }
            var temp = 0.0

            shipmentsSet.sortedByDescending { it.distance }
                .forEachIndexed { index, packageDetail ->
                    val tempTime =
                        truncateToTwoDecimalPlaces(packageDetail.distance.toDouble() / vehicleDetails.maxSpeed).toDouble()

                    packageDetail.estimatedTime = tempTime + availableVehicle.timeDuration
                    processedPackageList.add(packageDetail)
                    if (index == 0) {
                        temp = tempTime
                    }
                }

            val round = if (index == shipmentsSortedList.size - 1) 1 else 2
            val updatedTimeDuration = availableVehicle.timeDuration + (round * temp)
            vehicleInfoList[availableVehicleIndex] = availableVehicle.copy(timeDuration = updatedTimeDuration)
        }
        return processedPackageList
    }

    /**
     * Reorders the packages in the new list to match the order of the original list.
     *
     * @param oldList Original list of PackageDetail objects.
     * @param newList New list of PackageDetail objects with updated information.
     * @return Reordered list of PackageDetail objects.
     */
    fun reorderPackages(oldList: List<PackageDetail>, newList: List<PackageDetail>): List<PackageDetail> {
        return newList.sortedBy { packageItem -> oldList.indexOfFirst { it.id == packageItem.id } }
    }

    /**
     * Gets the available vehicle with the least time duration.
     *
     * @param vehicleInfoList List of VehicleInfo objects containing vehicle information.
     * @return VehicleInfo object of the available vehicle with the least time duration.
     */
    private fun getAvailableVehicle(vehicleInfoList: List<VehicleInfo>): VehicleInfo {
        return vehicleInfoList.minBy {
            it.timeDuration
        }
    }

    /**
     * Calculates the delivery time for a set of packages based on vehicle speed.
     *
     * @param shipmentsSet Set of PackageDetail objects to calculate delivery time for.
     * @param vehicleSpeed Speed of the vehicle.
     * @return Estimated delivery time for the set of packages.
     */
    fun calculateDeliveryTime(shipmentsSet: Set<PackageDetail>, vehicleSpeed: Int): Double {
        var deliveryTime = 0.0
        shipmentsSet.sortedByDescending { it.distance }.forEachIndexed { index, shipment ->
            if (index == 0) {
                deliveryTime += ((2 * shipment.distance.toDouble()) / vehicleSpeed)
            }
        }
        return deliveryTime
    }

    /**
     * Truncates a double value to two decimal places and returns it as a string.
     *
     * @param value Double value to be truncated.
     * @return Truncated value as a string with two decimal places.
     */
    fun truncateToTwoDecimalPlaces(value: Double): String {
        val factor = 100
        val truncatedValue = (value * factor).toInt() / factor.toFloat()
        return String.format("%.2f", truncatedValue)
    }
}