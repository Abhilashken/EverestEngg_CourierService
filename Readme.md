<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<h1>Delivery Time and Cost Estimation</h1>

<p>This project provides a Kotlin implementation for calculating the delivery cost and estimating the delivery time for packages using a fleet of vehicles. The main classes involved are <code>DeliveryCostCalculator</code> and <code>DeliveryTimeEstimation</code>.</p>

<h2>Classes Overview</h2>

<h3><code>DeliveryCostCalculator</code></h3>
<p>This class handles the calculation of delivery costs for packages. It includes methods to prompt user input, calculate raw delivery costs, apply discounts, and compute the total delivery cost for each package.</p>

<h4>Methods</h4>
<ul>
    <li>
        <b>calculateDeliveryCostPrompt()</b>
        <p>Prompts the user to enter the base delivery cost and the number of packages, then calculates and prints the delivery cost for each package.</p>
    </li>
    <li>
        <b>getPkgDataCodeFromUser(scanner: Scanner, numPackages: Int): List&lt;PackageDetail&gt;</b>
        <p>Prompts the user to enter the details of each package and returns a list of <code>PackageDetail</code> objects.</p>
    </li>
    <li>
        <b>calculateTotalPackageDeliveryCost(scanner: Scanner, noOfPackages: Int, baseDeliveryCost: Int): List&lt;PackageDetail&gt;</b>
        <p>Calculates the total delivery cost for all packages based on the provided base delivery cost.</p>
    </li>
    <li>
        <b>pkgDiscCostCalculate(pkgDetail: PackageDetail, baseDeliveryCost: Int)</b>
        <p>Calculates the delivery cost and discount for a single package.</p>
    </li>
    <li>
        <b>getPkgRawDeliveryCost(baseDeliveryCost: Int, weight: Int, distance: Int): Int</b>
        <p>Computes the raw delivery cost based on base cost, weight, and distance.</p>
    </li>
    <li>
        <b>getDiscountFromOffer(weight: Int, distance: Int, offerCode: String, rawDeliveryCost: Int): Int</b>
        <p>Calculates the discount for a package based on its offer code, weight, and distance.</p>
    </li>
    <li>
        <b>isOfferApplicable(offer: OfferDetails, weight: Int, distance: Int): Boolean</b>
        <p>Checks if a discount offer is applicable for a given package based on its weight and distance.</p>
    </li>
</ul>

<h3><code>DeliveryTimeEstimation</code></h3>
<p>This class handles the estimation of delivery times for packages using a fleet of vehicles. It includes methods to prompt user input, group packages into shipments, distribute shipments to vehicles, and calculate delivery times.</p>

<h4>Methods</h4>
<ul>
    <li>
        <b>calculateDeliveryTimePrompt()</b>
        <p>Prompts the user to enter the base delivery cost and number of packages, then calculates and prints the delivery time for each package.</p>
    </li>
    <li>
        <b>parseCostMetadata(pkgCostMetadata: List&lt;String&gt;): CostMetadata</b>
        <p>Parses the cost metadata from the user input.</p>
    </li>
    <li>
        <b>getFleetDataCodeFromUser(scanner: Scanner): FleetInfo</b>
        <p>Prompts the user to enter fleet details and returns a <code>FleetInfo</code> object.</p>
    </li>
    <li>
        <b>groupPackagesIntoShipments(packages: List&lt;PackageDetail&gt;, maxWeight: Int): List&lt;Set&lt;PackageDetail&gt;&gt;</b>
        <p>Groups packages into shipments based on the maximum vehicle load.</p>
    </li>
    <li>
        <b>doDistributionDeliveryTask(vehicleDetails: FleetInfo, shipmentsSortedList: List&lt;Set&lt;PackageDetail&gt;&gt;): List&lt;PackageDetail&gt;</b>
        <p>Distributes the shipments to the available vehicles and calculates the delivery time.</p>
    </li>
    <li>
        <b>reorderPackages(oldList: List&lt;PackageDetail&gt;, newList: List&lt;PackageDetail&gt;): List&lt;PackageDetail&gt;</b>
        <p>Reorders the packages in the new list to match the order of the original list.</p>
    </li>
    <li>
        <b>getAvailableVehicle(vehicleInfoList: List&lt;VehicleInfo&gt;): VehicleInfo</b>
        <p>Gets the available vehicle with the least time duration.</p>
    </li>
    <li>
        <b>calculateDeliveryTime(shipmentsSet: Set&lt;PackageDetail&gt;, vehicleSpeed: Int): Double</b>
        <p>Calculates the delivery time for a set of packages based on vehicle speed.</p>
    </li>
    <li>
        <b>truncateToTwoDecimalPlaces(value: Double): String</b>
        <p>Truncates a double value to two decimal places and returns it as a string.</p>
    </li>
</ul>

<h2>Models Overview</h2>

<h3><code>PackageDetail</code></h3>
<p>This class represents the details of a package, including its ID, weight, distance, offer code, discount amount, and total delivery cost.</p>

<h3><code>OfferDetails</code></h3>
<p>This class represents the details of a discount offer, including the discount percentage, and valid ranges for weight and distance.</p>

<h3><code>CostMetadata</code></h3>
<p>This class represents the metadata for the cost calculation, including the base delivery cost and the number of packages.</p>

<h3><code>FleetInfo</code></h3>
<p>This class represents the details of the fleet, including the number of vehicles, maximum speed, and maximum load capacity.</p>

<h3><code>VehicleInfo</code></h3>
<p>This class represents the details of a single vehicle, including its ID and current time duration.</p>

<h2>Utilities Overview</h2>

<h3><code>ShipmentSetComparatorWeight</code></h3>
<p>This class provides a comparator to sort shipment sets based on their weight.</p>

<h2>Usage</h2>
<p>To use the classes and methods provided in this project, you need to create an instance of the respective classes and call their methods as required. Here is a simple example:</p>

<pre><code>fun main() {
    val deliveryCostCalculator = DeliveryCostCalculator()
    deliveryCostCalculator.calculateDeliveryCostPrompt()

    val deliveryTimeEstimation = DeliveryTimeEstimation()
    deliveryTimeEstimation.calculateDeliveryTimePrompt()
}
</code></pre>

<p>This example prompts the user to enter the required details and then calculates and prints the delivery cost and estimated delivery time for each package.</p>

<h2>Conclusion</h2>
<p>This project provides a comprehensive solution for calculating delivery costs and estimating delivery times for packages using a fleet of vehicles. By following the methods and models described above, you can easily integrate these functionalities into your own applications.</p>

</body>
</html>
