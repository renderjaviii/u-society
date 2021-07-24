package usociety.manager.domain.service.survey;

import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;

public interface SurveyService {

    void validateIfUserHasAlreadyInteracted(String username, Post post) throws GenericException;

    void create(String username, Post post, Integer option) throws GenericException;

}
