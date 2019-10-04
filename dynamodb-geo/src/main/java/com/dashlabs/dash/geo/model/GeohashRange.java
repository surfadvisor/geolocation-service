package com.dashlabs.dash.geo.model;

import com.dashlabs.dash.geo.s2.internal.S2Manager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: blangel
 * Date: 7/19/17
 * Time: 1:38 PM
 *
 * Originally in {@code com.amazonaws.geo.model.GeohashRange} in DynamoDB-Geo (https://github.com/awslabs/dynamodb-geo),
 * moved to share across dynamodb-geo and s3 equivalent.
 */
public class GeohashRange {

    public static final long MERGE_THRESHOLD = 2;

    private long rangeMin;

    private long rangeMax;


    public GeohashRange(long range1, long range2) {
        this.rangeMin = Math.min(range1, range2);
        this.rangeMax = Math.max(range1, range2);
    }

    public boolean tryMerge(GeohashRange range) {
        if (range.getRangeMin() - this.rangeMax <= MERGE_THRESHOLD
                && range.getRangeMin() - this.rangeMax > 0) {
            this.rangeMax = range.getRangeMax();
            return true;
        }

        if (this.rangeMin - range.getRangeMax() <= MERGE_THRESHOLD
                && this.rangeMin - range.getRangeMax() > 0) {
            this.rangeMin = range.getRangeMin();
            return true;
        }

        return false;
    }

    /*
     * Try to split the range to multiple ranges based on the hash key.
     *
     * e.g., for the following range:
     *
     * min: 123456789
     * max: 125678912
     *
     * when the hash key length is 3, we want to split the range to:
     *
     * 1
     * min: 123456789
     * max: 123999999
     *
     * 2
     * min: 124000000
     * max: 124999999
     *
     * 3
     * min: 125000000
     * max: 125678912
     *
     * For this range:
     *
     * min: -125678912
     * max: -123456789
     *
     * we want:
     *
     * 1
     * min: -125678912
     * max: -125000000
     *
     * 2
     * min: -124999999
     * max: -124000000
     *
     * 3
     * min: -123999999
     * max: -123456789
     */
    public List<GeohashRange> trySplit(int hashKeyLength, S2Manager s2Manager) {
        List<GeohashRange> result = new ArrayList<GeohashRange>();

        long minHashKey = s2Manager.generateHashKey(rangeMin, hashKeyLength);
        long maxHashKey = s2Manager.generateHashKey(rangeMax, hashKeyLength);

        long denominator = (long) Math.pow(10, String.valueOf(rangeMin).length() - String.valueOf(minHashKey).length());

        if (minHashKey == maxHashKey) {
            result.add(this);
        } else {
            for (long l = minHashKey; l <= maxHashKey; l++) {
                if (l > 0) {
                    result.add(new GeohashRange(l == minHashKey ? rangeMin : l * denominator,
                            l == maxHashKey ? rangeMax : (l + 1) * denominator - 1));
                } else {
                    result.add(new GeohashRange(l == minHashKey ? rangeMin : (l - 1) * denominator + 1,
                            l == maxHashKey ? rangeMax : l * denominator));
                }
            }
        }

        return result;
    }

    public long getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(long rangeMin) {
        this.rangeMin = rangeMin;
    }

    public long getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(long rangeMax) {
        this.rangeMax = rangeMax;
    }

}
