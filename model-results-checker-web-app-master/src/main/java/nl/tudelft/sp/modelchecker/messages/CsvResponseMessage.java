package nl.tudelft.sp.modelchecker.messages;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CsvResponseMessage {
    private String message;
    private String fileDownloadUri;
}
