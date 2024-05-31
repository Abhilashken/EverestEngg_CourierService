package test

import main.features.DeliveryCostCalculator
import main.model.OfferDetails
import main.model.PackageDetail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

class TestDeliveryCostCalculator {

    private lateinit var deliveryCostCalculator: DeliveryCostCalculator

    @BeforeEach
    fun setUp() {
        deliveryCostCalculator = DeliveryCostCalculator()
    }

    @Test
    fun testCalculateDeliveryCostPrompt_ValidInput() {
        val input = "100 2\nPKG1 50 30 OFR001\nPKG2 75 125 OFR003\n"
        val inputStream = ByteArrayInputStream(input.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        deliveryCostCalculator.calculateDeliveryCostPrompt()

        val output = outputStream.toString().trim()
        assertTrue(output.contains("Enter base_delivery_cost and no_of_packages:"))
        assertTrue(output.contains("PKG1"))
        assertTrue(output.contains("PKG2"))
    }

    @Test
    fun testGetPkgDataCodeFromUser_ValidInput() {
        val input = "PKG1 50 30 OFR001\nPKG2 75 125 OFR003\n"
        val inputStream = ByteArrayInputStream(input.toByteArray())
        System.setIn(inputStream)

        val scanner = Scanner(System.`in`)
        val packageDetails = deliveryCostCalculator.getPkgDataCodeFromUser(scanner, 2)

        assertEquals(2, packageDetails.size)
        assertEquals("PKG1", packageDetails[0].id)
        assertEquals("PKG2", packageDetails[1].id)
    }

    @Test
    fun testCalculateTotalPackageDeliveryCost() {
        val input = "PKG1 50 30 OFR001\nPKG2 75 125 OFR003\n"
        val inputStream = ByteArrayInputStream(input.toByteArray())
        System.setIn(inputStream)

        val scanner = Scanner(System.`in`)
        val packageDetails = deliveryCostCalculator.calculateTotalPackageDeliveryCost(scanner, 2, 100)

        assertEquals(2, packageDetails.size)
        assertEquals("PKG1", packageDetails[0].id)
        assertEquals("PKG2", packageDetails[1].id)
        assertTrue(packageDetails[0].totalCostDelivery > 0)
        assertTrue(packageDetails[1].totalCostDelivery > 0)
    }

    @Test
    fun testPkgDiscCostCalculate() {
        val pkgDetail = PackageDetail("PKG1", 10, 100, "OFR003", 0, 0)
        deliveryCostCalculator.pkgDiscCostCalculate(pkgDetail, 100)

        assertEquals(35, pkgDetail.discountAmount )
        assertEquals(665, pkgDetail.totalCostDelivery)
    }

    @Test
    fun testGetPkgRawDeliveryCost() {
        val rawCost = deliveryCostCalculator.getPkgRawDeliveryCost(100, 50, 30)
        assertEquals(750, rawCost)  // 100 + (50 * 10) + (30 * 5)
    }

    @Test
    fun testGetDiscountFromOffer_ValidOffer() {

        val discount = deliveryCostCalculator.getDiscountFromOffer(10, 100, "OFR003", 700)
        assertEquals(35, discount)  // 5% of 700
    }

    @Test
    fun testGetDiscountFromOffer_InvalidOffer() {
        val discount = deliveryCostCalculator.getDiscountFromOffer(50, 30, "INVALID", 750)
        assertEquals(0, discount)
    }

    @Test
    fun testIsOfferApplicable() {
        val offerDetails = OfferDetails(10, 0..200, 70..200)
        val isApplicable = deliveryCostCalculator.isOfferApplicable(offerDetails, 100, 100)
        assertTrue(isApplicable)

        val isNotApplicable = deliveryCostCalculator.isOfferApplicable(offerDetails, 50, 100)
        assertFalse(isNotApplicable)
    }
}