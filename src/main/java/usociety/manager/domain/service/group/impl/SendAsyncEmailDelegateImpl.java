package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.TRUE;

import java.util.List;

import javax.mail.MessagingException;

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
import usociety.manager.domain.service.group.SendAsyncEmail;
import usociety.manager.domain.service.user.UserService;

@Component
public class SendAsyncEmailDelegateImpl implements SendAsyncEmail {

    private final UserCategoryRepository userCategoryRepository;
    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public SendAsyncEmailDelegateImpl(MailService mailService,
                                      UserCategoryRepository userCategoryRepository,
                                      UserService userService) {
        this.mailService = mailService;
        this.userCategoryRepository = userCategoryRepository;
        this.userService = userService;
    }

    @Async
    @Override
    public void send(UserApi user, Group group, Category category) throws GenericException, MessagingException {
        List<UserCategory> userCategoryList = userCategoryRepository
                .findAllByCategoryIdAndUserIdIsNot(category.getId(), user.getId());

        for (UserCategory userCategory : userCategoryList) {
            UserApi userApi = userService.getById(userCategory.getUserId());
            mailService.send(userApi.getEmail(), buildHtmlContent(user, group, category), TRUE);
        }
    }

    private String buildHtmlContent(UserApi user, Group group, Category category) {
        String categoryName = StringUtils.capitalize(category.getName());
        return String.format("<html><body>" +
                        "<h3>Hola: %s</h3>" +
                        "<p>Nos contaste que te gustan los: %s y acaba nacer un grupo que te puede interesar llamado: <u>%s.</u></p>" +
                        "<p>¡Dirígite a <b>U - Society</b> y échale un vistazo!</p>" +
                        "</body></html>",
                StringUtils.capitalize(user.getName()),
                categoryName.endsWith("s") ? categoryName : categoryName + "s",
                StringUtils.capitalize(group.getName()));
    }

}
