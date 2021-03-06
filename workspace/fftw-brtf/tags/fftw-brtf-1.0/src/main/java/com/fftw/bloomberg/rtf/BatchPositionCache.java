package com.fftw.bloomberg.rtf;

import com.fftw.bloomberg.PositionKey;
import com.fftw.bloomberg.batch.messages.BatchPosition;

import java.util.Map;

/**
 *
 */
public class BatchPositionCache extends GenericCache<PositionKey,BatchPosition> {

    public BatchPositionCache() {

    }

    public BatchPositionCache(Map<PositionKey, BatchPosition> newPositions) {
        super(newPositions);
    }

    /**
     * Add the batch position to the cache.
     *
     * @param bp
     * @return
     */
    public BatchPosition addBatchPosition(BatchPosition bp) {

        PositionKey key = new PositionKey(bp.getSecurityId(), bp.getAccount(), bp.getLevel1TagName(),
                bp.getLevel2TagName(), bp.getLevel3TagName(), bp.getLevel4TagName());

        return addCacheItem(key, bp);
    }

/*
    public BatchPosition updateOnlinePosition(RtfOnlinePosition op) {
        PositionKey key = new PositionKey(op.getSecurityId(), op.getAccount(), op.getLevel1TagName(),
                op.getLevel2TagName(), op.getLevel3TagName(), op.getLevel4TagName());

        BatchPosition bp = null;
        if (cache.containsKey(key)) {
            bp = cache.get(key);

            // Replace the current positions
            bp.setFullCurrentNetPosition(op.getCurrentPosition());
            bp.setFullCurrentNetPositionWithoutComma(op.getCurrentPosition());
            bp.updateCurrentLongPosition(op.getTotalBuyVolume());
            bp.updateCurrentShortPosition(op.getTotalSellVolume());
        } else {
            // Create a new batch position record for new positions
            bp = new BatchPosition(op.getSecurityId(), op.getAccount(), op.getLevel1TagName(),
                    op.getLevel2TagName(), op.getLevel3TagName(), op.getLevel4TagName());
            bp.setFullCurrentNetPosition(op.getCurrentPosition());
            bp.setFullCurrentNetPositionWithoutComma(op.getCurrentPosition());
            bp.updateCurrentLongPosition(op.getTotalBuyVolume());
            bp.updateCurrentShortPosition(op.getTotalSellVolume());

            // additonal fields
            bp.setSecurityIdFlag(op.getSecurityIdFlag());
            bp.setProductCode(op.getProductCode());
        }

        return bp;
    }
*/
}
