package reportgenerator.domain;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "settings")
@XmlType(propOrder = {"page", "columns"})
public class Settings {
    private Page page;
    private Columns columns;
}