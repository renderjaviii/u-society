package usociety.manager.domain.service.common;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class CommonServiceImpl implements CommonService {

    @Autowired
    protected Clock clock;

    public CommonServiceImpl() {
        super();
    }

}
