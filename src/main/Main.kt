package main

import main.features.DeliveryCostCalculator
import main.features.DeliveryTimeEstimation


fun main() {
    /**
     * Problem 1 : Solution
     * IP : 100 5
     *      PKG1 50 30 OFR001
     *      PKG2 75 125 OFR0008
     *      PKG3 175 100 OFFR003
     *      PKG4 110 60 OFR002
     *      PKG5 155 95 NA
     *
     * OP : PKG1 0 750
     *      PKG2 0 1475
     *      PKG3 0 2350
     *      PKG4 105 1395
     *      PKG5 0 2125
     * */
    DeliveryCostCalculator().calculateDeliveryCostPrompt()


    /**
     * Problem 2 : Solution
     *
     * IP : 100 5
     *      PKG1 50 30 OFR001
     *      PKG2 75 125 OFR0008
     *      PKG3 175 100 OFFR003
     *      PKG4 110 60 OFR002
     *      PKG5 155 95 NA
     *      2 70 200
     *
     * OP : PKG1 0 750 3.98
     *      PKG2 0 1475 1.78
     *      PKG3 0 2350 1.42
     *      PKG4 105 1395 0.85
     *      PKG5 0 2125 4.19
     * */
    // DeliveryTimeEstimation().calculateDeliveryTimePrompt()

}