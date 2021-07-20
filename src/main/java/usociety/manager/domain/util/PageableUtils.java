package usociety.manager.domain.util;

import org.springframework.data.domain.PageRequest;

public class PageableUtils {

    private PageableUtils() {
        super();
    }

    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private static final Integer DEFAULT_PAGE = 0;

    public static PageRequest paginate(int page, int pageSize) {
        if (Integer.signum(page) <= 0) {
            page = DEFAULT_PAGE;
        }
        if (Integer.signum(pageSize) <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return PageRequest.of(page, pageSize);
    }

    public static PageRequest paginate(int page) {
        if (Integer.signum(page) <= 0) {
            page = DEFAULT_PAGE;
        }
        return PageRequest.of(page, DEFAULT_PAGE_SIZE);
    }

}
