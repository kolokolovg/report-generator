package reportgenerator.domain;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {"title", "width"})
public class Column {
    private String title;
    private int width;
}
