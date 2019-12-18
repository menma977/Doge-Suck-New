package com.dogesuck.model

class Config {
    fun getOnPercentValue(balance: Double): Int {
        when {
            balance > 240000 -> {
                return 40800000
            }
            balance > 230000 && balance <= 240000 -> {
                return 39100000
            }
            balance > 220000 && balance <= 230000 -> {
                return 37400000
            }
            balance > 210000 && balance <= 220000 -> {
                return 35700000
            }
            balance > 200000 && balance <= 210000 -> {
                return 34000000
            }
            balance > 190000 && balance <= 200000 -> {
                return 32300000
            }
            balance > 180000 && balance <= 190000 -> {
                return 30600000
            }
            balance > 170000 && balance <= 180000 -> {
                return 28900000
            }
            balance > 160000 && balance <= 170000 -> {
                return 27200000
            }
            balance > 150000 && balance <= 160000 -> {
                return 25500000
            }
            balance > 140000 && balance <= 150000 -> {
                return 23800000
            }
            balance > 130000 && balance <= 140000 -> {
                return 22100000
            }
            balance > 120000 && balance <= 130000 -> {
                return 20400000
            }
            balance > 110000 && balance <= 120000 -> {
                return 18700000
            }
            balance > 100000 && balance <= 110000 -> {
                return 17000000
            }
            balance > 90000 && balance <= 100000 -> {
                return 15300000
            }
            balance > 80000 && balance <= 90000 -> {
                return 13600000
            }
            balance > 70000 && balance <= 80000 -> {
                return 11900000
            }
            balance > 60000 && balance <= 70000 -> {
                return 10200000
            }
            balance > 50000 && balance <= 60000 -> {
                return 8500000
            }
            balance > 40000 && balance <= 50000 -> {
                return 6800000
            }
            balance > 30000 && balance <= 40000 -> {
                return 5100000
            }
            balance > 20000 && balance <= 30000 -> {
                return 4000000
            }
            else -> {
                return 3000000
            }
        }
    }

    fun getThreePercentValue(balance: Double): Int {
        when {
            balance > 240000 -> {
                return 57600000
            }
            balance > 230000 && balance >= 240000 -> {
                return 55200000
            }
            balance > 220000 && balance <= 230000 -> {
                return 52800000
            }
            balance > 210000 && balance <= 220000 -> {
                return 50400000
            }
            balance > 200000 && balance <= 210000 -> {
                return 48000000
            }
            balance > 190000 && balance <= 200000 -> {
                return 45600000
            }
            balance > 180000 && balance <= 190000 -> {
                return 43200000
            }
            balance > 170000 && balance <= 180000 -> {
                return 40800000
            }
            balance > 160000 && balance <= 170000 -> {
                return 38400000
            }
            balance > 150000 && balance <= 160000 -> {
                return 36000000
            }
            balance > 140000 && balance <= 150000 -> {
                return 33600000
            }
            balance > 130000 && balance <= 140000 -> {
                return 31200000
            }
            balance > 120000 && balance <= 130000 -> {
                return 28800000
            }
            balance > 110000 && balance <= 120000 -> {
                return 26400000
            }
            balance > 100000 && balance <= 110000 -> {
                return 24000000
            }
            balance > 90000 && balance <= 100000 -> {
                return 21600000
            }
            balance > 80000 && balance <= 90000 -> {
                return 19200000
            }
            balance > 70000 && balance <= 80000 -> {
                return 16800000
            }
            balance > 60000 && balance <= 70000 -> {
                return 14400000
            }
            balance > 50000 && balance <= 60000 -> {
                return 12000000
            }
            balance > 40000 && balance <= 50000 -> {
                return 9600000
            }
            balance > 30000 && balance <= 40000 -> {
                return 7200000
            }
            balance > 20000 && balance <= 30000 -> {
                return 5000000
            }
            else -> {
                return 3500000
            }
        }
    }

