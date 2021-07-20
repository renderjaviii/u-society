package usociety.manager.domain.service.common.impl;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import usociety.manager.domain.service.common.AbstractDelegate;
import usociety.manager.domain.util.mapper.CustomObjectMapper;

@Primary
@Component
public class AbstractDelegateImpl implements AbstractDelegate {

    @Autowired
    protected Clock clock;

    @Autowired
    protected CustomObjectMapper objectMapper;

    protected AbstractDelegateImpl() {
        super();
    }

}
