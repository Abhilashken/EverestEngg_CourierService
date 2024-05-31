package test

import main.features.DeliveryCostCalculator
import main.features.DeliveryTimeEstimation
import main.model.FleetInfo
import main.model.PackageDetail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TestDeliveryTimeEstimation {
    private lateinit var deliveryTimeEstimation: DeliveryTimeEstimation
    private lateinit var scanner: Scanner

    @BeforeEach
    fun setUp() {
        deliveryTimeEstimation = DeliveryTimeEstimation()
        scanner = Scanner(System.`in`)
    }

    @Test
    fun testReorderPackages() {
        val oldList = listOf(
            PackageDetail("P1", 10, 100, "", 50, 10),
            PackageDetail("P2", 20, 200, "", 100, 20)
        )
        val newList = listOf(
            PackageDetail("P2", 20, 200, "", 100, 20),
            PackageDetail("P1", 10, 100, "", 50, 10)
        )

        val result = deliveryTimeEstimation.reorderPackages(oldList, newList)
        assertEquals("P1", result[0].id)
        assertEquals("P2", result[1].id)
    }

    @Test
    fun testDoDistributionDeliveryTask() {
        val vehicleDetails = FleetInfo(2, 60, 200)
        val packageDetailsList = listOf(
            PackageDetail("P1", 10, 100, "", 50, 0),
            PackageDetail("P2", 20, 200, "", 100, 0)
        )
        val shipmentsSortedList =
            deliveryTimeEstimation.groupPackagesIntoShipments(packageDetailsList, vehicleDetails.maxVehicleLoad)

        val result = deliveryTimeEstimation.doDistributionDeliveryTask(vehicleDetails, shipmentsSortedList)

        // Expected results will depend on your logic
        assertEquals(2, result.size)
        // Further assertions can be added based on the logic of doDistributionDeliveryTask
    }

    @Test
    fun testCalculateDeliveryTime() {
        val packageDetailsList = setOf(
            PackageDetail("P1", 10, 100, "", 50, 0),
            PackageDetail("P2", 20, 200, "", 100, 0)
        )
        val vehicleSpeed = 60


        val result = deliveryTimeEstimation.calculateDeliveryTime(packageDetailsList, vehicleSpeed)

        //Package with longest distance will considered always
        // and return journey will be accounted
        assertEquals(((200 * 2).toDouble() / 60), result)
    }

    @Test
    fun testGetFleetDataCodeFromUser() {
        val scannerInput = "2 60 200"
        val scanner = Scanner(scannerInput)

        val result = deliveryTimeEstimation.getFleetDataCodeFromUser(scanner)
        assertEquals(FleetInfo(2, 60, 200), result)
    }

    @Test
    fun testGroupPackagesIntoShipments() {
        val packageDetailsList = listOf(
            PackageDetail("PKG1", 50, 30, "OFR001"),
            PackageDetail("PKG2", 75, 125, "OFR0008"),
            PackageDetail("PKG3", 175, 100, "OFFR003"),
            PackageDetail("PKG4", 110, 60, "OFR002"),
            PackageDetail("PKG5", 155, 95, "NA")
        )
        val maxWeight = 200

//        while (true) {
//            val currentShipment: MutableSet<PackageDetail> = HashSet()
//            var currentLoad = 0
//            var addedAny = false
//
//            for (i in sortedPkg.indices) {
//                if (!used[i] && currentLoad + sortedPkg[i].weight <= maxWeight) {
//                    currentShipment.add(sortedPkg[i])
//                    currentLoad += sortedPkg[i].weight
//                    used[i] = true
//                    addedAny = true
//                }
//            }
//
//            if (!addedAny) {
//                break
//            }
//            listOfShipment.add(currentShipment)
//        }
//        Collections.sort(listOfShipment, ShipmentSetComparatorWeight())
//

        val expectedlist = listOf(
            setOf(PackageDetail("PKG4", 110, 60, "OFR002"), PackageDetail("PKG2", 75, 125, "OFR0008")),
            setOf(PackageDetail("PKG3", 175, 100, " OFFR003")),
            setOf(PackageDetail("PKG5", 155, 95, "NA")),
            setOf(PackageDetail("PKG1", 50, 30, "OFR001"))
        )

        val result = deliveryTimeEstimation.groupPackagesIntoShipments(packageDetailsList, maxWeight)

        assertEquals(expectedlist.size, result.size)
    }

    @Test
    fun testTruncateToTwoDecimalPlaces() {
        val value = 123.456789
        val result = deliveryTimeEstimation.truncateToTwoDecimalPlaces(value)
        assertEquals("123.45", result)
    }

    // Additional test cases for different scenarios
    @Test
    fun testCalculateDeliveryCostWithOffers() {
        val deliveryCostCalculator = DeliveryCostCalculator()
        val baseDeliveryCost = 100
        val packageDetailsList = listOf(
            PackageDetail("PKG1", 5, 5, "OFR001", 0, 0),
            PackageDetail("PKG2", 15, 5, "OFR002", 0, 0),
            PackageDetail("PKG3", 10, 100, "OFR003", 0, 0),
            PackageDetail("PKG4", 110, 60, "OFR002", 0, 0),
            PackageDetail("PKG5", 155, 95, "NA", 0, 0)
        )

        val expectedResults = listOf(
            PackageDetail("PKG1", 5, 5, "OFR001", 0, 175),
            PackageDetail("PKG2", 15, 5, "OFR002", 0, 275),
            PackageDetail("PKG3", 10, 100, "OFR003", 0, 665),
            PackageDetail("PKG4", 110, 60, "OFR002", 0, 1395),
            PackageDetail("PKG5", 155, 95, "NA", 0, 2125)
        )

        packageDetailsList.forEachIndexed { index, packageDetail ->
            deliveryCostCalculator.pkgDiscCostCalculate(packageDetail, baseDeliveryCost)
            assertEquals(expectedResults[index].totalCostDelivery, packageDetail.totalCostDelivery)
        }
    }

    @Test
    fun testFullFlowWithSampleInput() {
        val inputBaseCostPackagesCount = "100 5"
        val packageDetailsList = listOf(
            PackageDetail("PKG1", 50, 30, "OFR001", 0, 0),
            PackageDetail("PKG2", 75, 125, "OFFR0008", 0, 0),
            PackageDetail("PKG3", 175, 100, "OFFR003", 0, 0),
            PackageDetail("PKG4", 110, 60, "OFR002", 0, 0),
            PackageDetail("PKG5", 155, 95, "NA", 0, 0)
        )
        val vehicleDetails = FleetInfo(2, 70, 200)

        val deliveryCostCalculator = DeliveryCostCalculator()
        packageDetailsList.forEach {
            deliveryCostCalculator.pkgDiscCostCalculate(it, inputBaseCostPackagesCount.split(" ")[0].toInt())
        }

        val shipmentsSortedList =
            deliveryTimeEstimation.groupPackagesIntoShipments(packageDetailsList, vehicleDetails.maxVehicleLoad)
        val distributedShipmentToFleet =
            deliveryTimeEstimation.doDistributionDeliveryTask(vehicleDetails, shipmentsSortedList)
        val finalResultString = deliveryTimeEstimation.reorderPackages(packageDetailsList, distributedShipmentToFleet)

        val expectedResults = listOf(
            PackageDetail("PKG1", 50, 30, "OFR001", 0, 750, estimatedTime = 3.98),
            PackageDetail("PKG2", 75, 125, "OFFR0", 0, 1475, estimatedTime = 1.78),
            PackageDetail("PKG3", 175, 100, "OFFR", 0, 2350, estimatedTime = 1.42),
            PackageDetail("PKG4", 110, 60, "OFFR0", 105, 1395, estimatedTime = 0.85),
            PackageDetail("PKG5", 155, 95, "NA", 0, 2125, estimatedTime = 4.19)
        )

        finalResultString.forEachIndexed { index, packageDetail ->
            assertEquals(expectedResults[index].totalCostDelivery, packageDetail.totalCostDelivery)
            assertEquals(expectedResults[index].estimatedTime, packageDetail.estimatedTime, 0.01)
        }
    }


}
