package usociety.manager.domain.service.email.impl;

import static java.lang.Boolean.TRUE;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.email.SendAsyncEmailDelegate;
import usociety.manager.domain.service.user.UserService;

@Component
public class SendAsyncEmailDelegateImpl implements SendAsyncEmailDelegate {

    private static final String EMAIL_CONTENT_FORMAT = "<html><body>" +
            "<h3>Hola: %s</h3>" +
            "<p>Nos contaste que te gustan los: %s y acaba nacer un grupo que te puede interesar llamado: <u>%s.</u></p>" +
            "<p>¡Dirígite a <a href='https://usociety-68208.web.app/'>U - Society</a> y échale un vistazo!</p>" +
            "</body></html>";

    private static final String PLURAL_SUFFIX = "s";

    private final UserCategoryRepository userCategoryRepository;
    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public SendAsyncEmailDelegateImpl(UserCategoryRepository userCategoryRepository,
                                      MailService mailService,
                                      UserService userService) {
        this.userCategoryRepository = userCategoryRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    @Async
    @Override
    public void execute(UserApi user, Group group, Category category) throws GenericException {
        List<UserCategory> userCategoryList = userCategoryRepository
                .findAllByCategoryIdAndUserIdIsNot(category.getId(), user.getId());

        for (UserCategory userCategory : userCategoryList) {
            UserApi userApi = userService.getById(userCategory.getUserId());
            mailService.send(userApi.getEmail(), buildHtmlContent(user, group, category), TRUE);
        }
    }

    private String buildHtmlContent(UserApi user, Group group, Category category) {
        String categoryName = StringUtils.capitalize(category.getName());
        return String.format(EMAIL_CONTENT_FORMAT,
                StringUtils.capitalize(user.getName()),
                categoryName.endsWith(PLURAL_SUFFIX) ? categoryName : categoryName + PLURAL_SUFFIX,
                StringUtils.capitalize(group.getName()));
    }

}
