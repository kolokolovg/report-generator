package reportgenerator.services;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import reportgenerator.domain.Settings;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsService {

    public Settings readSettingsFromFile(String settingsFile) throws IOException, JAXBException {
        var data = Files.readString(Paths.get(settingsFile), StandardCharsets.UTF_8);
        var jaxbContext = JAXBContext.newInstance(Settings.class);
        var unmarshaller = jaxbContext.createUnmarshaller();
        var xmlReader = new StringReader(data);
        return (Settings) unmarshaller.unmarshal(xmlReader);
    }
}
