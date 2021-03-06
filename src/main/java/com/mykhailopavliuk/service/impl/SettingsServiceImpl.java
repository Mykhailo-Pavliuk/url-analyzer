package com.mykhailopavliuk.service.impl;

import com.mykhailopavliuk.exception.NullEntityReferenceException;
import com.mykhailopavliuk.model.Settings;
import com.mykhailopavliuk.repository.SettingsRepository;
import com.mykhailopavliuk.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;


@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }


    @Override
    public Settings save(Settings settings) {
        if (settings != null) {
            return settingsRepository.save(settings);
        }
        throw new NullEntityReferenceException("Settings cannot be 'null'");
    }

    @Override
    public Settings read() {
        Settings settings = settingsRepository.find();
        if (settings.getExportDirectory().toString().equals("/") || !settings.getExportDirectory().toFile().exists()) {
            settings.setExportDirectory(Path.of(System.getProperty("user.home")).toAbsolutePath());
            settingsRepository.save(settings);
        }
        return settings;
    }
}
