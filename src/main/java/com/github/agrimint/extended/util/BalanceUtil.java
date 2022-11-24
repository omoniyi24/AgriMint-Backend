package com.github.agrimint.extended.util;

import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.dto.AppUserDTO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */
@Component
public class BalanceUtil {

    private Map<Long, Long> memberBalance;

    public BalanceUtil() {
        this.memberBalance = new HashMap<>();
        this.memberBalance.put(1L, 200L);
    }

    public Map<Long, Long> getMemberBalance() {
        return memberBalance;
    }

    public Long getMemberBalanceById(Long memberId) {
        Long aLong = this.memberBalance.get(memberId);
        if (aLong != null) {
            return aLong;
        } else {
            this.memberBalance.put(memberId, 500L);
            return this.memberBalance.get(memberId);
        }
    }

    public void setMemberBalance(Long memberId, Long newMemberBalance) {
        this.memberBalance.put(memberId, newMemberBalance);
    }
}
