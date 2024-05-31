package main.features

import main.model.OfferDetails
import main.model.PackageDetail
import java.util.*

class DeliveryCostCalculator {

    /**
     * Prompts the user to enter base delivery cost and number of packages,
     * then calculates and prints the delivery cost for each package.
     */
    fun calculateDeliveryCostPrompt() {
        val scanner = Scanner(System.`in`)
        var validInput = false
        while (!validInput) {
            println("Enter base_delivery_cost and no_of_packages:")
            val inputBaseCostPackagesCount = scanner.nextLine()
            val inputItems = inputBaseCostPackagesCount.split(" ")

            if (inputItems.size != 2) {
                validInput = false
                println("Invalid input format. Please enter base delivery cost and number of packages separated by space.")
            } else {
                try {
                    val baseDeliveryCost = inputItems[0].toInt()
                    val numPackages = inputItems[1].toInt()
                    validInput = true

                    val deliveryResults = calculateTotalPackageDeliveryCost(scanner, numPackages, baseDeliveryCost)
                    deliveryResults.forEach {
                        println("${it.id} ${it.discountAmount} ${it.totalCostDelivery} ")
                    }
                } catch (e: NumberFormatException) {
                    println("Invalid input. Please enter integers for base delivery cost and number of packages.")
                    validInput = false
                }
            }
        }
    }

    /**
     * Prompts the user to enter package details for the specified number of packages.
     *
     * @param scanner Scanner object to read user input.
     * @param numPackages Number of packages to read details for.
     * @return List of PackageDetail objects containing the entered package details.
     */
    fun getPkgDataCodeFromUser(scanner: Scanner, numPackages: Int): List<PackageDetail> {
        val packageDetailsList = mutableListOf<PackageDetail>()
        println("Enter details for $numPackages packages (format: PKG1 50 30 OFR001):")
        for (i in 1..numPackages) {
            val packageDetailsString = scanner.nextLine().trim()
            if (packageDetailsString.isEmpty()) {
                println("Invalid input. Please enter package details.")
                continue  // Skip to the next iteration for empty input
            }
            packageDetailsList.add(PackageDetail().getTranslatedPkgData(packageDetailsString))
        }
        return packageDetailsList
    }


    /**
     * Calculates the total delivery cost for each package.
     *
     * @param scanner Scanner object to read user input.
     * @param noOfPackages Number of packages to calculate delivery cost for.
     * @param baseDeliveryCost Base delivery cost for calculation.
     * @return List of PackageDetail objects with updated delivery cost and discount details.
     */
    fun calculateTotalPackageDeliveryCost(
        scanner: Scanner,
        noOfPackages: Int,
        baseDeliveryCost: Int
    ): List<PackageDetail> {
        val packageList = getPkgDataCodeFromUser(scanner, noOfPackages)
        for (packg in packageList) {
            pkgDiscCostCalculate(packg, baseDeliveryCost)
        }

        return packageList
    }

    /**
     * Calculates the delivery cost and discount for a given package.
     *
     * @param pkgDetail PackageDetail object containing package information.
     * @param baseDeliveryCost Base delivery cost for calculation.
     */
    fun pkgDiscCostCalculate(pkgDetail: PackageDetail, baseDeliveryCost: Int) {
        val deliveryCost = getPkgRawDeliveryCost(baseDeliveryCost, pkgDetail.weight, pkgDetail.distance)
        val discount = getDiscountFromOffer(pkgDetail.weight, pkgDetail.distance, pkgDetail.offerCode, deliveryCost)
        val totalCost = deliveryCost - discount
        pkgDetail.discountAmount = discount
        pkgDetail.totalCostDelivery = totalCost
    }


    /**
     * Calculates the raw delivery cost for a package based on its weight and distance.
     *
     * @param baseDeliveryCost Base delivery cost for calculation.
     * @param weight Weight of the package.
     * @param distance Distance to deliver the package.
     * @return Raw delivery cost for the package.
     */
    fun getPkgRawDeliveryCost(baseDeliveryCost: Int, weight: Int, distance: Int): Int {
        return baseDeliveryCost + (weight * 10) + (distance * 5)
    }

    /**
     * Calculates the discount for a package based on the offer code and raw delivery cost.
     *
     * @param weight Weight of the package.
     * @param distance Distance to deliver the package.
     * @param offerCode Offer code for discount calculation.
     * @param rawDeliveryCost Raw delivery cost for the package.
     * @return Discount amount for the package.
     */
    fun getDiscountFromOffer(weight: Int, distance: Int, offerCode: String, rawDeliveryCost: Int): Int {
        val offerDiscounts = mapOf(
            "OFR001" to OfferDetails(10, distanceRange = 0..200, weightRange = 70..200),
            "OFR002" to OfferDetails(7, distanceRange = 50..150, weightRange = 100..250),
            "OFR003" to OfferDetails(5, distanceRange = 50..250, weightRange = 10..150),
        )

        val offerData = offerDiscounts[offerCode]
        return if (offerData != null && isOfferApplicable(offerData, weight, distance)) {
            (rawDeliveryCost * offerData.discount) / 100 // Adjust discount percentage as needed
        } else {
            0
        }
    }

    /**
     * Checks if an offer is applicable based on package weight and distance.
     *
     * @param offer OfferDetails object containing offer information.
     * @param weight Weight of the package.
     * @param distance Distance to deliver the package.
     * @return Boolean indicating whether the offer is applicable.
     */
    fun isOfferApplicable(offer: OfferDetails, weight: Int, distance: Int): Boolean {
        return distance in offer.distanceRange && weight in offer.weightRange
    }
}