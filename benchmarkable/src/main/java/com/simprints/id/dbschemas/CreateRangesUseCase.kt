package com.simprints.id.dbschemas

class CreateRangesUseCase {


    operator fun invoke(
        totalCount: Int,
        minBatchSize: Int = DEFAULT_BATCH_SIZE,
    ): List<IntRange> {
        val ranges = mutableListOf<IntRange>()
        var index = 1

        var nextBatchSize = minBatchSize
        var start = 0
        var end = nextBatchSize

        while (start < totalCount) {
            if (end > totalCount) {
                end = totalCount
            }
            ranges.add(start..end)
            start = end
            end += nextBatchSize

            // Make sure next batch is increased
            nextBatchSize = minBatchSize + (minBatchSize * index.coerceIn(1, 4))
            index++
        }
        return ranges
    }

    companion object {
        /**
         * Experimentally determined batch size that works well for most cases.
         */
        private const val DEFAULT_BATCH_SIZE = 1000
    }

}