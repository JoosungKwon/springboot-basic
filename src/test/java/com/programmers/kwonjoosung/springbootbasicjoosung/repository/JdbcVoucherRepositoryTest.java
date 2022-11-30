package com.programmers.kwonjoosung.springbootbasicjoosung.repository;

import com.programmers.kwonjoosung.springbootbasicjoosung.config.TestDataSourceConfig;
import com.programmers.kwonjoosung.springbootbasicjoosung.exception.DataAlreadyExistException;
import com.programmers.kwonjoosung.springbootbasicjoosung.exception.DataNotExistException;
import com.programmers.kwonjoosung.springbootbasicjoosung.model.voucher.Voucher;
import com.programmers.kwonjoosung.springbootbasicjoosung.model.voucher.VoucherFactory;
import com.programmers.kwonjoosung.springbootbasicjoosung.model.voucher.VoucherType;
import com.programmers.kwonjoosung.springbootbasicjoosung.repository.voucher.JdbcVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
@Import(TestDataSourceConfig.class)
@Transactional
class JdbcVoucherRepositoryTest {

    private JdbcVoucherRepository jdbcVoucherRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.jdbcVoucherRepository = new JdbcVoucherRepository(jdbcTemplate);
    }


    @Test
    @DisplayName("[성공] 바우처를 조회할 수 있다.")
    void findByVoucherIdTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);
        //when
        Optional<Voucher> voucherOptional = jdbcVoucherRepository.findById(voucher.getVoucherId());
        //then
        assertThat(voucherOptional.isPresent()).isTrue();
        assertThat(voucherOptional.get()).isEqualTo(voucher);
    }

    @Test
    @DisplayName("[실패] 없는 바우처는 조회할 수 없다.")
    void findByNotExistVoucherIdTest() {
        //given
        UUID uuid = UUID.randomUUID();
        //when & then
        assertThat(jdbcVoucherRepository.findById(uuid)).isEmpty();
    }

    @Test
    @DisplayName("[성공] 바우처를 저장할 수 있다.")
    void insertVoucherTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        //when
        Voucher insertedVoucher = jdbcVoucherRepository.insert(voucher);
        //then
        assertThat(insertedVoucher).isEqualTo(voucher);
    }

    @Test
    @DisplayName("[성공] id만 다르면 type과 discount가 같은 바우처도 저장할 수 있다.")
    void insertSameTypeAndNameVoucherTest() {
        //given
        Voucher voucher1 = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        Voucher voucher2 = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        //when
        Voucher insertedVoucher1 = jdbcVoucherRepository.insert(voucher1);
        Voucher insertedVoucher2 = jdbcVoucherRepository.insert(voucher2);
        //then
        assertThat(insertedVoucher1).isEqualTo(voucher1);
        assertThat(insertedVoucher2).isEqualTo(voucher2);
    }

    @Test
    @DisplayName("[실패] 같은 바우처id는 저장할 수 없다.")
    void insertSameVoucherTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);
        //when & then
        assertThatThrownBy(() -> jdbcVoucherRepository.insert(voucher))
                .isInstanceOf(DataAlreadyExistException.class);
    }


    @Test
    @DisplayName("[성공] 전체 바우처를 조회할 수 있다.")
    void findAllVoucherTest() {
        //given
        Voucher voucher1 = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        Voucher voucher2 = VoucherFactory.createVoucher(VoucherType.PERCENT, UUID.randomUUID(), 10);
        jdbcVoucherRepository.insert(voucher1);
        jdbcVoucherRepository.insert(voucher2);
        //when
        List<Voucher> voucherList = jdbcVoucherRepository.findAll();
        //then
        assertThat(voucherList).contains(voucher1, voucher2); // 순서를 보장할 필요성?
    }

    @Test
    @DisplayName("[성공] 바우처를 업데이트를 할 수 있다.")
    void updateVoucherTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);
        Voucher newVoucher = VoucherFactory.createVoucher(VoucherType.PERCENT, voucher.getVoucherId(), 10);
        //when
        Voucher updatedVoucher = jdbcVoucherRepository.update(newVoucher);
        //then
        assertThat(updatedVoucher).isEqualTo(newVoucher);
    }

    @Test
    @DisplayName("[실패] 없는 바우처는 업데이트를 할 수 없다.")
    void updateNotExistVoucherTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        //when & then
        assertThatThrownBy(() -> jdbcVoucherRepository.update(voucher))
                .isInstanceOf(DataNotExistException.class);
    }

    @Test
    @DisplayName("[성공] 바우처를 삭제할 수 있다")
    void deleteVoucherTest() {
        //given
        Voucher voucher = VoucherFactory.createVoucher(VoucherType.FIXED, UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);
        //when
        jdbcVoucherRepository.deleteById(voucher.getVoucherId());
        //then
        assertThat(jdbcVoucherRepository.findById(voucher.getVoucherId())).isEmpty();
    }

    @Test
    @DisplayName("[실패] 없는 바우처는 삭제할 수 없다")
    void deleteNotExistVoucherTest() {
        //given
        UUID uuid = UUID.randomUUID();
        //when & then
        assertThatThrownBy(() -> jdbcVoucherRepository.deleteById(uuid))
                .isInstanceOf(DataNotExistException.class);
    }
}