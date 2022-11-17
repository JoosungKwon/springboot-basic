package com.programmers.kwonjoosung.springbootbasicjoosung.repository.voucher;

import com.programmers.kwonjoosung.springbootbasicjoosung.model.voucher.Voucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("dev")
public class MemoryVoucherRepository implements VoucherRepository {

    private static final Logger logger = LoggerFactory.getLogger(MemoryVoucherRepository.class);
    private final Map<UUID, Voucher> storage = new LinkedHashMap<>();

    @Override
    public Voucher insert(Voucher voucher) {
        logger.debug("insert Voucher = {}", voucher);
        return storage.put(voucher.getVoucherId(), voucher);
    }

    @Override
    public int deleteById(UUID voucherId) {
        Voucher removedVoucher = storage.remove(voucherId);
        return removedVoucher == null ? 0 : 1;
    }

    @Override
    public Voucher update(Voucher voucher) {
        return storage.put(voucher.getVoucherId(), voucher);
    }

    @Override
    public Voucher findById(UUID voucherId) {
        return storage.get(voucherId);
    }

    @Override
    public List<Voucher> findAll() {
        logger.debug("findAll Vouchers");
        return new ArrayList<>(storage.values());
    }

}
