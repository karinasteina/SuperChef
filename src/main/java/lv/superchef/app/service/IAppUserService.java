package lv.superchef.app.service;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Recipe;

import java.util.List;

public interface IAppUserService {
    public AppUser register(RegisterRequest registerRequest);



}
