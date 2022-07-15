package com.simplilearn.project.app.sportyshoesecommerceapp.service;

import com.simplilearn.project.app.sportyshoesecommerceapp.model.Setting;
import com.simplilearn.project.app.sportyshoesecommerceapp.repository.SettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class SettingService {
    @Autowired private SettingRepository settingRepository;

    public Setting save(Setting setting){
        return settingRepository.save(setting);
    }
    public Setting getById(long id){
        return settingRepository.getById(id);
    }

    public List<Setting> findAll(){
        return settingRepository.findAll();
    }

    public boolean delete(Long id) {
        try {
            settingRepository.deleteById(id);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