    fun getFivePercentValue(balance: Double): Int {
        when {
            balance > 240000 -> {
                return 81600000
            }
            balance > 230000 && balance >= 240000 -> {
                return 78200000
            }
            balance > 220000 && balance <= 230000 -> {
                return 74800000
            }
            balance > 210000 && balance <= 220000 -> {
                return 71400000
            }
            balance > 200000 && balance <= 210000 -> {
                return 68000000
            }
            balance > 190000 && balance <= 200000 -> {
                return 64600000
            }
            balance > 180000 && balance <= 190000 -> {
                return 61200000
            }
            balance > 170000 && balance <= 180000 -> {
                return 57800000
            }
            balance > 160000 && balance <= 170000 -> {
                return 54400000
            }
            balance > 150000 && balance <= 160000 -> {
                return 51000000
            }
            balance > 140000 && balance <= 150000 -> {
                return 47600000
            }
            balance > 130000 && balance <= 140000 -> {
                return 44200000
            }
            balance > 120000 && balance <= 130000 -> {
                return 40800000
            }
            balance > 110000 && balance <= 120000 -> {
                return 37400000
            }
            balance > 100000 && balance <= 110000 -> {
                return 34000000
            }
            balance > 90000 && balance <= 100000 -> {
                return 30600000
            }
            balance > 80000 && balance <= 90000 -> {
                return 27200000
            }
            balance > 70000 && balance <= 80000 -> {
                return 23800000
            }
            balance > 60000 && balance <= 70000 -> {
                return 20400000
            }
            balance > 50000 && balance <= 60000 -> {
                return 17000000
            }
            balance > 40000 && balance <= 50000 -> {
                return 13600000
            }
            balance > 30000 && balance <= 40000 -> {
                return 10200000
            }
            balance > 20000 && balance <= 30000 -> {
                return 9000000
            }
            else -> {
                return 6000000
            }
        }
    }

    fun getMaxPayIn(balance: Double): Long {
        when {
            balance >= 240000 -> {
                return 120000000000
            }
            balance in 220000.0..230000.0 -> {
                return 110000000000
            }
            balance in 200000.0..210000.0 -> {
                return 100000000000
            }
            balance in 180000.0..190000.0 -> {
                return 90000000000
            }
            balance in 160000.0..170000.0 -> {
                return 80000000000
            }
            balance in 140000.0..150000.0 -> {
                return 70000000000
            }
            balance in 120000.0..130000.0 -> {
                return 60000000000
            }
            balance in 100000.0..110000.0 -> {
                return 50000000000
            }
            balance in 80000.0..90000.0 -> {
                return 40000000000
            }
            balance in 60000.0..70000.0 -> {
                return 30000000000
            }
            balance in 40000.0..50000.0 -> {
                return 20000000000
            }
            else -> {
                return 10000000000
            }
        }
    }

    fun getLabor(balance: Double): Double {
        when {
            balance > 240000 -> {
                return 2.4
            }
            balance > 230000 && balance <= 240000 -> {
                return 2.3
            }
            balance > 220000 && balance <= 230000 -> {
                return 2.2
            }
            balance > 210000 && balance <= 220000 -> {
                return 2.1
            }
            balance > 200000 && balance <= 210000 -> {
                return 2.0
            }
            balance > 190000 && balance <= 200000 -> {
                return 1.9
            }
            balance > 180000 && balance <= 190000 -> {
                return 1.8
            }
            balance > 170000 && balance <= 180000 -> {
                return 1.7
            }
            balance > 160000 && balance <= 170000 -> {
                return 1.6
            }
            balance > 150000 && balance <= 160000 -> {
                return 1.5
            }
            balance > 140000 && balance <= 150000 -> {
                return 1.4
            }
            balance > 130000 && balance <= 140000 -> {
                return 1.3
            }
            balance > 120000 && balance <= 130000 -> {
                return 1.2
            }
            balance > 110000 && balance <= 120000 -> {
                return 1.1
            }
            balance > 100000 && balance <= 110000 -> {
                return 1.0
            }
            balance > 90000 && balance <= 100000 -> {
                return 0.9
            }
            balance > 80000 && balance <= 90000 -> {
                return 0.8
            }
            balance > 70000 && balance <= 80000 -> {
                return 0.7
            }
            balance > 60000 && balance <= 70000 -> {
                return 0.6
            }
            balance > 50000 && balance <= 60000 -> {
                return 0.5
            }
            balance > 40000 && balance <= 50000 -> {
                return 0.4
            }
            balance > 30000 && balance <= 40000 -> {
                return 0.3
            }
            balance > 20000 && balance <= 30000 -> {
                return 0.2
            }
            else -> {
                return 0.1
            }
        }
    }
}