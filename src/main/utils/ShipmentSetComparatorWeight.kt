package main.utils

import main.model.PackageDetail

class ShipmentSetComparatorWeight : Comparator<Set<PackageDetail>> {
    override fun compare(set1: Set<PackageDetail>, set2: Set<PackageDetail>): Int {
        val sum1 = set1.sumOf { it.weight }
        val sum2 = set2.sumOf { it.weight }
        return sum2.compareTo(sum1)
    }
}
