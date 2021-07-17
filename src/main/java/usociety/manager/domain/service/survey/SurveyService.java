package usociety.manager.domain.service.survey;

import usociety.manager.domain.exception.GenericException;

public interface SurveyService {

    void create(String username, Long postId, Integer vote) throws GenericException;

}
