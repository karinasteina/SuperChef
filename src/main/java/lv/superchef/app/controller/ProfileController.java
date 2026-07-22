package lv.superchef.app.controller;

import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProfileController {
    @Autowired
    private IProfileService profileService;



}
