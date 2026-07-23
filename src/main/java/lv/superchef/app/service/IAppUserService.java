package lv.superchef.app.service;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.model.AppUser;


public interface IAppUserService {
    public AppUser register(RegisterRequest registerRequest);


}
