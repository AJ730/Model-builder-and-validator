package nl.tudelft.sp.modelchecker.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordListDto implements Serializable {
    List<RecordDto> recordDtos;
    Long containerId;
}
